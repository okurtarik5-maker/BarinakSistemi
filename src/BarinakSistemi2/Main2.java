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
 
				System.out.println("\n========== ANA MENÜ ==========");
				System.out.println("1-) Yönetici Modu");
				System.out.println("2-) Veteriner Modu");
				System.out.println("3-) Bakıcı Modu");
				System.out.println("4-) Müşteri Modu");
				System.out.println("5-) Yeni Kayıt İşlemleri");
				System.out.println("6-) Durum Raporu");
				System.out.println("0-) Sistemden çıkış");
				System.out.println("==============================");
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
						MenuIslemleri.yoneticiModu(scan, yonetici);
						break;
 
					case 2:
						MenuIslemleri.veterinerModu(scan, yonetici);
						break;
 
					case 3:
						MenuIslemleri.bakiciModu(scan, yonetici);
						break;
 
					case 4:
						MenuIslemleri.musteriModu(scan, yonetici);
						break;
 
					case 5:
						MenuIslemleri.kayitModu(scan, yonetici);
						break;
 
					case 6:
						yonetici.durumRaporuYazdir();
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