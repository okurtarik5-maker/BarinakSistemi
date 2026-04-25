package BarinakSistemi2;

public class Calisan extends Person {

	private String employeeId;
	private String role;
	private String shift;

	public Calisan(int id, String name, String email, String password, String employeeId, String role, String shift) {
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
		return "Kişinin Id numarası: "+getId()+
				"Kişinin ismi: "+getName()+
				"Kişinin emaili: "+getEmail()+
				"Kişinin İşçi Id numarası: "+employeeId+
				"Kişinin rolü: "+role+
				"Vardiya: "+shift;
	}

	public void hayvanEkle() {
		System.out.println(getName() + " sisteme yeni bir hayvan ekledi.");
	}

	public void saglikGuncelle() {
		System.out.println("Hayvan sağlık durumu güncellendi.");
	}

	public void basvuruOnayla() {
		System.out.println("Sahiplenme başvurusu onaylandı.");
	}
}
