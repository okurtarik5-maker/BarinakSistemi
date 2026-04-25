package BarinakSistemi2;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class SaglikKaydi {
	
	private int recordId;
	private Date tarih;
	private String tani;
	private String asi;
	
	public SaglikKaydi(int recordId, Date tarih, String tani, String asi) {
		this.recordId = recordId;
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
		System.out.println("Saağlık kaydı başarıyla kaydedildi. Tanı: "+tani);
	}
	
	public List<String> getAsiTakvimi(){
		List<String> takvim = new ArrayList<>();
		takvim.add(asi);
		return takvim;
	}

}
