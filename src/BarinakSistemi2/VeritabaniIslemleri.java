package BarinakSistemi2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList; 
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class VeritabaniIslemleri {
    
    // Proje klasöründe barinak.db dosyası oluşacak
    private static final String URL = "jdbc:sqlite:barinak.db";

    public static Connection baglan() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.err.println("Bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    // Program ilk açıldığında tabloları kontrol eder
    public static void tablolariOlustur() {
        String sqlHayvanlar = "CREATE TABLE IF NOT EXISTS hayvanlar (animalId INTEGER PRIMARY KEY, name TEXT, tur TEXT, yas INTEGER, saglikDurumu TEXT, irk TEXT, durum TEXT);";
        String sqlMusteriler = "CREATE TABLE IF NOT EXISTS musteriler (id INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, customerId TEXT, address TEXT, phone TEXT);";
        String sqlCalisanlar = "CREATE TABLE IF NOT EXISTS calisanlar (id INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, employeeId TEXT, role TEXT, shift TEXT);";

        try (Connection conn = baglan(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlHayvanlar);
            stmt.execute(sqlMusteriler);
            stmt.execute(sqlCalisanlar);
            System.out.println("[SİSTEM] SQLite veritabanı tabloları hazır.");
        } catch (Exception e) {
            System.err.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }

    // Veritabanına Hayvan Ekleme
    public static void hayvanKaydet(Hayvan h) {
        String sql = "INSERT INTO hayvanlar(animalId, name, tur, yas, saglikDurumu, irk, durum) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, h.getAnimalId());
            pstmt.setString(2, h.getName());
            pstmt.setString(3, h.getTur());
            pstmt.setInt(4, h.getYas());
            pstmt.setString(5, h.getSaglikDurumu());
            
            // Abstract sınıf olduğu için Kedi mi Köpek mi kontrol ediyoruz
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

    // Veritabanına Müşteri Ekleme
    public static void musteriKaydet(Musteri m) {
        String sql = "INSERT INTO musteriler(id, name, email, password, customerId, address, phone) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getId());
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

    // Veritabanına Çalışan Ekleme
    public static void calisanKaydet(Calisan c) {
        String sql = "INSERT INTO calisanlar(id, name, email, password, employeeId, role, shift) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = baglan(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getId());
            pstmt.setString(2, c.getName());
            pstmt.setString(3, c.getEmail());
            pstmt.setString(4, c.getPassword());
            pstmt.setString(5, c.getEmployeeId());
            pstmt.setString(6, c.getRole());
            pstmt.setString(7, c.getShift());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Çalışan DB'ye eklenemedi: " + e.getMessage());
        }
    }
 // Veritabanından Hayvanları Çekme
    public static List<Hayvan> hayvanlariGetir() {
        List<Hayvan> liste = new ArrayList<>();
        String sql = "SELECT * FROM hayvanlar";
        try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tur = rs.getString("tur");
                Hayvan h;
                if (tur.equalsIgnoreCase("Kedi") || tur.equalsIgnoreCase("Kedi ")) {
                    h = new Kedi(rs.getInt("animalId"), rs.getString("name"), tur, rs.getInt("yas"), rs.getString("saglikDurumu"), rs.getString("irk"));
                } else {
                    h = new Kopek(rs.getInt("animalId"), rs.getString("name"), tur, rs.getInt("yas"), rs.getString("saglikDurumu"), rs.getString("irk"));
                }
                h.setDurum(rs.getString("durum"));
                liste.add(h);
            }
        } catch (Exception e) {
            System.err.println("Hayvanlar getirilemedi: " + e.getMessage());
        }
        return liste;
    }

    // Veritabanından Müşterileri Çekme
    public static List<Musteri> musterileriGetir() {
        List<Musteri> liste = new ArrayList<>();
        String sql = "SELECT * FROM musteriler";
        try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Musteri m = new Musteri(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("customerId"), rs.getString("address"), rs.getString("phone"));
                liste.add(m);
            }
        } catch (Exception e) {
            System.err.println("Müşteriler getirilemedi: " + e.getMessage());
        }
        return liste;
    }

    // Veritabanından Çalışanları Çekme
    public static List<Calisan> calisanlariGetir() {
        List<Calisan> liste = new ArrayList<>();
        String sql = "SELECT * FROM calisanlar";
        try (Connection conn = baglan(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Calisan c = new Calisan(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("employeeId"), rs.getString("role"), rs.getString("shift"));
                liste.add(c);
            }
        } catch (Exception e) {
            System.err.println("Çalışanlar getirilemedi: " + e.getMessage());
        }
        return liste;
    }
    
    
}