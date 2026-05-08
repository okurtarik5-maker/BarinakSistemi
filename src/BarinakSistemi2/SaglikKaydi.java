package BarinakSistemi2;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class SaglikKaydi {

	private int recordId;
	private int animalId;       // Hangi hayvana ait olduğunu tutar (DB'de yabancı anahtar)
	private Date tarih;
	private String tani;
	private String asi;

	public SaglikKaydi(int recordId, int animalId, Date tarih, String tani, String asi) {
		this.recordId = recordId;
		this.animalId = animalId;
		this.tarih = tarih;
		this.tani = tani;
		this.asi = asi;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public int getRecordId() {
		return recordId;
	}

	public void setAnimalId(int animalId) {
		this.animalId = animalId;
	}
	public int getAnimalId() {
		return animalId;
	}

	public void setTarih(Date tarih) {
		this.tarih = tarih;
	}
	public Date getTarih() {
		return tarih;
	}

	public void setTani(String tani) {
		this.tani = tani;
	}
	public String getTani() {
		return tani;
	}

	public void setAsi(String asi) {
		this.asi = asi;
	}
	public String getAsi() {
		return asi;
	}

	public void kaydet() {
		System.out.println("Sağlık kaydı başarıyla kaydedildi. Tanı: " + tani);
	}

	public List<String> getAsiTakvimi() {
		List<String> takvim = new ArrayList<>();
		takvim.add(asi);
		return takvim;
	}

	@Override
	public String toString() {
		return "Kayıt #" + recordId + " | Tarih: " + tarih + " | Tanı: " + tani + " | Aşı: " + asi;
	}
}
