package BarinakSistemi2;

public abstract class Person implements IYonetilebilir{

	private int id;
	private String name;
	private String email;
	private String password;

	public Person(int id, String name, String email, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public boolean login() {
        // Temel giriş kontrol mantığı
        System.out.println(name + " sisteme giriş yaptı.");
        return true;
    }

	// Soyut metod: Alt sınıflar kendi özelliklerine göre dolduracak
    public abstract String getInfo();

    public void kaydet() {
    	System.out.println("Kişi kaydetildi. "+name);
    	//ArrayList ile kayıt yapıp database'e öyle kaydedebilir miyiz
    }
    public void guncelle() {
    	System.out.println("Kişi güncecllendi. "+name);
    }
    public void sil() {
    	System.out.println("Kişi silindi. "+name);
    }
}
