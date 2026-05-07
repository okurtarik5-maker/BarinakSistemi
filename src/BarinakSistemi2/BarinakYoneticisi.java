package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Akıllı Barınak Sistemi
 * Barınağın tüm operasyonlarını koordine eden yönetici (Facade) sınıfı.
 */
public class BarinakYoneticisi {

    private List<Hayvan>               hayvanlar;
    private List<Calisan>              calisanlar;
    private List<Musteri>              musteriler;
    private List<SahiplenmeBasvurusu>  basvurular;

    // ── Singleton ────────────────────────────────────────────────────────────────

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
        // Başvurular artık DB'den yükleniyor (sistem kapansa bile kaybolmaz)
        this.basvurular = VeritabaniIslemleri.basvurulariGetir(musteriler, hayvanlar);
    }

    // ── Hayvan Yönetimi ──────────────────────────────────────────────────────────

    public void hayvanEkle(Hayvan hayvan) throws BarinakIstisnasi {
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

    // ── Başvuru Yönetimi ─────────────────────────────────────────────────────────

    /**
     * Yeni başvuru kaydeder.
     * Aynı hayvana BEKLEMEDE durumunda bir başvuru varsa tekrar başvuru engellenir.
     */
    public void basvuruKaydet(SahiplenmeBasvurusu basvuru) throws BarinakIstisnasi {
        if (basvuru == null) {
            throw new BarinakIstisnasi("Başvuru null olamaz.");
        }

        // Aynı hayvana zaten BEKLEMEDE durumunda başvuru var mı?
        for (SahiplenmeBasvurusu b : basvurular) {
            if (b.getHayvan() != null
                    && basvuru.getHayvan() != null
                    && b.getHayvan().getAnimalId() == basvuru.getHayvan().getAnimalId()
                    && b.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
                throw new BarinakIstisnasi(
                        basvuru.getHayvan().getName() + " isimli hayvan için zaten beklemede olan bir başvuru var!");
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

    /**
     * Başvuruyu ID ile bulur.
     */
    public SahiplenmeBasvurusu basvuruBul(int appId) throws BarinakIstisnasi {
        for (SahiplenmeBasvurusu b : basvurular) {
            if (b.getAppId() == appId) {
                return b;
            }
        }
        throw new BarinakIstisnasi("#" + appId + " numaralı başvuru bulunamadı.");
    }

    /**
     * Yetkili çalışan aracılığıyla başvuruyu onaylar.
     * Onaylanan hayvan barınak listesinden ve veritabanından silinir.
     */
    public void basvuruOnayla(SahiplenmeBasvurusu basvuru, Calisan calisan) throws BarinakIstisnasi {
        if (calisan == null) {
            throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
        }
        if (basvuru == null) {
            throw new BarinakIstisnasi("Onaylanacak başvuru belirtilmedi.");
        }
        if (!basvuru.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
            throw new BarinakIstisnasi("Bu başvuru zaten işlenmiş. Durumu: " + basvuru.getDurum());
        }
        if (!calisan.getRole().equalsIgnoreCase("yönetici")
                && !calisan.getRole().equalsIgnoreCase("yonetici")) {
            throw new BarinakIstisnasi(calisan.getName() + " adlı çalışanın onaylama yetkisi yok.");
        }

        // Başvuru durumunu güncelle
        basvuru.onayla();
        VeritabaniIslemleri.basvuruDurumGuncelle(basvuru.getAppId(), "Onaylandı");

        // Hayvanı barınaktan çıkar (liste + DB)
        Hayvan sahiplenilenHayvan = basvuru.getHayvan();
        if (sahiplenilenHayvan != null) {
            hayvanlar.remove(sahiplenilenHayvan);
            VeritabaniIslemleri.hayvanSil(sahiplenilenHayvan.getAnimalId());
            System.out.println("[SİSTEM] " + sahiplenilenHayvan.getName()
                    + " barınaktan çıkarıldı ve " + basvuru.getMusteri().getName() + " ile eşleştirildi.");
        }
    }

    /**
     * Yetkili çalışan aracılığıyla başvuruyu reddeder.
     */
    public void basvuruReddet(SahiplenmeBasvurusu basvuru, Calisan calisan) throws BarinakIstisnasi {
        if (calisan == null) {
            throw new BarinakIstisnasi("İşlemi yapacak çalışan belirtilmedi.");
        }
        if (basvuru == null) {
            throw new BarinakIstisnasi("Reddedilecek başvuru belirtilmedi.");
        }
        if (!basvuru.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
            throw new BarinakIstisnasi("Bu başvuru zaten işlenmiş. Durumu: " + basvuru.getDurum());
        }
        if (!calisan.getRole().equalsIgnoreCase("yönetici")
                && !calisan.getRole().equalsIgnoreCase("yonetici")) {
            throw new BarinakIstisnasi(calisan.getName() + " adlı çalışanın reddetme yetkisi yok.");
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
        System.out.println("[EKLENDI] Çalışan " + calisan.getName() + " kayıt edildi.");
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
