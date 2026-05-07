package BarinakSistemi2;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MenuIslemleri {

    // Sayaç artık DB'deki en büyük appId üzerinden başlatılıyor
    public static int basvuruSayaci = VeritabaniIslemleri.sonBasvuruIdGetir();

    // ── Hayvan Sahiplenme ────────────────────────────────────────────────────────

    public static void hayvanSahiplen(Scanner scan, BarinakYoneticisi yonetici) throws Exception {

        System.out.println("\n--- YENİ HAYVAN SAHİPLENME KAYDI ---\n");

        if (yonetici.tumMusterileriListele().isEmpty()) {
            throw new BarinakIstisnasi("Sistemde kayıtlı müşteri yok.");
        }

        System.out.print("Adınız: ");
        String ad = scan.nextLine();

        if (ad == null || ad.trim().isEmpty()) {
            throw new BarinakIstisnasi("Müşteri ismi boş bırakılamaz!");
        }

        Musteri aktifMusteri = null;
        for (Musteri m : yonetici.tumMusterileriListele()) {
            if (m.getName().equalsIgnoreCase(ad.trim())) {
                aktifMusteri = m;
                break;
            }
        }

        if (aktifMusteri == null) {
            throw new BarinakIstisnasi("'" + ad + "' isimli müşteri sistemde bulunamadı.");
        }

        if (yonetici.tumHayvanlariListele().isEmpty()) {
            throw new BarinakIstisnasi("Barınakta sahiplenilebilecek hayvan bulunmamakta.");
        }

        System.out.println("\n--- Sahiplenilebilecek Hayvanlar ---");
        for (Hayvan h : yonetici.tumHayvanlariListele()) {
            System.out.println("- " + h.getName() + " | Durumu: " + h.getSaglikDurumu()
                    + " | ID: " + h.getAnimalId());
        }

        System.out.print("\nSahiplenmek istediğiniz hayvanın ID numarası: ");
        int arananId = scan.nextInt();
        scan.nextLine();

        Hayvan secilenHayvan = null;
        for (Hayvan h : yonetici.tumHayvanlariListele()) {
            if (h.getAnimalId() == arananId) {
                secilenHayvan = h;
                break;
            }
        }

        if (secilenHayvan == null) {
            throw new BarinakIstisnasi("Bu ID numarasına sahip bir hayvan bulunamadı!");
        }

        basvuruSayaci++;
        SahiplenmeBasvurusu yeniBasvuru = new SahiplenmeBasvurusu(
                basvuruSayaci, "BEKLEMEDE", new Date(), aktifMusteri, secilenHayvan
        );
        aktifMusteri.basvuruYap();
        yonetici.basvuruKaydet(yeniBasvuru);
    }

    // ── Başvuru Yönetimi (Onayla / Reddet) ───────────────────────────────────────

    public static void basvuruYonet(Scanner scan, BarinakYoneticisi yonetici) throws Exception {

        System.out.println("\n--- BAŞVURU YÖNETİMİ ---\n");

        // Çalışan doğrulama
        if (yonetici.tumCalisanlariListele().isEmpty()) {
            throw new BarinakIstisnasi("Sistemde kayıtlı çalışan yok.");
        }

        System.out.print("Yönetici çalışan ID numarası: ");
        String calisanId = scan.nextLine().trim();

        if (calisanId.isEmpty()) {
            throw new BarinakIstisnasi("Çalışan ID boş olamaz.");
        }

        Calisan yetkiliCalisan = null;
        for (Calisan c : yonetici.tumCalisanlariListele()) {
            if (c.getEmployeeId().equals(calisanId)) {
                yetkiliCalisan = c;
                break;
            }
        }

        if (yetkiliCalisan == null) {
            throw new BarinakIstisnasi("'" + calisanId + "' ID'li çalışan bulunamadı.");
        }

        if (!yetkiliCalisan.getRole().equalsIgnoreCase("yönetici")
                && !yetkiliCalisan.getRole().equalsIgnoreCase("yonetici")) {
            throw new BarinakIstisnasi(yetkiliCalisan.getName()
                    + " adlı çalışanın başvuru yönetme yetkisi yok. Rol: " + yetkiliCalisan.getRole());
        }

        // Bekleyen başvuruları listele
        List<SahiplenmeBasvurusu> bekleyenler = yonetici.bekleyenBasvurular();

        if (bekleyenler.isEmpty()) {
            System.out.println("Beklemede olan başvuru bulunmamaktadır.");
            return;
        }

        System.out.println("\n--- Bekleyen Başvurular ---");
        for (SahiplenmeBasvurusu b : bekleyenler) {
            String musteriAdi = (b.getMusteri() != null) ? b.getMusteri().getName() : "Bilinmiyor";
            String hayvanAdi = (b.getHayvan() != null) ? b.getHayvan().getName() : "Bilinmiyor";
            int hayvanId = (b.getHayvan() != null) ? b.getHayvan().getAnimalId() : -1;

            System.out.println("  Başvuru #" + b.getAppId()
                    + " | Müşteri: " + musteriAdi
                    + " | Hayvan: " + hayvanAdi + " (ID: " + hayvanId + ")"
                    + " | Tarih: " + b.getTarih());
        }

        System.out.print("\nİşlem yapmak istediğiniz başvuru numarası: ");
        int secilen = scan.nextInt();
        scan.nextLine();

        SahiplenmeBasvurusu hedefBasvuru = yonetici.basvuruBul(secilen);

        if (!hedefBasvuru.getDurum().equalsIgnoreCase("BEKLEMEDE")) {
            throw new BarinakIstisnasi("Bu başvuru zaten işlenmiş. Durumu: " + hedefBasvuru.getDurum());
        }

        System.out.println("\n1-) Onayla");
        System.out.println("2-) Reddet");
        System.out.print("Seçiminiz: ");

        int islem = scan.nextInt();
        scan.nextLine();

        switch (islem) {
            case 1:
                yonetici.basvuruOnayla(hedefBasvuru, yetkiliCalisan);
                break;
            case 2:
                yonetici.basvuruReddet(hedefBasvuru, yetkiliCalisan);
                break;
            default:
                throw new BarinakIstisnasi("Geçersiz seçim! 1 (Onayla) veya 2 (Reddet) giriniz.");
        }
    }

    // ── Müşteri Ekleme ───────────────────────────────────────────────────────────

    public static void musteriEkle(Scanner scan, BarinakYoneticisi yonetici) throws Exception {

        System.out.println("\n--- YENİ MÜŞTERİ KAYDI ---\n");

        System.out.print("Müşteri TC numarası: ");
        long id = scan.nextLong();
        scan.nextLine();

        tcDogrula(id);

        for (Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
            if (mevcutMusteri.getId() == id) {
                throw new BarinakIstisnasi(id + " numarasına sahip müşteri zaten bulunmakta!");
            }
        }

        System.out.print("Müşteri ismi: ");
        String isim = scan.nextLine();
        if (isim == null || isim.trim().isEmpty()) {
            throw new BarinakIstisnasi("Müşteri ismi boş bırakılamaz!");
        }

        System.out.print("Müşteri maili: ");
        String email = scan.nextLine();
        if (email == null || email.trim().isEmpty()) {
            throw new BarinakIstisnasi("E-posta adresi boş bırakılamaz!");
        }
        for (Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
            if (mevcutMusteri.getEmail().equalsIgnoreCase(email)) {
                throw new BarinakIstisnasi(email + " mailine sahip müşteri zaten bulunmakta!");
            }
        }

        System.out.print("Müşteri şifresi: ");
        String password = scan.nextLine();
        if (password == null || password.trim().isEmpty()) {
            throw new BarinakIstisnasi("Şifre boş bırakılamaz!");
        }

        System.out.print("Müşterinin müşteri ID numarası: ");
        String customerId = scan.nextLine();
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new BarinakIstisnasi("Müşteri kodu boş bırakılamaz!");
        }
        for (Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
            if (mevcutMusteri.getCustomerId().equalsIgnoreCase(customerId)) {
                throw new BarinakIstisnasi(customerId + " ID numarasına sahip müşteri zaten bulunmakta!");
            }
        }

        System.out.print("Müşteri adresi: ");
        String address = scan.nextLine();

        System.out.print("Müşteri telefon numarası: ");
        String phone = scan.nextLine();
        if (!phone.matches("^[0-9]{10,11}$")) {
            throw new BarinakIstisnasi("Telefon numarası sadece rakamlardan oluşmalı ve 10-11 haneli olmalıdır!");
        }
        for (Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
            if (mevcutMusteri.getPhone().equals(phone)) {
                throw new BarinakIstisnasi(phone + " telefon numarasına sahip müşteri zaten bulunmakta!");
            }
        }

        Musteri yeniKayit = new Musteri(id, isim, email, password, customerId, address, phone);
        yonetici.musteriEkle(yeniKayit);
    }

    // ── Çalışan Ekleme ───────────────────────────────────────────────────────────

    public static void calisanEkle(Scanner scan, BarinakYoneticisi yonetici) throws Exception {

        System.out.println("\n--- YENİ ÇALIŞAN KAYDI ---\n");

        System.out.print("Çalışan TC numarası: ");
        long id = scan.nextLong();
        scan.nextLine();

        tcDogrula(id);

        for (Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
            if (mevcutCalisan.getId() == id) {
                throw new BarinakIstisnasi(id + " TC numaralı çalışan zaten bulunmakta!");
            }
        }

        System.out.print("Çalışan ismi: ");
        String isim = scan.nextLine();
        if (isim == null || isim.trim().isEmpty()) {
            throw new BarinakIstisnasi("Çalışan ismi boş bırakılamaz!");
        }

        System.out.print("Çalışan maili: ");
        String email = scan.nextLine();
        if (email == null || email.trim().isEmpty()) {
            throw new BarinakIstisnasi("E-posta adresi boş bırakılamaz!");
        }
        for (Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
            if (mevcutCalisan.getEmail().equalsIgnoreCase(email)) {
                throw new BarinakIstisnasi(email + " mailine sahip çalışan zaten bulunmakta!");
            }
        }

        System.out.print("Çalışan şifresi: ");
        String password = scan.nextLine();
        if (password == null || password.trim().isEmpty()) {
            throw new BarinakIstisnasi("Şifre boş bırakılamaz!");
        }

        System.out.print("Çalışanın çalışan Id numarası: ");
        String employeeId = scan.nextLine();
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new BarinakIstisnasi("Çalışan kodu boş bırakılamaz!");
        }
        for (Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
            if (mevcutCalisan.getEmployeeId().equalsIgnoreCase(employeeId)) {
                throw new BarinakIstisnasi(employeeId + " ID numarasına sahip çalışan zaten bulunmakta!");
            }
        }

        System.out.print("Çalışanın rolü: ");
        String role = scan.nextLine();
        if (role == null || role.trim().isEmpty()) {
            throw new BarinakIstisnasi("Rol boş bırakılamaz!");
        }

        System.out.print("Çalışanın vardiyası: ");
        String shift = scan.nextLine();

        Calisan yeniCalisan = new Calisan(id, isim, email, password, employeeId, role, shift);
        yonetici.calisanEkle(yeniCalisan);
    }

    // ── Hayvan Ekleme ────────────────────────────────────────────────────────────

    public static void hayvanEkle(Scanner scan, BarinakYoneticisi yonetici) throws Exception {

        if (yonetici.tumCalisanlariListele().isEmpty()) {
            throw new BarinakIstisnasi("Sistemde kayıtlı çalışan yok. Önce çalışan ekleyin.");
        }

        System.out.print("Çalışan Id numarası: ");
        String ID = scan.nextLine().trim();

        if (ID.isEmpty()) {
            throw new BarinakIstisnasi("Çalışan ID boş olamaz.");
        }

        Calisan aktifCalisan = null;
        for (Calisan c : yonetici.tumCalisanlariListele()) {
            if (c.getEmployeeId().equals(ID)) {
                aktifCalisan = c;
                break;
            }
        }

        if (aktifCalisan == null) {
            throw new BarinakIstisnasi("'" + ID + "' ID'li çalışan bulunamadı.");
        }

        System.out.println("\n--- YENİ HAYVAN KAYDI ---\n");
        System.out.println("1-) Köpek");
        System.out.println("2-) Kedi");
        System.out.print("Seçiminiz: ");

        int secim = scan.nextInt();
        scan.nextLine();

        if (secim != 1 && secim != 2) {
            throw new BarinakIstisnasi("Geçersiz seçim! 1 (Köpek) veya 2 (Kedi) giriniz.");
        }

        String hitap = (secim == 1) ? "Köpeğin" : "Kedinin";

        System.out.print(hitap + " ID'si: ");
        int animalId = scan.nextInt();
        scan.nextLine();

        if (animalId <= 0) {
            throw new BarinakIstisnasi("Hayvan ID'si sıfırdan büyük olmalıdır.");
        }

        for (Hayvan h : yonetici.tumHayvanlariListele()) {
            if (h.getAnimalId() == animalId) {
                throw new BarinakIstisnasi(animalId + " ID numarasına sahip hayvan zaten bulunmaktadır.");
            }
        }

        System.out.print(hitap + " ismi: ");
        String name = scan.nextLine();
        if (name == null || name.trim().isEmpty()) {
            throw new BarinakIstisnasi("Hayvan ismi boş bırakılamaz!");
        }

        System.out.print(hitap + " türü: ");
        String tur = scan.nextLine();
        if (tur == null || tur.trim().isEmpty()) {
            throw new BarinakIstisnasi("Hayvan türü boş bırakılamaz!");
        }

        System.out.print(hitap + " yaşı: ");
        int yas = scan.nextInt();
        scan.nextLine();

        if (yas < 0 || yas > 30) {
            throw new BarinakIstisnasi("Hayvan yaşı 0-30 aralığında olmalıdır!");
        }

        System.out.print(hitap + " sağlık durumu: ");
        String saglikDurumu = scan.nextLine();

        System.out.print(hitap + " ırkı: ");
        String irk = scan.nextLine();
        if (irk == null || irk.trim().isEmpty()) {
            throw new BarinakIstisnasi("Hayvan ırkı boş bırakılamaz!");
        }

        Hayvan yeniHayvan;
        if (secim == 1) {
            yeniHayvan = new Kopek(animalId, name, tur, yas, saglikDurumu, irk);
        } else {
            yeniHayvan = new Kedi(animalId, name, tur, yas, saglikDurumu, irk);
        }

        aktifCalisan.hayvanEkle();
        yonetici.hayvanEkle(yeniHayvan);
    }

    // ── TC Kimlik Doğrulama (ortak yardımcı metod) ──────────────────────────────

    private static void tcDogrula(long id) throws BarinakIstisnasi {
        String metin = String.valueOf(Math.abs(id));

        if (metin.length() != 11) {
            throw new BarinakIstisnasi("TC numarası 11 haneli olmalıdır. Girilen: " + metin.length() + " hane.");
        }

        int toplam = 0;
        for (int i = 0; i < 10; i++) {
            toplam += Character.getNumericValue(metin.charAt(i));
        }

        int onBirinciRakam = Character.getNumericValue(metin.charAt(10));

        if (onBirinciRakam % 2 != 0) {
            throw new BarinakIstisnasi("TC numarasının son hanesi çift olmalıdır.");
        }

        if (toplam % 10 != onBirinciRakam) {
            throw new BarinakIstisnasi("Geçersiz TC Kimlik Numarası.");
        }
    }
}
