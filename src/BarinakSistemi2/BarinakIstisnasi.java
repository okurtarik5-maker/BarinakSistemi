package BarinakSistemi2;

/**
 * Barınak sistemine özgü istisnaların merkezi yönetim sınıfı.
 * Hem ana istisna tipini hem de spesifik hata türlerini barındırır.
 */
public class BarinakIstisnasi extends Exception {

	private static final long serialVersionUID = 1L;

	public BarinakIstisnasi(String mesaj) {
		super("Sistem Hatası: " + mesaj);
	}

	// ── Özelleştirilmiş İstisna Türleri (Extending BarinakIstisnasi) ───────────────

	public static class GecersizTCIstisnasi extends BarinakIstisnasi {
		public GecersizTCIstisnasi(String detay) {
			super("Kimlik Doğrulama Hatası: " + detay);
		}
	}

	public static class BosAlanIstisnasi extends BarinakIstisnasi {
		public BosAlanIstisnasi(String detay) {
			super("Eksik Bilgi: " + detay);
		}
	}

	public static class VeriFormatIstisnasi extends BarinakIstisnasi {
		public VeriFormatIstisnasi(String alanAdi, String beklenenTip) {
			super(alanAdi + " alanı geçersiz. Beklenen: " + beklenenTip);
		}
	}

	// ── Merkezi Doğrulama ve Yardımcı Metotlar ────────────────────────────────────

	/**
	 * TC Kimlik Numarasını standart algoritmaya göre doğrular.
	 */
	public static void tcDogrula(long id) throws GecersizTCIstisnasi {
		String s = String.valueOf(Math.abs(id));
		if (s.length() != 11) {
			throw new GecersizTCIstisnasi("TC 11 haneli olmalıdır. (Girilen: " + s.length() + ")");
		}
		int toplam = 0;
		for (int i = 0; i < 10; i++) {
			toplam += Character.getNumericValue(s.charAt(i));
		}
		int son = Character.getNumericValue(s.charAt(10));
		if (son % 2 != 0) {
			throw new GecersizTCIstisnasi("TC'nin son hanesi çift olmalıdır.");
		}
		if (toplam % 10 != son) {
			throw new GecersizTCIstisnasi("Geçersiz TC Kimlik Numarası algoritması.");
		}
	}

	/**
	 * Verilen metin dizisindeki alanların boş olup olmadığını kontrol eder.
	 */
	public static void bosAlanKontrolu(String hataMesaji, String... alanlar) throws BosAlanIstisnasi {
		for (String alan : alanlar) {
			if (alan == null || alan.trim().isEmpty()) {
				throw new BosAlanIstisnasi(hataMesaji);
			}
		}
	}

	/**
	 * String bir değeri tam sayıya çevirir, hata durumunda özel istisna fırlatır.
	 */
	public static int tamsayiCevir(String deger, String alanAdi) throws VeriFormatIstisnasi {
		try {
			return Integer.parseInt(deger.trim());
		} catch (NumberFormatException e) {
			throw new VeriFormatIstisnasi(alanAdi, "Tam Sayı");
		}
	}

	/**
	 * String bir değeri long tipine çevirir, hata durumunda özel istisna fırlatır.
	 */
	public static long uzunSayiCevir(String deger, String alanAdi) throws VeriFormatIstisnasi {
		try {
			return Long.parseLong(deger.trim());
		} catch (NumberFormatException e) {
			throw new VeriFormatIstisnasi(alanAdi, "Sayısal Değer");
		}
	}
}
