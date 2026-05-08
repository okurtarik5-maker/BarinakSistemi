package BarinakSistemi2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VeritabaniIslemleri {

	private static final String URL = "jdbc:sqlite:barinak.db";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Connection baglan() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL);
		} catch (Exception e) {
			System.err.println("Bağlantı hatası: " + e.getMessage());
		}
		return conn;
	}

	public static void tablolariOlustur() {
		String sqlHayvanlar = "CREATE TABLE IF NOT EXISTS hayvanlar ("
				+ "animalId INTEGER PRIMARY KEY, name TEXT, tur TEXT, "
				+ "yas INTEGER, saglikDurumu TEXT, irk TEXT, durum TEXT);";

		String sqlMusteriler = "CREATE TABLE IF NOT EXISTS musteriler ("
				+ "id INTEGER PRIMARY KEY, name TEXT, email TEXT, "
				+ "password TEXT, customerId TEXT, address TEXT, phone TEXT);";

		// Çalışan tablosuna ek alan (extraInfo): Veteriner için uzmanlık, Bakıcı için bölge
		String sqlCalisanlar = "CREATE TABLE IF NOT EXISTS calisanlar ("
				+ "id INTEGER PRIMARY KEY, name TEXT, email TEXT, "
				+ "password TEXT, employeeId TEXT, role TEXT, shift TEXT, extraInfo TEXT);";

		String sqlBasvurular = "CREATE TABLE IF NOT EXISTS basvurular ("
				+ "appId INTEGER PRIMARY KEY, durum TEXT, tarih TEXT, "
				+ "musteriId INTEGER, hayvanId INTEGER);";

		// Sağlık kayıtları tablosu — her kayıt bir hayvana bağlıdır
		String sqlSaglikKayitlari = "CREATE TABLE IF NOT EXISTS saglik_kayitlari ("
				+ "recordId INTEGER PRIMARY KEY AUTOINCREMENT, animalId INTEGER, "
				+ "tarih TEXT, tani TEXT, asi TEXT, "
				+ "FOREIGN KEY(animalId) REFERENCES hayvanlar(animalId));";

		try (Connection conn = baglan(); Statement stmt = conn.createStatement()) {
			stmt.execute(sqlHayvanlar);
			stmt.execute(sqlMusteriler);
			stmt.execute(sqlCalisanlar);
			stmt.execute(sqlBasvurular);
			stmt.execute(sqlSaglikKayitlari);
			System.out.println("[SİSTEM] SQLite veritabanı tabloları hazır.");
		} catch (Exception e) {
			System.err.println("Tablo oluşturma hatası: " + e.getMessage());
			throw new RuntimeException("Veritabanı oluşturulamadı, sistem güvenli bir şekilde kapatılıyor.", e);
		}
	}

	// ── Hayvan ───────────────────────────────────────────────────────────────────

	public static void hayvanKaydet(Hayvan h) {
		String sql = "INSERT INTO hayvanlar(animalId, name, tur, yas, saglikDurumu, irk, durum) VALUES(?,?,?,?,?,?,?)";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, h.getAnimalId());
			pstmt.setString(2, h.getName());
			pstmt.setString(3, h.getTur());
			pstmt.setInt(4, h.getYas());
			pstmt.setString(5, h.getSaglikDurumu());
			if (h instanceof Kedi) {
				pstmt.setString(6, ((Kedi) h).getIrk());
			} else if (h instanceof Kopek) {
				pstmt.setString(6, ((Kopek) h).getIrk());
			} else {
				pstmt.setString(6, "Bilinmiyor");
			}
			pstmt.setString(7, h.getDurum());
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Hayvan DB'ye eklenemedi: " + e.getMessage());
		}
	}

	public static void hayvanSil(int animalId) {
		// Önce hayvana bağlı sağlık kayıtlarını sil (foreign key ihlali olmasın)
		String sqlSaglik = "DELETE FROM saglik_kayitlari WHERE animalId = ?";
		String sqlHayvan = "DELETE FROM hayvanlar WHERE animalId = ?";

		try (Connection conn = baglan()) {
			try (PreparedStatement pstmt1 = conn.prepareStatement(sqlSaglik)) {
				pstmt1.setInt(1, animalId);
				pstmt1.executeUpdate();
			}
			try (PreparedStatement pstmt2 = conn.prepareStatement(sqlHayvan)) {
				pstmt2.setInt(1, animalId);
				pstmt2.executeUpdate();
			}
		} catch (Exception e) {
			System.err.println("Hayvan DB'den silinemedi: " + e.getMessage());
		}
	}

	public static List<Hayvan> hayvanlariGetir() {
		List<Hayvan> liste = new ArrayList<>();
		String sql = "SELECT * FROM hayvanlar";
		try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				String tur = rs.getString("tur");
				Hayvan h;
				if (tur != null && tur.trim().equalsIgnoreCase("Kedi")) {
					h = new Kedi(rs.getInt("animalId"), rs.getString("name"), tur,
							rs.getInt("yas"), rs.getString("saglikDurumu"), rs.getString("irk"));
				} else {
					h = new Kopek(rs.getInt("animalId"), rs.getString("name"), tur,
							rs.getInt("yas"), rs.getString("saglikDurumu"), rs.getString("irk"));
				}
				h.setDurum(rs.getString("durum"));
				// Sağlık kayıtlarını yükle
				h.setSaglikKayitlari(saglikKayitlariniGetir(h.getAnimalId()));
				liste.add(h);
			}
		} catch (Exception e) {
			System.err.println("Hayvanlar getirilemedi: " + e.getMessage());
		}
		return liste;
	}

	// ── Müşteri ──────────────────────────────────────────────────────────────────

	public static void musteriKaydet(Musteri m) {
		String sql = "INSERT INTO musteriler(id, name, email, password, customerId, address, phone) VALUES(?,?,?,?,?,?,?)";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, m.getId());
			pstmt.setString(2, m.getName());
			pstmt.setString(3, m.getEmail());
			pstmt.setString(4, m.getPassword());
			pstmt.setString(5, m.getCustomerId());
			pstmt.setString(6, m.getAddress());
			pstmt.setString(7, m.getPhone());
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Müşteri DB'ye eklenemedi: " + e.getMessage());
		}
	}

	public static List<Musteri> musterileriGetir() {
		List<Musteri> liste = new ArrayList<>();
		String sql = "SELECT * FROM musteriler";
		try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Musteri m = new Musteri(rs.getLong("id"), rs.getString("name"),
						rs.getString("email"), rs.getString("password"),
						rs.getString("customerId"), rs.getString("address"), rs.getString("phone"));
				liste.add(m);
			}
		} catch (Exception e) {
			System.err.println("Müşteriler getirilemedi: " + e.getMessage());
		}
		return liste;
	}

	// ── Çalışan ──────────────────────────────────────────────────────────────────

	public static void calisanKaydet(Calisan c) {
		String sql = "INSERT INTO calisanlar(id, name, email, password, employeeId, role, shift, extraInfo) VALUES(?,?,?,?,?,?,?,?)";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, c.getId());
			pstmt.setString(2, c.getName());
			pstmt.setString(3, c.getEmail());
			pstmt.setString(4, c.getPassword());
			pstmt.setString(5, c.getEmployeeId());
			pstmt.setString(6, c.getRole());
			pstmt.setString(7, c.getShift());

			// Alt sınıfa özel ek bilgi
			String extraInfo = "";
			if (c instanceof Veteriner) {
				extraInfo = ((Veteriner) c).getUzmanlikAlani();
			} else if (c instanceof Bakici) {
				extraInfo = ((Bakici) c).getSorumluBolge();
			}
			pstmt.setString(8, extraInfo);

			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Çalışan DB'ye eklenemedi: " + e.getMessage());
		}
	}

	public static List<Calisan> calisanlariGetir() {
		List<Calisan> liste = new ArrayList<>();
		String sql = "SELECT * FROM calisanlar";
		try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				String role = rs.getString("role");
				String extraInfo = rs.getString("extraInfo");
				if (extraInfo == null) extraInfo = "";

				Calisan c;
				// Role göre doğru alt sınıfı oluştur
				if (role != null && role.equalsIgnoreCase("Veteriner")) {
					c = new Veteriner(rs.getLong("id"), rs.getString("name"),
							rs.getString("email"), rs.getString("password"),
							rs.getString("employeeId"), rs.getString("shift"), extraInfo);
				} else if (role != null && role.equalsIgnoreCase("Bakici")) {
					c = new Bakici(rs.getLong("id"), rs.getString("name"),
							rs.getString("email"), rs.getString("password"),
							rs.getString("employeeId"), rs.getString("shift"), extraInfo);
				} else {
					// Varsayılan: Yönetici
					c = new Yonetici(rs.getLong("id"), rs.getString("name"),
							rs.getString("email"), rs.getString("password"),
							rs.getString("employeeId"), rs.getString("shift"));
				}
				liste.add(c);
			}
		} catch (Exception e) {
			System.err.println("Çalışanlar getirilemedi: " + e.getMessage());
		}
		return liste;
	}

	// ── Başvuru ──────────────────────────────────────────────────────────────────

	public static void basvuruKaydet(SahiplenmeBasvurusu b) {
		String sql = "INSERT INTO basvurular(appId, durum, tarih, musteriId, hayvanId) VALUES(?,?,?,?,?)";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, b.getAppId());
			pstmt.setString(2, b.getDurum());
			pstmt.setString(3, SDF.format(b.getTarih()));
			pstmt.setLong(4, b.getMusteri().getId());
			pstmt.setInt(5, b.getHayvan().getAnimalId());
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Başvuru DB'ye eklenemedi: " + e.getMessage());
		}
	}

	public static void basvuruDurumGuncelle(int appId, String yeniDurum) {
		String sql = "UPDATE basvurular SET durum = ? WHERE appId = ?";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, yeniDurum);
			pstmt.setInt(2, appId);
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Başvuru durumu güncellenemedi: " + e.getMessage());
		}
	}

	public static List<SahiplenmeBasvurusu> basvurulariGetir(List<Musteri> musteriler, List<Hayvan> hayvanlar) {
		List<SahiplenmeBasvurusu> liste = new ArrayList<>();
		String sql = "SELECT * FROM basvurular";
		try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				long musteriId = rs.getLong("musteriId");
				int hayvanId = rs.getInt("hayvanId");

				Musteri musteri = null;
				for (Musteri m : musteriler) {
					if (m.getId() == musteriId) {
						musteri = m;
						break;
					}
				}

				Hayvan hayvan = null;
				for (Hayvan h : hayvanlar) {
					if (h.getAnimalId() == hayvanId) {
						hayvan = h;
						break;
					}
				}

				String durum = rs.getString("durum");
				Date tarih;
				try {
					tarih = SDF.parse(rs.getString("tarih"));
				} catch (Exception e) {
					tarih = new Date();
				}

				SahiplenmeBasvurusu b = new SahiplenmeBasvurusu(
						rs.getInt("appId"), durum, tarih, musteri, hayvan
				);
				liste.add(b);
			}
		} catch (Exception e) {
			System.err.println("Başvurular getirilemedi: " + e.getMessage());
		}
		return liste;
	}

	public static int sonBasvuruIdGetir() {
		String sql = "SELECT MAX(appId) AS maxId FROM basvurular";
		try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getInt("maxId");
			}
		} catch (Exception e) {
			System.err.println("Son başvuru ID alınamadı: " + e.getMessage());
		}
		return 0;
	}

	// ── Sağlık Kaydı ─────────────────────────────────────────────────────────────

	/**
	 * Yeni sağlık kaydını DB'ye ekler. recordId AUTOINCREMENT olduğu için
	 * SaglikKaydi nesnesindeki recordId DB'deki gerçek ID ile güncellenir.
	 */
	public static void saglikKaydiEkle(SaglikKaydi sk) {
		String sql = "INSERT INTO saglik_kayitlari(animalId, tarih, tani, asi) VALUES(?,?,?,?)";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, sk.getAnimalId());
			pstmt.setString(2, SDF.format(sk.getTarih()));
			pstmt.setString(3, sk.getTani());
			pstmt.setString(4, sk.getAsi());
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.err.println("Sağlık kaydı DB'ye eklenemedi: " + e.getMessage());
		}
	}

	/**
	 * Belirli bir hayvana ait tüm sağlık kayıtlarını getirir.
	 */
	public static List<SaglikKaydi> saglikKayitlariniGetir(int animalId) {
		List<SaglikKaydi> liste = new ArrayList<>();
		String sql = "SELECT * FROM saglik_kayitlari WHERE animalId = ?";
		try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, animalId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Date tarih;
					try {
						tarih = SDF.parse(rs.getString("tarih"));
					} catch (Exception e) {
						tarih = new Date();
					}
					SaglikKaydi sk = new SaglikKaydi(
							rs.getInt("recordId"),
							rs.getInt("animalId"),
							tarih,
							rs.getString("tani"),
							rs.getString("asi")
					);
					liste.add(sk);
				}
			}
		} catch (Exception e) {
			System.err.println("Sağlık kayıtları getirilemedi: " + e.getMessage());
		}
		return liste;
	}
}
