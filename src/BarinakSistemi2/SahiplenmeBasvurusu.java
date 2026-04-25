package BarinakSistemi2;

import java.util.Date;

public class SahiplenmeBasvurusu {

	private int appId;
	private String durum;
	private Date tarih;
	private Musteri musteri;
	private Hayvan hayvan;

	public SahiplenmeBasvurusu(int appId, String durum, Date tarih, Musteri musteri, Hayvan hayvan) {
		this.appId = appId;
		this.durum = durum;
		this.tarih = tarih;
		this.musteri = musteri;
		this.hayvan = hayvan;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}
	public void setDurum(String durum) {
		this.durum = durum;
	}
	public void setTarih(Date tarih) {
		this.tarih = tarih;
	}

	public int getAppId() {
		return appId;
	}
	public String getDurum() {
		return durum;
	}
	public Date getTarih() {
		return tarih;
	}

	public Musteri getMusteri() {
		return musteri;
	}
	public void setMusteri(Musteri musteri) {
		this.musteri = musteri;
	}

	public Hayvan getHayvan() {
		return hayvan;
	}
	public void setHayvan(Hayvan hayvan) {
		this.hayvan = hayvan;
	}

	public void onayla() {
		this.durum = "Onaylandı";
		System.out.println(appId + " numaralı başvuru onaylandı.");
	}
	public void reddet() {
		this.durum = "Reddedildi";
		System.out.println(appId + " numaralı başvuru reddedildi.");
	}

	public void istek(Hayvan x) {
		x.getInfo();
		x.setSaglikDurumu("beklemede");
	}
}
