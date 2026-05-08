package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;

public class VeriDeposu<T> {
	private List<T> kayitlar;

	public VeriDeposu() {
		this.kayitlar = new ArrayList<>();
	}

	public void ekle(T oge) {
		kayitlar.add(oge);
	}

	public List<T> hepsiniGetir() {
		return kayitlar;
	}

	public void sil(T oge) {
		kayitlar.remove(oge);
	}

	public int boyut() {
		return kayitlar.size();
	}
}
