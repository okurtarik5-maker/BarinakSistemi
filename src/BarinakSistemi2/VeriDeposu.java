package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;

// <T> ifadesi bu sınıfın jenerik olduğunu belirtir.
public class VeriDeposu<T> {
    private List<T> kayitlar;

    public VeriDeposu() {
        this.kayitlar = new ArrayList<>();
    }

    // Listeye yeni bir öğe ekler
    public void ekle(T oge) {
        kayitlar.add(oge);
    }

    // Tüm listeyi döndürür
    public List<T> hepsiniGetir() {
        return kayitlar;
    }

    // Listeden öğe siler
    public void sil(T oge) {
        kayitlar.remove(oge);
    }

    // Listedeki eleman sayısını döndürür
    public int boyut() {
        return kayitlar.size();
    }
}
