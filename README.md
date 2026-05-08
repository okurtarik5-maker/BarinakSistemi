Bu proje, bir hayvan barınağının tüm operasyonel süreçlerini (sahiplendirme, sağlık takibi, personel yönetimi) dijital ortamda yönetmek için geliştirilmiş Java Swing tabanlı bir masaüstü uygulamasıdır.
Proje, Nesne Yönelimli Programlama (OOP) prensipleri ve katmanlı mimari kullanılarak tasarlanmıştır.

Rol Tabanlı Yetkilendirme: Kullanıcılar sisteme Giriş/Kayıt yaparak rollerine göre özel yetkilere sahip olur.
Dinamik Veritabanı: SQLite entegrasyonu sayesinde veriler kalıcı olarak saklanır. Program ilk açılışta veritabanı dosyasını otomatik oluşturur.
Akıllı Başvuru Sistemi: Bir müşteri, daha önce reddedildiği bir hayvana tekrar başvuru yapamaz.
Gelişmiş Arayüz: Karanlık tema (Dark Mode) uyumlu, kullanıcı dostu ve modern bir GUI tasarımı.

Sisteme dışarıdan yetkisiz kişilerin "Çalışan" (Yönetici, Veteriner, Bakıcı) olarak kaydolmasını engellemek için bir güvenlik mekanizması eklenmiştir:
Onay Kodu: BARINAK2026--------------------------------------------------(ÖNEMLİ)-------------------------------------------------------------------------------------------------
İşleyiş: Çalışan kayıt formunda bu kod doğru girilmediği sürece sistem kayıt işlemini reddeder. Bu, barınak yönetiminin sadece yetkili personel tarafından yapılmasını garanti altına alır.

👥 Kullanıcı Rolleri ve Yetkileri
Rol                        Yetki Kapsamı
Yönetici                   Hayvan ekleme, personel ve müşteri yönetimi, başvuruları onaylama/reddetme
Veteriner                  Hayvanların geçmiş sağlık kayıtlarını görme ve yeni tıbbi kayıt/aşı girişi yapma
Bakıcı                     Barınaktaki hayvanları ve sahiplenme durumlarını görüntüleme 
Müşteri                    Hayvanları inceleme, sahiplenme başvurusu yapma ve başvuru durumunu takip etme.

🛠️ Kurulum ve Çalıştırma
1. Gereksinimler
Java JDK 11 veya üzeri.
sqlite-jdbc sürücüsü (Proje içindeki lib klasöründe mevcuttur).

2. Kütüphane Tanımlama
Projenin veritabanına bağlanabilmesi için lib klasöründeki .jar dosyasını IDE'nize tanıtmanız gerekir:
NetBeans: Project Properties -> Libraries -> Compile -> Add JAR/Folder -> lib/sqlite-jdbc...jar seçin.
Eclipse: Project -> Properties -> Java Build Path -> Libraries -> Add External JARs -> lib içindeki dosyayı seçin.

3. Çalıştırma
Ana menü ve giriş ekranı için BarinakGUI.java dosyasını sağ tıklayıp Run diyerek uygulamayı başlatabilirsiniz.

📁 Veritabanı Yapısı
Sistem ilk çalıştırıldığında barinak.db dosyasını otomatik olarak kök dizinde oluşturur. Dosyayı silerseniz sistem tüm tabloları sıfırdan ve temiz bir şekilde yeniden inşa edecektir.
