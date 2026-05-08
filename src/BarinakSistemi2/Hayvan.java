package BarinakSistemi2;

import java.util.List;
import java.util.ArrayList;

public abstract class Hayvan {

	private int animalId;
	private String name;
	private String tur;
	private int yas;
	private String saglikDurumu;
	private String durum;

	private List<SaglikKaydi> saglikKayitlari;

	public Hayvan(int animalId, String name, String tur, int yas, String saglikDurumu) {
		this.animalId = animalId;
		this.name = name;
		this.tur = tur;
		this.yas = yas;
		this.saglikDurumu = saglikDurumu;
		this.saglikKayitlari = new ArrayList<>();
	}

	public String getDurum() {
		return durum;
	}
	public void setDurum(String durum) {
		this.durum = durum;
	}

	public void setAnimalId(int animalId) {
		this.animalId = animalId;
	}
	public int getAnimalId() {
		return animalId;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setTur(String tur) {
		this.tur = tur;
	}
	public String getTur() {
		return tur;
	}

	public void setYas(int yas) {
		this.yas = yas;
	}
	public int getYas() {
		return yas;
	}

	public void setSaglikDurumu(String saglikDurumu) {
		this.saglikDurumu = saglikDurumu;
	}
	public String getSaglikDurumu() {
		return saglikDurumu;
	}

	public abstract String getInfo();

	public void saglikKaydiEkle(SaglikKaydi kayit) {
		this.saglikKayitlari.add(kayit);
	}

	public List<SaglikKaydi> getSaglikKayitlari() {
		return saglikKayitlari;
	}

	public void setSaglikKayitlari(List<SaglikKaydi> saglikKayitlari) {
		this.saglikKayitlari = saglikKayitlari;
	}
}
