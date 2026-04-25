package BarinakSistemi2;

public class Kedi extends Hayvan {
	
	private String irk;
	
	public Kedi(int animalId, String name, String tur, int yas, String saglikDurumu, String irk) {
		super(animalId, name, tur, yas, saglikDurumu);
		this.irk = irk;
	}
	
	public void setIrk(String irk) {
		this.irk = irk;
	}
	public String getIrk() {
		return irk;
	}

	public String getInfo() {
		return "Kedinin Id numarasi: "+getAnimalId()+
				"\nKedinin adı: "+getName()+
				"\nKedinin türü: "+getTur()+
				"\nKedinin yaaşı: "+getYas()+
				"\nKedinin ırkı: "+irk+
				"\n Kedinin sahiplenme durumu: " + getDurum();
	}
}
