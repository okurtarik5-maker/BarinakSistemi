package BarinakSistemi2;

public class Kopek extends Hayvan {

	private String irk;

	public Kopek(int animalId, String name, String tur, int yas, String saglikDurumu, String irk) {
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
		return "Köpeğin Id numarası: "+getAnimalId()+
				"\nKöpeğin adı: "+getName()+
				"\nKöpeğin türü: "+getTur()+
				"\nKöpeğin yaşı: "+getYas()+
				"\nKöpeğin ırkı: "+irk;
	}
}
