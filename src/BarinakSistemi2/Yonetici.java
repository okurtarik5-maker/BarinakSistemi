package BarinakSistemi2;

/**
 * Yönetici çalışan — tüm yetkilere sahiptir.
 * Hayvan ekleyebilir, çıkarabilir, başvuruları onaylayabilir/reddedebilir.
 */
public class Yonetici extends Calisan {

	public Yonetici(long id, String name, String email, String password, String employeeId, String shift) {
		super(id, name, email, password, employeeId, "Yonetici", shift);
	}

	@Override
	public void hayvanEkleYetkisi() {
		// Yetkili — istisna fırlatmaz
	}

	@Override
	public void hayvanCikarYetkisi() {
		// Yetkili — istisna fırlatmaz
	}

	@Override
	public void basvuruIslemYetkisi() {
		// Yetkili — istisna fırlatmaz
	}

	public void hayvanEkle() {
		System.out.println(getName() + " (Yönetici) sisteme yeni bir hayvan ekledi.");
	}

	public void basvuruOnayla() {
		System.out.println(getName() + " (Yönetici) sahiplenme başvurusunu onayladı.");
	}

	public void basvuruReddet() {
		System.out.println(getName() + " (Yönetici) sahiplenme başvurusunu reddetti.");
	}
}
