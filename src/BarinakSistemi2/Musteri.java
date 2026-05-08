package BarinakSistemi2;

import java.util.ArrayList;
import java.util.List;

public class Musteri extends Person {
    private String customerId;
    private String address;
    private String phone;

    public Musteri(long id, String name, String email, String password, String customerId, String address, String phone) {
        super(id, name, email, password);
        this.customerId = customerId;
        this.address = address;
        this.phone = phone;
    }
    
    public void setCustomerId(String customerId) {
    	this.customerId = customerId;
    }
    public void setAddress(String address) {
    	this.address = address;
    }
    public void setPhone(String phone) {
    	this.phone = phone;
    }
    
    public String getCustomerId() {
    	return customerId;
    }
    public String getAddress() {
    	return address;
    }
    public String getPhone() {
    	return phone;
    }
    
    @Override
    public String getInfo() {
        return "Müşteri: " + getName() + " | Tel: " + phone;
    }

    public List<Hayvan> hayvanListele() {
        System.out.println("Sahiplenilebilecek hayvanlar listeleniyor...");
        return new ArrayList<>(); // Gerçek senaryoda veritabanından liste döner
    }

    public void basvuruYap() {
        System.out.println(getName() + " bir sahiplenme başvurusu yaptı.");
    }

    public List<Hayvan> filtrele(String tur, int yas) {
        System.out.println(tur + " türünde ve " + yas + " yaşındaki hayvanlar filtreleniyor...");
        return new ArrayList<>(); 
    }
}