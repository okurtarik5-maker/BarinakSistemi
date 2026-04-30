package BarinakSistemi2;

import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main2 {
	public static void main(String args[]) {

		VeritabaniIslemleri.tablolariOlustur();

		Scanner scan = new Scanner(System.in);

		System.out.println("--- Akıllı Barınak Sistemi Başlatılıyor ---");

		BarinakYoneticisi yonetici = BarinakYoneticisi.getInstance();

		int basvuruSayaci = 0;
		boolean sistemCalisiyorMu = true;

		while (sistemCalisiyorMu) {

			try {

				System.out.println("\nANA MENÜ");
				System.out.println("1-) Çalışan Modu (Hayvan ekle)");
				System.out.println("2-) Müşteri Modu (Hayvan sahiplen)");
				System.out.println("3-) Müşteri ekle");
				System.out.println("4-) Çalışan ekle");
				System.out.println("5-) Durum Raporu");
				System.out.println("6-) Musteri Listele");
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

					case 1: {

						if (yonetici.tumCalisanlariListele().isEmpty()) {
							System.out.println("Sistemde kayıtlı çalışan yok.");
							break;
						}

						System.out.print("Çalışan Id numarası: ");
						String ID = scan.nextLine();
						Calisan aktifCalisan = null;

						for (Calisan c : yonetici.tumCalisanlariListele()) {
							if (c.getEmployeeId().equals(ID)) {
								aktifCalisan = c;
								break;
							}
						}

						if (aktifCalisan == null) {
							throw new BarinakIstisnasi("Belirtilen ID numarasına ait çalışan bulunamadı!");
						}

						System.out.println("Yeni hayvan girişi.");
						System.out.println("1-) Köpek");
						System.out.println("2-) Kedi");
						System.out.print("Seçiminiz: ");

						int secim = scan.nextInt();
						scan.nextLine();

						switch (secim) {

							case 1: {
								System.out.print("Köpeğin ID'si: ");
								int animalId = scan.nextInt();
								scan.nextLine();

								System.out.print("Köpeğin ismi: ");
								String name = scan.nextLine();

								if (name == null || name.trim().isEmpty()) {
								    throw new IllegalArgumentException("Hayvan ismi boş bırakılamaz!");
								}

								System.out.print("Köpeğin türü: ");
								String tur = scan.nextLine();

								System.out.print("Köpeğin yaşı: ");
								int yas = scan.nextInt();
								scan.nextLine();

								if(yas < 0 || yas > 30) {
									throw new IllegalArgumentException("Hayvan yaşı 0'dan küçük veya 30'dan büyük olamaz!");
								}

								System.out.print("Köpeğin sağlık durumu: ");
								String saglikDurumu = scan.nextLine();

								System.out.print("Köpeğin ırkı: ");
								String irk = scan.nextLine();

								Hayvan yeniKopek = new Kopek(animalId, name, tur, yas, saglikDurumu, irk);
								aktifCalisan.hayvanEkle();
								yonetici.hayvanEkle(yeniKopek);
								break; }

							case 2: {
								System.out.print("Kedinin ID'si: ");
								int animalId = scan.nextInt();
								scan.nextLine();

								System.out.print("Kedinin ismi: ");
								String name = scan.nextLine();

								if (name == null || name.trim().isEmpty()) {
								    throw new IllegalArgumentException("Hayvan ismi boş bırakılamaz!");
								}

								System.out.print("Kedinin türü: ");
								String tur = scan.nextLine();

								System.out.print("Kedinin yaşı: ");
								int yas = scan.nextInt();
								scan.nextLine();

								if(yas < 0 || yas > 30) {
									throw new IllegalArgumentException("Hayvan yaşı 0'dan küçük veya 30'dan büyük olamaz!");
								}

								System.out.print("Kedinin sağlık durumu: ");
								String saglikDurumu = scan.nextLine();

								System.out.print("Kedinin ırkı: ");
								String irk = scan.nextLine();

								Hayvan yeniKedi = new Kedi(animalId, name, tur, yas, saglikDurumu, irk);
								aktifCalisan.hayvanEkle();
								yonetici.hayvanEkle(yeniKedi);
								break;}

							default: {
								System.out.println("Geçersiz seçim.");
								break; }
						}
						break; }

					case 2: {
						if (yonetici.tumMusterileriListele().isEmpty()) {
							System.out.println("Sistemde kayıtlı müşteri yok.");
							break;
						}

						System.out.print("\nAdınız: ");
						String ad = scan.nextLine();

						if (ad == null || ad.trim().isEmpty()) {
						    throw new IllegalArgumentException("Müşteri ismi boş bırakılamaz!");
						}

						Musteri aktifMusteri = null;

						for (Musteri m : yonetici.tumMusterileriListele()) {
							if (m.getName().equalsIgnoreCase(ad)) {
								aktifMusteri = m;
								break;
							}
						}

						if (aktifMusteri == null) {
							throw new BarinakIstisnasi("Sistemde kaydınız bulunmamaktadır!");
						}

						if (yonetici.tumHayvanlariListele().isEmpty()) {
							throw new BarinakIstisnasi("Barınakta sahiplenilebilecek hayvan bulunmamakta.");
						}

						System.out.println("\n--- Sahiplenilebilecek Hayvanlar ---");
						for (Hayvan h : yonetici.tumHayvanlariListele()) {
							System.out.println("- " + h.getName() + " | Durumu: " + h.getSaglikDurumu());
						}

						System.out.print("\nSahiplenmek istediğiniz hayvanın adı: ");
						String arananHayvan = scan.nextLine();

						if (arananHayvan == null || arananHayvan.trim().isEmpty()) {
						    throw new IllegalArgumentException("Hayvan ismi boş bırakılamaz!");
						}

						Hayvan secilenHayvan = null;

						for (Hayvan h : yonetici.tumHayvanlariListele()) {
							if (h.getName().equalsIgnoreCase(arananHayvan)) {
								secilenHayvan = h;
								break;
							}
						}

						if (secilenHayvan == null) {
							throw new BarinakIstisnasi("Bu isimde bir hayvan yok!");
						}

						// Başvuruyu oluştur ve BarinakYoneticisi'ne kaydet
						basvuruSayaci++;
						SahiplenmeBasvurusu yeniBasvuru = new SahiplenmeBasvurusu(
								basvuruSayaci, "BEKLEMEDE", new Date(), aktifMusteri, secilenHayvan
						);
						aktifMusteri.basvuruYap();
						yonetici.basvuruKaydet(yeniBasvuru);
						break; }

					case 3: {
						System.out.println("Yeni müşteri kaydı");

						System.out.print("Müşteri TC numarası: ");
						int id = scan.nextInt();
						scan.nextLine();

						for(Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
							if(mevcutMusteri.getId() == id) {
								throw new BarinakIstisnasi(id+" numarasına sahip musteri bulunmakta!");
							}
						}

						System.out.print("Müşteri ismi: ");
						String isim = scan.nextLine();

						if (isim == null || isim.trim().isEmpty()) {
						    throw new IllegalArgumentException("Müşteri ismi boş bırakılamaz!");
						}

						System.out.print("Müşteri maili: ");
						String email = scan.nextLine();

						for(Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
							if(mevcutMusteri.getEmail().equalsIgnoreCase(email)) {
								throw new BarinakIstisnasi(email+" mailine sahip musteri bulunmakta!");
							}
						}

						System.out.print("Müşteri şifresi: ");
						String password = scan.nextLine();

						System.out.print("Müşterinin müşteri ID numarası: ");
						String customerId = scan.nextLine();

						for(Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
							if(mevcutMusteri.getCustomerId().equalsIgnoreCase(customerId)) {
								throw new BarinakIstisnasi(customerId+" ID numarasına sahip musteri bulunmakta!");
							}
						}

						System.out.print("Müşteri adresi: ");
						String address = scan.nextLine();

						System.out.print("Müşteri telefon numarası: ");
						String phone = scan.nextLine();

						for(Musteri mevcutMusteri : yonetici.tumMusterileriListele()) {
							if(mevcutMusteri.getPhone().equalsIgnoreCase(phone)) {
								throw new BarinakIstisnasi(phone+" telefon numarasına sahip musteri bulunmakta!");
							}
						}

						Musteri yeniKayit = new Musteri(id, isim, email, password, customerId, address, phone);
						yonetici.musteriEkle(yeniKayit);
						break; }

					case 4: {
						System.out.println("Yeni çalışan kaydı");

						System.out.print("Çalışan TC numarası: ");
						int id = scan.nextInt();
						scan.nextLine();

						for(Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
							if(mevcutCalisan.getId() == id) {
								throw new BarinakIstisnasi(id+" id numaralı çalışan var!");
							}
						}

						System.out.print("Çalışan ismi: ");
						String isim = scan.nextLine();

						if (isim == null || isim.trim().isEmpty()) {
						    throw new IllegalArgumentException("Çalışan ismi boş bırakılamaz!");
						}

						System.out.print("Çalışan maili: ");
						String email = scan.nextLine();

						for(Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
							if(mevcutCalisan.getEmail().equalsIgnoreCase(email)) {
								throw new BarinakIstisnasi(email+" mailine sahip çalışan bulunmakta!");
							}
						}

						System.out.print("Çalışan şifresi: ");
						String password = scan.nextLine();

						System.out.print("Çalışanın çalışan Id numarası: ");
						String employeeId = scan.nextLine();

						for(Calisan mevcutCalisan : yonetici.tumCalisanlariListele()) {
							if(mevcutCalisan.getEmployeeId().equalsIgnoreCase(employeeId)) {
								throw new BarinakIstisnasi(employeeId+" ID numarasına sahip çalısan bulunmakta!");
							}
						}

						System.out.print("Çalışanın rolü: ");
						String role = scan.nextLine();

						System.out.print("Çalışanın vardiyası: ");
						String shift = scan.nextLine();

						Calisan yeniCalisan = new Calisan(id, isim, email, password, employeeId, role, shift);
						yonetici.calisanEkle(yeniCalisan);
						break; }

					case 5: {
						yonetici.durumRaporuYazdir();
						break; }
                    case 6: {
						yonetici.musteriListeme();
						break;
					}
					default: {
						System.out.println("Hatalı tuşlama yaptınız.");
						break; }
				}

			} catch (InputMismatchException e) {
				System.err.println("\n[HATA]: Lütfen sadece rakam giriniz!");
				scan.nextLine();
			} catch (Exception e) {
				System.err.println("\n[SİSTEM UYARISI]: " + e.getMessage());
			}
		}
	}
}
