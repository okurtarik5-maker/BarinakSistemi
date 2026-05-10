package BarinakSistemi2;

/**
 * Bakıcı çalışan — sadece görüntüleme yetkisine sahiptir.
 * Hiçbir değişiklik (ekleme, silme, güncelleme) yapamaz.
 */
public class Bakici extends Calisan {

	private String sorumluBolge;

	public Bakici(long id, String name, String email, String password, String employeeId, String shift, String sorumluBolge) {
		super(id, name, email, password, employeeId, "Bakici", shift);
		this.sorumluBolge = sorumluBolge;
	}

	public String getSorumluBolge() {
		return sorumluBolge;
	}
	public void setSorumluBolge(String sorumluBolge) {
		this.sorumluBolge = sorumluBolge;
	}

	@Override
	public String getInfo() {
		return super.getInfo() + " | Sorumlu Bölge: " + sorumluBolge;
	}

	// Bakıcı hiçbir yetki override etmez — tüm değişiklik istekleri reddedilir.
	// Sadece goruntulemeYetkisi() süper sınıftan true döner.

	public void hayvanlariGoruntule() {
		System.out.println(getName() + " (Bakıcı) barınaktaki hayvanları görüntülüyor.");
	}
}
