package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BarinakYoneticisi {

	private List<Hayvan>               hayvanlar;
	private List<Calisan>              calisanlar;
	private List<Musteri>              musteriler;
	private List<SahiplenmeBasvurusu>  basvurular;

	private static BarinakYoneticisi instance;

	public static BarinakYoneticisi getInstance() {
		if (instance == null) {
			instance = new BarinakYoneticisi();
		}
		return instance;
	}

	private BarinakYoneticisi() {
		this.hayvanlar  = VeritabaniIslemleri.hayvanlariGetir();
		this.calisanlar = VeritabaniIslemleri.calisanlariGetir();
		this.musteriler = VeritabaniIslemleri.musterileriGetir();
		this.basvurular = VeritabaniIslemleri.basvurulariGetir(musteriler, hayvanlar);
	}

	// ── Hayvan Yönetimi ──────────────────────────────────────────────────────────

	/**
	 * Hayvan ekler. Sadece yetkili çalışan (Yönetici) bu işlemi yapabilir.
	 */
	public void hayvanEkle(Hayvan hayvan, Calisan calisan) throws BarinakIstisnasi {
		if (calisan == null) {
			throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
		}
		// Yetki kontrolü → Yönetici dışı çalışanlar istisna fırlatır
		calisan.hayvanEkleYetkisi();

		if (hayvan == null) {
			throw new BarinakIstisnasi("Eklenecek hayvan null olamaz.");
		}
		for (Hayvan h : hayvanlar) {
			if (h.getAnimalId() == hayvan.getAnimalId()) {
				throw new BarinakIstisnasi("#" + hayvan.getAnimalId() + " ID'li hayvan zaten kayıtlı.");
			}
		}
		hayvanlar.add(hayvan);
		VeritabaniIslemleri.hayvanKaydet(hayvan);
		System.out.println("[EKLENDI] " + hayvan.getName() + " barınağa kaydedildi.");
	}

	public List<Hayvan> tumHayvanlariListele() {
		return new ArrayList<>(hayvanlar);
	}

	public List<Hayvan> hayvanlariFiltrele(String tur, int yas) {
		List<Hayvan> sonuc = new ArrayList<>(hayvanlar);
		if (tur != null && !tur.isEmpty()) {
			sonuc = sonuc.stream()
					.filter(h -> h.getTur().equalsIgnoreCase(tur))
					.collect(Collectors.toList());
		}
		if (yas >= 0) {
			sonuc = sonuc.stream()
					.filter(h -> h.getYas() <= yas)
					.collect(Collectors.toList());
		}
		return sonuc;
	}

	public Hayvan hayvanBul(int animalId) throws BarinakIstisnasi {
		for (Hayvan h : hayvanlar) {
			if (h.getAnimalId() == animalId) {
				return h;
			}
		}
		throw new BarinakIstisnasi("#" + animalId + " ID'li hayvan bulunamadı.");
	}

	// ── Sağlık Kaydı ─────────────────────────────────────────────────────────────

	/**
	 * Hayvana sağlık kaydı ekler. Sadece Veteriner bu işlemi yapabilir.
	 */
	public void saglikKaydiEkle(SaglikKaydi kayit, Hayvan hayvan, Calisan calisan) throws BarinakIstisnasi {
		if (calisan == null) {
			throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
		}
		// Yetki kontrolü → sadece Veteriner geçer
		calisan.saglikKaydiYetkisi();

		if (hayvan == null) {
			throw new BarinakIstisnasi("Sağlık kaydı eklenecek hayvan null olamaz.");
		}
		if (kayit == null) {
			throw new BarinakIstisnasi("Sağlık kaydı null olamaz.");
		}

		hayvan.saglikKaydiEkle(kayit);
		VeritabaniIslemleri.saglikKaydiEkle(kayit);
		System.out.println("[KAYIT] " + hayvan.getName() + " için sağlık kaydı eklendi.");
	}

	// ── Başvuru Yönetimi ─────────────────────────────────────────────────────────

	public void basvuruKaydet(SahiplenmeBasvurusu basvuru) throws BarinakIstisnasi {
        if (basvuru == null) {
            throw new BarinakIstisnasi("Başvuru null olamaz.");
        }

        for (SahiplenmeBasvurusu b : basvurular) {
            // 1. KONTROL: Hayvan için zaten bekleyen bir başvuru var mı? (Mevcut kontrol)
            if (b.getHayvan() != null
                    && basvuru.getHayvan() != null
                    && b.getHayvan().getAnimalId() == basvuru.getHayvan().getAnimalId()
                    && b.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
                throw new BarinakIstisnasi(
                        basvuru.getHayvan().getName() + " isimli hayvan için zaten beklemede olan bir başvuru var!");
            }

            // 2. KONTROL (YENİ): Bu müşteri bu hayvan için daha önce reddedildi mi?
            if (b.getMusteri() != null && b.getHayvan() != null
                    && b.getMusteri().getId() == basvuru.getMusteri().getId()
                    && b.getHayvan().getAnimalId() == basvuru.getHayvan().getAnimalId()
                    && b.getDurum().equalsIgnoreCase("Reddedildi")) {
                throw new BarinakIstisnasi("Bu hayvan için yaptığınız önceki başvuru reddedilmiştir. "
                        + "Aynı hayvana tekrar başvuru yapamazsınız.");
            }
        }

        basvurular.add(basvuru);
        VeritabaniIslemleri.basvuruKaydet(basvuru);
        System.out.println("[KAYIT] Başvuru #" + basvuru.getAppId() + " sisteme eklendi.");
    }

	public List<SahiplenmeBasvurusu> bekleyenBasvurular() {
		return basvurular.stream()
				.filter(b -> b.getDurum().equalsIgnoreCase("BEKLEMEDE"))
				.collect(Collectors.toList());
	}

	public List<SahiplenmeBasvurusu> tumBasvurular() {
		return new ArrayList<>(basvurular);
	}

	public SahiplenmeBasvurusu basvuruBul(int appId) throws BarinakIstisnasi {
		for (SahiplenmeBasvurusu b : basvurular) {
			if (b.getAppId() == appId) {
				return b;
			}
		}
		throw new BarinakIstisnasi("#" + appId + " numaralı başvuru bulunamadı.");
	}

	/**
	 * Başvuruyu onaylar. Sadece Yönetici bu işlemi yapabilir.
	 * Onaylanan hayvan barınaktan ve veritabanından silinir.
	 */
	public void basvuruOnayla(SahiplenmeBasvurusu basvuru, Calisan calisan) throws BarinakIstisnasi {
		if (calisan == null) {
			throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
		}
		// Yetki kontrolü → sadece Yönetici geçer
		calisan.basvuruIslemYetkisi();

		if (basvuru == null) {
			throw new BarinakIstisnasi("Onaylanacak başvuru belirtilmedi.");
		}
		if (!basvuru.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
			throw new BarinakIstisnasi("Bu başvuru zaten işlenmiş. Durumu: " + basvuru.getDurum());
		}

		basvuru.onayla();
		VeritabaniIslemleri.basvuruDurumGuncelle(basvuru.getAppId(), "Onaylandı");

		Hayvan sahiplenilenHayvan = basvuru.getHayvan();
		if (sahiplenilenHayvan != null) {
			hayvanlar.remove(sahiplenilenHayvan);
			VeritabaniIslemleri.hayvanSil(sahiplenilenHayvan.getAnimalId());
			System.out.println("[SİSTEM] " + sahiplenilenHayvan.getName()
					+ " barınaktan çıkarıldı ve " + basvuru.getMusteri().getName() + " ile eşleştirildi.");
		}
	}

	/**
	 * Başvuruyu reddeder. Sadece Yönetici bu işlemi yapabilir.
	 */
	public void basvuruReddet(SahiplenmeBasvurusu basvuru, Calisan calisan) throws BarinakIstisnasi {
		if (calisan == null) {
			throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
		}
		// Yetki kontrolü → sadece Yönetici geçer
		calisan.basvuruIslemYetkisi();

		if (basvuru == null) {
			throw new BarinakIstisnasi("Reddedilecek başvuru belirtilmedi.");
		}
		if (!basvuru.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
			throw new BarinakIstisnasi("Bu başvuru zaten işlenmiş. Durumu: " + basvuru.getDurum());
		}

		basvuru.reddet();
		VeritabaniIslemleri.basvuruDurumGuncelle(basvuru.getAppId(), "Reddedildi");
	}

	// ── Çalışan Yönetimi ─────────────────────────────────────────────────────────

	public void calisanEkle(Calisan calisan) throws BarinakIstisnasi {
		if (calisan == null) {
			throw new BarinakIstisnasi("Çalışan null olamaz.");
		}
		for (Calisan c : calisanlar) {
			if (c.getEmployeeId().equals(calisan.getEmployeeId())) {
				throw new BarinakIstisnasi(calisan.getEmployeeId() + " ID'li çalışan zaten kayıtlı.");
			}
		}
		calisanlar.add(calisan);
		VeritabaniIslemleri.calisanKaydet(calisan);
		System.out.println("[EKLENDI] " + calisan.getRole() + " " + calisan.getName() + " kayıt edildi.");
	}

	public List<Calisan> tumCalisanlariListele() {
		return new ArrayList<>(calisanlar);
	}

	// ── Müşteri Yönetimi ─────────────────────────────────────────────────────────

	public void musteriEkle(Musteri musteri) throws BarinakIstisnasi {
		if (musteri == null) {
			throw new BarinakIstisnasi("Müşteri null olamaz.");
		}
		for (Musteri m : musteriler) {
			if (m.getCustomerId().equals(musteri.getCustomerId())) {
				throw new BarinakIstisnasi(musteri.getCustomerId() + " ID'li müşteri zaten kayıtlı.");
			}
		}
		musteriler.add(musteri);
		VeritabaniIslemleri.musteriKaydet(musteri);
		System.out.println("[EKLENDI] Müşteri " + musteri.getName() + " kayıt edildi.");
	}

	public List<Musteri> tumMusterileriListele() {
		return new ArrayList<>(musteriler);
	}

	// ── Raporlama ────────────────────────────────────────────────────────────────

	public void durumRaporuYazdir() {
		System.out.println("========== BARINAK DURUM RAPORU ==========");
		System.out.println("Toplam Hayvan Sayısı   : " + hayvanlar.size());
		System.out.println("Bekleyen Başvuru Sayısı: " + bekleyenBasvurular().size());
		System.out.println("Toplam Başvuru Sayısı  : " + basvurular.size());
		System.out.println("Aktif Çalışan Sayısı   : " + calisanlar.size());
		System.out.println("Kayıtlı Müşteri Sayısı : " + musteriler.size());
		System.out.println("==========================================");
	}

	// ── Getters ──────────────────────────────────────────────────────────────────

	public List<Hayvan>              getHayvanlar()  { return new ArrayList<>(hayvanlar); }
	public List<Calisan>             getCalisanlar() { return new ArrayList<>(calisanlar); }
	public List<Musteri>             getMusteriler() { return new ArrayList<>(musteriler); }
	public List<SahiplenmeBasvurusu> getBasvurular() { return new ArrayList<>(basvurular); }
}
