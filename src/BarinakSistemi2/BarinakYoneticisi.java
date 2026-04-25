package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Akıllı Barınak Sistemi
 * Barınağın tüm operasyonlarını koordine eden yönetici (Facade) sınıfı.
 */
public class BarinakYoneticisi {

    private List<Hayvan> hayvanlar;
    private List<Calisan> calisanlar;
    private List<Musteri> musteriler;
    private List<SahiplenmeBasvurusu> basvurular;

    // ── Singleton ────────────────────────────────────────────────────────────────

    private static BarinakYoneticisi instance;

    public static BarinakYoneticisi getInstance() {
        if (instance == null) {
            instance = new BarinakYoneticisi();
        }
        return instance;
    }

    private BarinakYoneticisi() {
        // Sistem açılır açılmaz veritabanındaki kayıtlar listelere doldurulur
        this.hayvanlar = VeritabaniIslemleri.hayvanlariGetir();
        this.calisanlar = VeritabaniIslemleri.calisanlariGetir();
        this.musteriler = VeritabaniIslemleri.musterileriGetir();

        // Başvurular için veritabanı tablosu yapmadığımızdan boş başlatıyoruz
        this.basvurular = new ArrayList<>();
    }

    // ── Hayvan Yönetimi ──────────────────────────────────────────────────────────

    public void hayvanEkle(Hayvan hayvan) {
        if (hayvan == null) {
            System.out.println("Hata: Eklenecek hayvan null olamaz.");
            return;
        }
        for (Hayvan h : hayvanlar) {
            if (h.getAnimalId() == hayvan.getAnimalId()) {
                System.out.println("Hata: #" + hayvan.getAnimalId() + " ID'li hayvan zaten kayıtlı.");
                return;
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

    public Hayvan hayvanBul(int animalId) {
        for (Hayvan h : hayvanlar) {
            if (h.getAnimalId() == animalId) {
                return h;
            }
        }
        System.out.println("Uyarı: #" + animalId + " ID'li hayvan bulunamadı.");
        return null;
    }

    // ── Başvuru Yönetimi ─────────────────────────────────────────────────────────

    public void basvuruKaydet(SahiplenmeBasvurusu basvuru) {
        if (basvuru == null) {
            System.out.println("Hata: Başvuru null olamaz.");
            return;
        }
        for (SahiplenmeBasvurusu b : basvurular) {
            boolean ayniMusteri = b.getMusteri() != null
                    && basvuru.getMusteri() != null
                    && b.getMusteri().getCustomerId()
                            .equals(basvuru.getMusteri().getCustomerId());
            boolean ayniHayvan = b.getHayvan() != null
                    && basvuru.getHayvan() != null
                    && b.getHayvan().getAnimalId() == basvuru.getHayvan().getAnimalId();
            if (ayniMusteri && ayniHayvan) {
                System.out.println("Hata: Bu müşteri bu hayvana zaten başvurmuş.");
                return;
            }
        }
        basvurular.add(basvuru);
        System.out.println("[KAYIT] Başvuru #" + basvuru.getAppId() + " sisteme eklendi.");
    }

    public List<SahiplenmeBasvurusu> bekleyenBasvurular() {
        return basvurular.stream()
                .filter(b -> b.getDurum().equals("BEKLEMEDE"))
                .collect(Collectors.toList());
    }

    public void basvuruOnayla(SahiplenmeBasvurusu basvuru, Calisan calisan) {
        if (calisan == null) {
            System.out.println("Hata: İşlemi yapacak çalışan belirtilmedi.");
            return;
        }
        if (!calisan.getRole().equalsIgnoreCase("yönetici")
                && !calisan.getRole().equalsIgnoreCase("yoneticisi")) {
            System.out.println("Hata: " + calisan.getName() + " adlı çalışanın onaylama yetkisi yok.");
            return;
        }
        basvuru.onayla();
    }

    public void basvuruReddet(SahiplenmeBasvurusu basvuru, Calisan calisan) {
        if (calisan == null) {
            System.out.println("Hata: İşlemi yapacak çalışan belirtilmedi.");
            return;
        }
        if (!calisan.getRole().equalsIgnoreCase("yönetici")
                && !calisan.getRole().equalsIgnoreCase("yoneticisi")) {
            System.out.println("Hata: " + calisan.getName() + " adlı çalışanın reddetme yetkisi yok.");
            return;
        }
        basvuru.reddet();
    }

    // ── Çalışan Yönetimi ─────────────────────────────────────────────────────────

    public void calisanEkle(Calisan calisan) {
        if (calisan == null) {
            System.out.println("Hata: Çalışan null olamaz.");
            return;
        }
        for (Calisan c : calisanlar) {
            if (c.getEmployeeId().equals(calisan.getEmployeeId())) {
                System.out.println("Hata: " + calisan.getEmployeeId() + " ID'li çalışan zaten kayıtlı.");
                return;
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

    public void musteriEkle(Musteri musteri) {
        if (musteri == null) {
            System.out.println("Hata: Müşteri null olamaz.");
            return;
        }
        for (Musteri m : musteriler) {
            if (m.getCustomerId().equals(musteri.getCustomerId())) {
                System.out.println("Hata: " + musteri.getCustomerId() + " ID'li müşteri zaten kayıtlı.");
                return;
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

    public List<Hayvan> getHayvanlar() {
        return new ArrayList<>(hayvanlar);
    }

    public List<Calisan> getCalisanlar() {
        return new ArrayList<>(calisanlar);
    }

    public List<Musteri> getMusteriler() {
        return new ArrayList<>(musteriler);
    }

    public List<SahiplenmeBasvurusu> getBasvurular() {
        return new ArrayList<>(basvurular);
    }

    public void musteriListeme() {

        System.out.println("Müşteriler listeleniyor");

        if (musteriler.isEmpty()) {
            System.out.println("Kayıtlı müşteri yok");
        } else {
            for (Musteri m : musteriler) {
                System.out.printf("Id: %d İsim: %s Mail: %s Çalışan Id: %s Adres: %s Telefon numarası: %s\n", m.getId(),
                        m.getName(), m.getEmail(), m.getCustomerId(), m.getAddress(), m.getPhone());
            }
        }
    }
}
