package BarinakSistemi2;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Main2 {
    public static void main(String args[]) {

        VeritabaniIslemleri.tablolariOlustur();

        Scanner scan = new Scanner(System.in);

        System.out.println("--- Akıllı Barınak Sistemi Başlatılıyor ---");

        BarinakYoneticisi yonetici = BarinakYoneticisi.getInstance();

        boolean sistemCalisiyorMu = true;

        while (sistemCalisiyorMu) {

            try {

                System.out.println("\nANA MENÜ");
                System.out.println("1-) Çalışan Modu (Hayvan ekle)");
                System.out.println("2-) Müşteri Modu (Hayvan sahiplen)");
                System.out.println("3-) Müşteri ekle");
                System.out.println("4-) Çalışan ekle");
                System.out.println("5-) Durum Raporu");
                System.out.println("6-) Başvuru Yönetimi (Onayla / Reddet)");
                System.out.println("0-) Sistemden çıkış");
                System.out.print("Seçiminiz: ");

                int karar = scan.nextInt();
                scan.nextLine();

                switch (karar) {
                    case 0:
                        System.out.println("Sistem kapatılıyor...");
                        sistemCalisiyorMu = false;
                        scan.close();
                        break;

                    case 1:
                        MenuIslemleri.hayvanEkle(scan, yonetici);
                        break;

                    case 2:
                        MenuIslemleri.hayvanSahiplen(scan, yonetici);
                        break;

                    case 3:
                        MenuIslemleri.musteriEkle(scan, yonetici);
                        break;

                    case 4:
                        MenuIslemleri.calisanEkle(scan, yonetici);
                        break;

                    case 5:
                        yonetici.durumRaporuYazdir();
                        break;

                    case 6:
                        MenuIslemleri.basvuruYonet(scan, yonetici);
                        break;

                    default:
                        System.out.println("Hatalı tuşlama yaptınız. 0-6 arası bir değer giriniz.");
                        break;
                }

            } catch (InputMismatchException e) {
                System.err.println("\n[HATA]: Lütfen sayısal bir değer giriniz! (Harf veya özel karakter girdiniz)");
                scan.nextLine();
            } catch (BarinakIstisnasi e) {
                System.err.println("\n[BARINAK HATASI]: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("\n[GİRDİ HATASI]: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("\n[SİSTEM UYARISI]: " + e.getMessage());
            }
        }
    }
}
