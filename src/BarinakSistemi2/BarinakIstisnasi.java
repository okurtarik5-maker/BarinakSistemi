package BarinakSistemi2;

public class BarinakIstisnasi extends Exception {

	private static final long serialVersionUID = 1L;
	
	public BarinakIstisnasi(String mesaj) {
		super("Sistem Hatası: "+mesaj);
	}
}
