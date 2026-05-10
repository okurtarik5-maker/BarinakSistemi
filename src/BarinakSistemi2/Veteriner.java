package BarinakSistemi2;

import java.util.List;

/**
 * Veteriner çalışan — sağlık kayıtlarına erişebilir ve yeni kayıt ekleyebilir.
 * Hayvan ekleme/çıkarma veya başvuru işleme yetkisi yoktur.
 */
public class Veteriner extends Calisan {

	private String uzmanlikAlani;

	public Veteriner(long id, String name, String email, String password, String employeeId, String shift, String uzmanlikAlani) {
		super(id, name, email, password, employeeId, "Veteriner", shift);
		this.uzmanlikAlani = uzmanlikAlani;
	}

	public String getUzmanlikAlani() {
		return uzmanlikAlani;
	}
	public void setUzmanlikAlani(String uzmanlikAlani) {
		this.uzmanlikAlani = uzmanlikAlani;
	}

	@Override
	public void saglikKaydiYetkisi() {
		// Yetkili — istisna fırlatmaz
	}

	@Override
	public String getInfo() {
		return super.getInfo() + " | Uzmanlık: " + uzmanlikAlani;
	}

	/**
	 * Hayvanın sağlık kayıtlarını görüntüler.
	 */
	public void saglikKayitlariniGoruntule(Hayvan hayvan) {
		if (hayvan == null) {
			System.out.println("Hayvan bilgisi yok.");
			return;
		}

		List<SaglikKaydi> kayitlar = hayvan.getSaglikKayitlari();
		System.out.println("\n--- " + hayvan.getName() + " için Sağlık Kayıtları ---");

		if (kayitlar == null || kayitlar.isEmpty()) {
			System.out.println("Bu hayvana ait sağlık kaydı bulunmamaktadır.");
			return;
		}

		for (SaglikKaydi sk : kayitlar) {
			System.out.println(sk);
		}
	}

	public void saglikGuncelle() {
		System.out.println(getName() + " (Veteriner) hayvan sağlık durumunu güncelledi.");
	}
}
