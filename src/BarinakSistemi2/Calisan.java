package BarinakSistemi2;

/**
 * Çalışan sınıfı — Yönetici, Veteriner ve Bakıcı için temel sınıf.
 * Her alt sınıf kendi yetki seviyesine göre işlemleri override eder.
 */
public abstract class Calisan extends Person {

	private String employeeId;
	private String role;
	private String shift;

	public Calisan(long id, String name, String email, String password, String employeeId, String role, String shift) {
		super(id, name, email, password);
		this.employeeId = employeeId;
		this.role = role;
		this.shift = shift;
	}

	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public String getShift() {
		return shift;
	}
	public void setShift(String shift) {
		this.shift = shift;
	}

	public String getInfo() {
		return "Kişinin Id numarası: " + getId() +
				" | İsim: " + getName() +
				" | Email: " + getEmail() +
				" | Çalışan ID: " + employeeId +
				" | Rol: " + role +
				" | Vardiya: " + shift;
	}

	// ── Yetki gerektiren işlemler (alt sınıflar override eder) ────────────────────

	/**
	 * Hayvan ekleme yetkisi. Sadece Yönetici izinlidir.
	 */
	public void hayvanEkleYetkisi() throws BarinakIstisnasi {
		throw new BarinakIstisnasi(getName() + " (" + role + ") hayvan ekleme yetkisine sahip değil.");
	}

	/**
	 * Hayvan silme/çıkarma yetkisi. Sadece Yönetici izinlidir.
	 */
	public void hayvanCikarYetkisi() throws BarinakIstisnasi {
		throw new BarinakIstisnasi(getName() + " (" + role + ") hayvan çıkarma yetkisine sahip değil.");
	}

	/**
	 * Başvuru onaylama/reddetme yetkisi. Sadece Yönetici izinlidir.
	 */
	public void basvuruIslemYetkisi() throws BarinakIstisnasi {
		throw new BarinakIstisnasi(getName() + " (" + role + ") başvuru işleme yetkisine sahip değil.");
	}

	/**
	 * Sağlık kaydı ekleme/güncelleme yetkisi. Sadece Veteriner izinlidir.
	 */
	public void saglikKaydiYetkisi() throws BarinakIstisnasi {
		throw new BarinakIstisnasi(getName() + " (" + role + ") sağlık kaydı işlemleri yapamaz.");
	}

	/**
	 * Görüntüleme tüm çalışanlara açıktır (Bakıcı dahil).
	 */
	public boolean goruntulemeYetkisi() {
		return true;
	}
}

