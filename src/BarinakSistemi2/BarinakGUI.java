package BarinakSistemi2;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class BarinakGUI extends JFrame {

	// ── Renkler
	// ──────────────────────────────────────────────────────────────────────────────────────────
	private static final Color BG_DARK = new Color(18, 18, 24);
	private static final Color BG_PANEL = new Color(28, 28, 36);
	private static final Color BG_CARD = new Color(35, 35, 45);
	private static final Color ACCENT_BLUE = new Color(88, 166, 255);
	private static final Color ACCENT_GREEN = new Color(46, 160, 67);
	private static final Color ACCENT_RED = new Color(248, 81, 73);
	private static final Color ACCENT_ORANGE = new Color(216, 141, 32);
	private static final Color TEXT_PRIMARY = new Color(240, 246, 252);
	private static final Color TEXT_SECONDARY = new Color(139, 148, 158);
	private static final Color TEXT_MUTED = new Color(110, 118, 129);
	private static final Color BORDER_COLOR = new Color(48, 54, 61);

	private BarinakYoneticisi yonetici;
	private JTabbedPane tabPane;

	// Tablolar
	private JTable hayvanTable, musteriTable, calisanTable, basvuruTable, musteriBasvuruTable;
	private DefaultTableModel hayvanModel, musteriModel, calisanModel, basvuruModel, musteriBasvuruModel;

	// Durum bar
	private JLabel statusLabel;

	private Calisan girisYapanCalisan;
	private Musteri girisYapanMusteri;

	public BarinakGUI(Calisan calisan, Musteri musteri) {
		this.girisYapanCalisan = calisan;
		this.girisYapanMusteri = musteri;

		VeritabaniIslemleri.tablolariOlustur();
		yonetici = BarinakYoneticisi.getInstance();

		setTitle("🐾 Akıllı Barınak Sistemi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 720);
		setMinimumSize(new Dimension(900, 600));
		setLocationRelativeTo(null);
		getContentPane().setBackground(BG_DARK);

		initComponents();
		refreshAllTables();
	}

	// ── Bileşen Kurulumu ─────────────────────────────────────────────────────────
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));

		// Üst başlık
		add(buildHeader(), BorderLayout.NORTH);

		// Sekme paneli
		tabPane = buildTabPane();
		add(tabPane, BorderLayout.CENTER);

		// Durum çubuğu
		add(buildStatusBar(), BorderLayout.SOUTH);
	}

	private JPanel buildHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(BG_PANEL);
		header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
		header.setPreferredSize(new Dimension(0, 60));

		// ── SOL TARAF: Logo ve Dinamik Kullanıcı Bilgisi ──────────────────────
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 16));
		leftPanel.setBackground(BG_PANEL);

		// Logo Metni
		JLabel logo = new JLabel("🐾 Akıllı Barınak Sistemi");
		logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		logo.setForeground(TEXT_PRIMARY);
		leftPanel.add(logo);

		// Ayırıcı Çizgi
		JLabel sep = new JLabel("|");
		sep.setForeground(BORDER_COLOR);
		sep.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		leftPanel.add(sep);

		// Dinamik İsim ve Rol Belirleme
		String isim, rol;
		if (girisYapanCalisan != null) {
			isim = girisYapanCalisan.getName();
			rol = girisYapanCalisan.getRole(); // Yönetici, Veteriner veya Bakıcı
		} else {
			isim = girisYapanMusteri.getName();
			rol = "Müşteri";
		}

		// Kullanıcı Bilgi Etiketi
		JLabel userLabel = new JLabel(isim + " ");
		userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		userLabel.setForeground(TEXT_PRIMARY);
		leftPanel.add(userLabel);

		JLabel roleLabel = new JLabel("(" + rol + ")");
		roleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		roleLabel.setForeground(ACCENT_BLUE); // Rolü mavi yaparak vurguluyoruz
		leftPanel.add(roleLabel);

		header.add(leftPanel, BorderLayout.WEST);

		// ── SAĞ TARAF: Butonlar ───────────────────────────────────────────────
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
		rightPanel.setBackground(BG_PANEL);

		// Oturumu Kapat Butonu
		JButton cikisBtn = styledButton("↩ Oturumu Kapat", ACCENT_RED);
		cikisBtn.addActionListener(e -> {
			int secim = JOptionPane.showConfirmDialog(this, "Oturumu kapatıp giriş ekranına dönmek istiyor musunuz?",
					"Çıkış", JOptionPane.YES_NO_OPTION);
			if (secim == JOptionPane.YES_OPTION) {
				this.dispose();
				showLoginDialog();
			}
		});

		// Durum Raporu Butonu
		JButton raporBtn = styledButton("📊 Durum Raporu", ACCENT_BLUE);
		raporBtn.addActionListener(e -> showDurumRaporu());

		rightPanel.add(cikisBtn);
		rightPanel.add(raporBtn);
		header.add(rightPanel, BorderLayout.EAST);

		return header;
	}

	private JTabbedPane buildTabPane() {
		JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP);
		tp.setBackground(BG_DARK);
		tp.setForeground(TEXT_PRIMARY);
		tp.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		if (girisYapanCalisan != null) {
			// Çalışan girişi — tüm sekmeler
			tp.addTab("🐶  Hayvanlar", buildHayvanlarPanel());
			tp.addTab("👥  Müşteriler", buildMusterilerPanel());
			tp.addTab("👷  Çalışanlar", buildCalisanlarPanel());
			tp.addTab("📋  Başvurular", buildBasvurularPanel());
		} else {
			// Müşteri girişi — hayvanlar ve kendi başvuruları
			tp.addTab("🐶  Hayvanlar", buildHayvanlarPanel()); // Müşteriye eklendi
			tp.addTab("📋  Başvurularım", buildMusteriBasvurularPanel());
		}
		tp.addTab("👤  Profilim", buildProfilPanel());
		return tp;
	}

	private JPanel buildStatusBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(new Color(25, 28, 38));
		bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
		bar.setPreferredSize(new Dimension(0, 28));
		statusLabel = new JLabel("  Sistem hazır.");
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		statusLabel.setForeground(TEXT_MUTED);
		bar.add(statusLabel, BorderLayout.WEST);
		return bar;
	}

	// ── Hayvanlar Sekmesi
	// ─────────────────────────────────────────────────────────
	private JPanel buildHayvanlarPanel() {
		JPanel panel = darkPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		// Tablo
		String[] cols = { "ID", "Ad", "Tür", "Yaş", "Irk", "Sağlık Durumu", "Durum" };
		hayvanModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		hayvanTable = styledTable(hayvanModel);
		panel.add(new JScrollPane(hayvanTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		// Butonlar
		JPanel btnRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		// YETKİ KONTROLÜ: Sadece Yönetici Hayvan Ekleyebilir
		if (girisYapanCalisan instanceof Yonetici) {
			JButton ekleBtn = styledButton("➕  Hayvan Ekle", ACCENT_GREEN);
			ekleBtn.addActionListener(e -> showHayvanEkleDialog());
			btnRow.add(ekleBtn);
		}

		JButton saglikBtn = styledButton("🩺  Sağlık Kayıtları", ACCENT_ORANGE);
		saglikBtn.addActionListener(e -> showSaglikKayitlariDialog());
		btnRow.add(saglikBtn);

		JButton yenileBtn = styledButton("🔄  Yenile", ACCENT_BLUE);
		yenileBtn.addActionListener(e -> refreshHayvanTable());
		btnRow.add(yenileBtn);

		panel.add(btnRow, BorderLayout.SOUTH);

		return panel;
	}

	private void showHayvanEkleDialog() {
		List<Calisan> calisanlar = yonetici.tumCalisanlariListele();
		if (calisanlar.isEmpty()) {
			showError("Önce bir çalışan ekleyin.");
			return;
		}

		JDialog dlg = dialog("Hayvan Ekle", 420, 500);
		JPanel form = formPanel();

		// Çalışan seçimi
		String[] calisanArr = calisanlar.stream().map(c -> c.getEmployeeId() + " - " + c.getName())
				.toArray(String[]::new);
		JComboBox<String> calisanBox = styledCombo(calisanArr);

		// Tür
		String[] turler = { "Köpek", "Kedi" };
		JComboBox<String> turBox = styledCombo(turler);

		JTextField idF = styledField();
		JTextField adF = styledField();
		JTextField yasF = styledField();
		JTextField saglikF = styledField();
		JTextField irkF = styledField();

		addFormRow(form, "Çalışan:", calisanBox);
		addFormRow(form, "Hayvan Tipi:", turBox);
		addFormRow(form, "ID:", idF);
		addFormRow(form, "Ad:", adF);
		addFormRow(form, "Yaş:", yasF);
		addFormRow(form, "Sağlık Durumu:", saglikF);
		addFormRow(form, "Irk:", irkF);

		JButton kaydetBtn = styledButton("Kaydet", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				int calisanIdx = calisanBox.getSelectedIndex();
				Calisan seciliCalisan = calisanlar.get(calisanIdx);
				int animalId = BarinakIstisnasi.tamsayiCevir(idF.getText(), "Hayvan ID");
				String ad = adF.getText().trim();
				int yas = BarinakIstisnasi.tamsayiCevir(yasF.getText(), "Yaş");
				String saglik = saglikF.getText().trim();
				String irk = irkF.getText().trim();

				BarinakIstisnasi.bosAlanKontrolu("Ad, Sağlık Durumu ve Irk alanları zorunludur.", ad, saglik, irk);

				String tur = turBox.getSelectedIndex() == 0 ? "Köpek" : "Kedi";

				Hayvan yeni;
				if (turBox.getSelectedIndex() == 0) {
					yeni = new Kopek(animalId, ad, tur, yas, saglik, irk);
				} else {
					yeni = new Kedi(animalId, ad, tur, yas, saglik, irk);
				}

				yonetici.hayvanEkle(yeni, seciliCalisan);

				refreshHayvanTable();
				setStatus("✅  " + ad + " barınağa eklendi.");
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				showError(ex.getMessage());
			}
		});

		finalizeAndShowDialog(dlg, form, kaydetBtn);
	}

	// ── Müşteriler Sekmesi
	// ────────────────────────────────────────────────────────
	private JPanel buildMusterilerPanel() {
		JPanel panel = darkPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		String[] cols = { "ID (TC)", "Ad", "E-posta", "Müşteri Kodu", "Adres", "Telefon" };
		musteriModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		musteriTable = styledTable(musteriModel);
		panel.add(new JScrollPane(musteriTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		JPanel btnRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		if (girisYapanCalisan instanceof Yonetici) {
			JButton ekleBtn = styledButton("➕  Müşteri Ekle", ACCENT_GREEN);
			ekleBtn.addActionListener(e -> showMusteriEkleDialog());
			btnRow.add(ekleBtn);
		}

		JButton yenileBtn = styledButton("🔄  Yenile", ACCENT_BLUE);
		yenileBtn.addActionListener(e -> refreshMusteriTable());
		btnRow.add(yenileBtn);

		panel.add(btnRow, BorderLayout.SOUTH);

		return panel;
	}

	private void showMusteriEkleDialog() {
		JDialog dlg = dialog("Müşteri Ekle", 400, 460);
		JPanel form = formPanel();

		JTextField tcF = styledField();
		JTextField adF = styledField();
		JTextField emailF = styledField();
		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());
		JTextField kodF = styledField();
		JTextField adresF = styledField();
		JTextField telF = styledField();

		addFormRow(form, "TC Kimlik No:", tcF);
		addFormRow(form, "Ad Soyad:", adF);
		addFormRow(form, "E-posta:", emailF);
		addFormRow(form, "Şifre:", sifreF);
		addFormRow(form, "Müşteri Kodu:", kodF);
		addFormRow(form, "Adres:", adresF);
		addFormRow(form, "Telefon (10-11):", telF);

		JButton kaydetBtn = styledButton("Kaydet", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				long tc = BarinakIstisnasi.uzunSayiCevir(tcF.getText(), "TC Kimlik No");
				BarinakIstisnasi.tcDogrula(tc);

				// Duplicate kontrol
				for (Musteri m : yonetici.tumMusterileriListele()) {
					if (m.getId() == tc) {
						throw new BarinakIstisnasi(tc + " TC'ye sahip müşteri zaten kayıtlı.");
					}
				}

				String ad = adF.getText().trim();
				String email = emailF.getText().trim();
				String sifre = new String(sifreF.getPassword()).trim();
				String kod = kodF.getText().trim();
				String adres = adresF.getText().trim();
				String tel = telF.getText().trim();

				BarinakIstisnasi.bosAlanKontrolu("Ad, E-posta, Şifre ve Müşteri Kodu zorunludur.", ad, email, sifre,
						kod);

				if (!tel.matches("^[0-9]{10,11}$")) {
					throw new BarinakIstisnasi("Telefon 10-11 haneli olmalıdır.");
				}

				yonetici.musteriEkle(new Musteri(tc, ad, email, sifre, kod, adres, tel));
				refreshMusteriTable();
				setStatus("✅  Müşteri " + ad + " eklendi.");
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				showError(ex.getMessage());
			}
		});

		finalizeAndShowDialog(dlg, form, kaydetBtn);
	}

	// ── Çalışanlar Sekmesi
	// ────────────────────────────────────────────────────────
	private JPanel buildCalisanlarPanel() {
		JPanel panel = darkPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		String[] cols = { "ID (TC)", "Ad", "E-posta", "Çalışan Kodu", "Rol", "Vardiya" };
		calisanModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		calisanTable = styledTable(calisanModel);
		panel.add(new JScrollPane(calisanTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		JPanel btnRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		if (girisYapanCalisan instanceof Yonetici) {
			JButton ekleBtn = styledButton("➕  Çalışan Ekle", ACCENT_GREEN);
			ekleBtn.addActionListener(e -> showCalisanEkleDialog());
			btnRow.add(ekleBtn);
		}

		JButton yenileBtn = styledButton("🔄  Yenile", ACCENT_BLUE);
		yenileBtn.addActionListener(e -> refreshCalisanTable());
		btnRow.add(yenileBtn);

		panel.add(btnRow, BorderLayout.SOUTH);

		return panel;
	}

	private void showCalisanEkleDialog() {
		JDialog dlg = dialog("Çalışan Ekle", 400, 480);
		JPanel form = formPanel();

		JTextField tcF = styledField();
		JTextField adF = styledField();
		JTextField emailF = styledField();
		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());
		JTextField kodF = styledField();

		// Rolleri Combobox ile seçtiriyoruz
		String[] roller = { "Yönetici", "Veteriner", "Bakıcı" };
		JComboBox<String> rolBox = styledCombo(roller);

		JTextField vardiyaF = styledField();
		JTextField extraInfoF = styledField();

		addFormRow(form, "TC Kimlik No:", tcF);
		addFormRow(form, "Ad Soyad:", adF);
		addFormRow(form, "E-posta:", emailF);
		addFormRow(form, "Şifre:", sifreF);
		addFormRow(form, "Çalışan Kodu:", kodF);
		addFormRow(form, "Rol Seçimi:", rolBox);
		addFormRow(form, "Vardiya:", vardiyaF);
		addFormRow(form, "Ek Bilgi (Uzmanlık/Bölge):", extraInfoF);

		JButton kaydetBtn = styledButton("Kaydet", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				long tc = BarinakIstisnasi.uzunSayiCevir(tcF.getText(), "TC Kimlik No");
				BarinakIstisnasi.tcDogrula(tc);

				for (Calisan c : yonetici.tumCalisanlariListele()) {
					if (c.getId() == tc) {
						throw new BarinakIstisnasi(tc + " TC'ye sahip çalışan zaten kayıtlı.");
					}
				}

				String ad = adF.getText().trim();
				String email = emailF.getText().trim();
				String sifre = new String(sifreF.getPassword()).trim();
				String kod = kodF.getText().trim();
				String vardiya = vardiyaF.getText().trim();
				String extraInfo = extraInfoF.getText().trim();
				int rolSecim = rolBox.getSelectedIndex();

				BarinakIstisnasi.bosAlanKontrolu("Ad, E-posta, Şifre ve Kod zorunludur.", ad, email, sifre, kod);

				Calisan yeniCalisan;
				if (rolSecim == 0) {
					yeniCalisan = new Yonetici(tc, ad, email, sifre, kod, vardiya);
				} else if (rolSecim == 1) {
					BarinakIstisnasi.bosAlanKontrolu("Veteriner uzmanlık alanı zorunludur.", extraInfo);
					yeniCalisan = new Veteriner(tc, ad, email, sifre, kod, vardiya, extraInfo);
				} else {
					BarinakIstisnasi.bosAlanKontrolu("Bakıcı bölge bilgisi zorunludur.", extraInfo);
					yeniCalisan = new Bakici(tc, ad, email, sifre, kod, vardiya, extraInfo);
				}

				yonetici.calisanEkle(yeniCalisan);
				refreshCalisanTable();
				setStatus("✅  Çalışan " + ad + " eklendi.");
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				showError(ex.getMessage());
			}
		});

		finalizeAndShowDialog(dlg, form, kaydetBtn);
	}

	// ── Başvurular Sekmesi
	// ────────────────────────────────────────────────────────
	private JPanel buildBasvurularPanel() {
		JPanel panel = darkPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		String[] cols = { "Başvuru No", "Müşteri", "Hayvan", "Durum", "Tarih" };
		basvuruModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		basvuruTable = styledTable(basvuruModel);
		basvuruTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
		panel.add(new JScrollPane(basvuruTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		JPanel btnRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		// DİKKAT: "Yeni Başvuru" butonu buradan tamamen kaldırıldı!
		// SADECE Yönetici onay/red butonlarını görebilir
		if (girisYapanCalisan instanceof Yonetici) {
			JButton onaylaBtn = styledButton("✔  Onayla", ACCENT_GREEN);
			JButton reddetBtn = styledButton("✘  Reddet", ACCENT_RED);
			onaylaBtn.addActionListener(e -> basvuruIslem(true));
			reddetBtn.addActionListener(e -> basvuruIslem(false));
			btnRow.add(onaylaBtn);
			btnRow.add(reddetBtn);
		}

		JButton yenileBtn = styledButton("🔄  Yenile", ACCENT_BLUE);
		yenileBtn.addActionListener(e -> refreshBasvuruTable());
		btnRow.add(yenileBtn);

		// buildBasvurularPanel metodu içinde, diğer butonların yanına ekle:
		JButton detayBtn = styledButton("🔍 Detayları Gör", ACCENT_BLUE);
		detayBtn.addActionListener(e -> {
			int row = basvuruTable.getSelectedRow();
			if (row < 0) {
				showError("Lütfen detaylarını görmek istediğiniz başvuruyu seçin.");
				return;
			}
			int appId = (int) basvuruModel.getValueAt(row, 0);
			showBasvuruDetayDialog(appId);
		});
		btnRow.add(detayBtn);

		panel.add(btnRow, BorderLayout.SOUTH);
		return panel;
	}

	private JPanel buildMusteriBasvurularPanel() {
		JPanel panel = darkPanel(new BorderLayout(10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		String[] cols = { "Başvuru No", "Hayvan", "Durum", "Tarih" };
		musteriBasvuruModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		musteriBasvuruTable = styledTable(musteriBasvuruModel);
		musteriBasvuruTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());
		panel.add(new JScrollPane(musteriBasvuruTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		// --- Buton Satırı ---
		JPanel btnRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

		// Yeni Başvuru Butonu
		JButton yeniBtn = styledButton("➕  Yeni Başvuru", ACCENT_GREEN);
		yeniBtn.addActionListener(e -> showYeniBasvuruDialog());
		btnRow.add(yeniBtn);

		// --- YENİ: Müşteri İçin Detay Butonu ---
		JButton detayBtn = styledButton("🔍  Detayları Gör", ACCENT_BLUE);
		detayBtn.addActionListener(e -> {
			int row = musteriBasvuruTable.getSelectedRow();
			if (row < 0) {
				showError("Lütfen detayını görmek istediğiniz başvuruyu seçin.");
				return;
			}
			// Başvuru No ilk sütunda (0. index)
			int appId = (int) musteriBasvuruModel.getValueAt(row, 0);
			showBasvuruDetayDialog(appId);
		});
		btnRow.add(detayBtn);

		// Yenile Butonu
		JButton yenileBtn = styledButton("🔄  Yenile", ACCENT_BLUE);
		yenileBtn.addActionListener(e -> refreshMusteriBasvuruTable());
		btnRow.add(yenileBtn);

		panel.add(btnRow, BorderLayout.SOUTH);

		refreshMusteriBasvuruTable();
		return panel;
	}

	// ── Profil Sekmesi ──────────────────────────────────────────────────────────
	private JPanel buildProfilPanel() {
		JPanel mainPanel = darkPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

		// Bilgileri bir kart (panel) içinde toplayalım
		JPanel card = new JPanel(new GridBagLayout());
		card.setBackground(BG_PANEL);
		card.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Kimin giriş yaptığını kontrol et
		boolean isCalisan = (girisYapanCalisan != null);
		String ad = isCalisan ? girisYapanCalisan.getName() : girisYapanMusteri.getName();
		String tc = String.valueOf(isCalisan ? girisYapanCalisan.getId() : girisYapanMusteri.getId());

		// Düzenlenebilir alanlar
		JTextField emailF = styledField();
		JTextField sifreF = styledField();
		JTextField extraF = styledField();

		emailF.setText(isCalisan ? girisYapanCalisan.getEmail() : girisYapanMusteri.getEmail());
		sifreF.setText(isCalisan ? girisYapanCalisan.getPassword() : girisYapanMusteri.getPassword());

		// Başlık
		JLabel title = new JLabel("👤 Profil Bilgilerim");
		title.setFont(new Font("Segoe UI", Font.BOLD, 20));
		title.setForeground(ACCENT_BLUE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		card.add(title, gbc);

		// Kilitli Bilgiler (TC ve İsim)
		gbc.gridwidth = 1;
		addProfilRow(card, "TC Kimlik (Değiştirilemez):", new JLabel(tc), gbc, 1);
		addProfilRow(card, "Ad Soyad (Değiştirilemez):", new JLabel(ad), gbc, 2);

		// Değiştirilebilir Bilgiler
		addProfilRow(card, "E-posta Adresi:", emailF, gbc, 3);
		addProfilRow(card, "Sistem Şifresi:", sifreF, gbc, 4);

		if (isCalisan) {
			extraF.setText(girisYapanCalisan.getShift());
			addProfilRow(card, "Çalışma Vardiyası:", extraF, gbc, 5);
		} else {
			extraF.setText(girisYapanMusteri.getPhone());
			addProfilRow(card, "İletişim Telefonu:", extraF, gbc, 5);
		}

		// Güncelleme Butonu
		JButton guncelleBtn = styledButton("Profilimi Güncelle", ACCENT_GREEN);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(25, 10, 10, 10);
		card.add(guncelleBtn, gbc);

		guncelleBtn.addActionListener(e -> {
			try {
				if (isCalisan) {
					girisYapanCalisan.setEmail(emailF.getText().trim());
					girisYapanCalisan.setPassword(sifreF.getText().trim());
					girisYapanCalisan.setShift(extraF.getText().trim());
					// Buraya Veritabanı güncelleme kodu gelebilir:
					// VeritabaniIslemleri.calisanGuncelle(girisYapanCalisan);
				} else {
					girisYapanMusteri.setEmail(emailF.getText().trim());
					girisYapanMusteri.setPassword(sifreF.getText().trim());
					girisYapanMusteri.setPhone(extraF.getText().trim());
					// Buraya Veritabanı güncelleme kodu gelebilir:
					// VeritabaniIslemleri.musteriGuncelle(girisYapanMusteri);
				}
				setStatus("✅ Profil bilgileriniz güncellendi.");
				JOptionPane.showMessageDialog(this, "Bilgileriniz başarıyla kaydedildi!", "Profil",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				showError("Hata oluştu: " + ex.getMessage());
			}
		});

		mainPanel.add(card, BorderLayout.NORTH);
		return mainPanel;
	}

	// Profil satırı oluşturmak için yardımcı metot
	private void addProfilRow(JPanel p, String text, JComponent comp, GridBagConstraints gbc, int y) {
		gbc.gridy = y;
		gbc.gridx = 0;
		JLabel lbl = new JLabel(text);
		lbl.setForeground(TEXT_MUTED);
		p.add(lbl, gbc);

		gbc.gridx = 1;
		if (comp instanceof JLabel) {
			comp.setForeground(TEXT_PRIMARY);
		}
		p.add(comp, gbc);
	}

	private void showYeniBasvuruDialog() {
		List<Hayvan> hayvanlar = yonetici.tumHayvanlariListele();

		// Güvenlik Kontrolü: Eğer bir şekilde çalışan buraya ulaşırsa engelle
		if (girisYapanMusteri == null) {
			showError("Sadece müşteriler sahiplenme başvurusu yapabilir.");
			return;
		}

		if (hayvanlar.isEmpty()) {
			showError("Barınakta sahiplenilebilecek hayvan yok.");
			return;
		}

		JDialog dlg = dialog("Sahiplenme Başvurusu", 380, 240);
		JPanel form = formPanel();

		// Giriş yapan müşteriyi sabit metin olarak gösteriyoruz (Seçmeli değil)
		JLabel musteriBilgi = new JLabel(girisYapanMusteri.getName() + " (" + girisYapanMusteri.getCustomerId() + ")");
		musteriBilgi.setForeground(ACCENT_BLUE);
		musteriBilgi.setFont(new Font("Segoe UI", Font.BOLD, 13));

		String[] hArr = hayvanlar.stream().map(h -> h.getName() + " | ID:" + h.getAnimalId() + " | " + h.getTur())
				.toArray(String[]::new);
		JComboBox<String> hBox = styledCombo(hArr);

		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());

		addFormRow(form, "Başvuru Sahibi:", musteriBilgi);
		addFormRow(form, "Hayvan Seçin:", hBox);
		addFormRow(form, "Onay Şifreniz:", sifreF);

		JButton kaydetBtn = styledButton("Başvuruyu Gönder", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				Hayvan seciliHayvan = hayvanlar.get(hBox.getSelectedIndex());
				String girilenSifre = new String(sifreF.getPassword()).trim();

				// Kendi şifresini doğrula
				if (!girisYapanMusteri.getPassword().equals(girilenSifre)) {
					showError("Şifre yanlış, başvuru tamamlanamadı!");
					return;
				}

				int yeniId = VeritabaniIslemleri.sonBasvuruIdGetir() + 1;
				SahiplenmeBasvurusu basvuru = new SahiplenmeBasvurusu(yeniId, "BEKLEMEDE", new Date(),
						girisYapanMusteri, seciliHayvan);

				yonetici.basvuruKaydet(basvuru);
				refreshAllTables();
				setStatus("✅  Başvuru #" + yeniId + " merkeze iletildi.");
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				showError(ex.getMessage());
			}
		});

		finalizeAndShowDialog(dlg, form, kaydetBtn);
	}

	private void basvuruIslem(boolean onayla) {
		int row = basvuruTable.getSelectedRow();
		if (row < 0) {
			showError("Lütfen bir başvuru seçin.");
			return;
		}

		int appId = (int) basvuruModel.getValueAt(row, 0);

		List<Calisan> yoneticiListesi = yonetici.tumCalisanlariListele().stream()
				.filter(c -> c.getRole().equalsIgnoreCase("yönetici") || c.getRole().equalsIgnoreCase("yonetici"))
				.collect(java.util.stream.Collectors.toList());

		if (yoneticiListesi.isEmpty()) {
			showError("Sistemde yönetici rolüne sahip çalışan bulunamadı.");
			return;
		}

		String[] yArr = yoneticiListesi.stream().map(c -> c.getName() + " (" + c.getEmployeeId() + ")")
				.toArray(String[]::new);
		String secim = (String) JOptionPane.showInputDialog(this, "İşlemi yapacak yönetici:", "Yönetici Seç",
				JOptionPane.PLAIN_MESSAGE, null, yArr, yArr[0]);
		if (secim == null) {
			return;
		}

		int yIdx = java.util.Arrays.asList(yArr).indexOf(secim);
		Calisan yetkili = yoneticiListesi.get(yIdx);

		// Çalışan şifre doğrulama
		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());

		int result = JOptionPane.showConfirmDialog(this, sifreF, yetkili.getName() + " şifresini girin:",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_OPTION) {
			return;
		}

		String girilenSifre = new String(sifreF.getPassword()).trim();
		if (!yetkili.getPassword().equals(girilenSifre)) {
			showError("Çalışan şifresi yanlış!");
			return;
		}

		try {
			SahiplenmeBasvurusu hedef = yonetici.basvuruBul(appId);
			if (onayla) {
				yonetici.basvuruOnayla(hedef, yetkili);
				setStatus("✅  Başvuru #" + appId + " onaylandı.");
			} else {
				yonetici.basvuruReddet(hedef, yetkili);
				setStatus("❌  Başvuru #" + appId + " reddedildi.");
			}
			refreshAllTables();
		} catch (BarinakIstisnasi ex) {
			showError(ex.getMessage());
		}
	}

	private void showBasvuruDetayDialog(int appId) {
		try {
			SahiplenmeBasvurusu b = yonetici.basvuruBul(appId);
			Musteri m = b.getMusteri();
			Hayvan h = b.getHayvan();

			JDialog dlg = dialog("Başvuru Detayı #" + appId, 450, 500);
			JPanel mainP = new JPanel(new GridLayout(0, 1, 5, 5));
			mainP.setBackground(BG_PANEL);
			mainP.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

			mainP.add(createDetailHeader("👤 Müşteri Bilgileri"));
			mainP.add(new JLabel("Ad Soyad: " + m.getName()));
			mainP.add(new JLabel("TC Kimlik: " + m.getId()));
			mainP.add(new JLabel("E-posta: " + m.getEmail()));
			mainP.add(new JLabel("Telefon: " + m.getPhone()));
			mainP.add(new JLabel("Adres: " + m.getAddress()));

			mainP.add(new JSeparator(JSeparator.HORIZONTAL));

			mainP.add(createDetailHeader("🐶 Hayvan Bilgileri"));

			// KRİTİK GÜVENLİK KONTROLÜ
			if (h != null) {
				// Hayvan nesnesi varsa (sahiplenilmiş de olsa, barınakta da olsa) gösterir
				mainP.add(new JLabel("Adı: " + h.getName()));
				mainP.add(new JLabel("Tür / Irk: " + h.getTur() + " / "
						+ (h instanceof Kedi ? ((Kedi) h).getIrk() : ((Kopek) h).getIrk())));
				mainP.add(new JLabel("Yaş: " + h.getYas()));
				mainP.add(new JLabel("Sağlık Durumu: " + h.getSaglikDurumu()));
				mainP.add(new JLabel("Mevcut Durum: " + h.getDurum())); // Buraya durumunu da ekle (Sahiplenildi yazar)
			} else {
				// SADECE VE SADECE veri tamamen silinmişse buraya düşer (Çökmeyi engeller)
				mainP.add(new JLabel("⚠️ Bu hayvanın detaylı verisi arşivden silinmiş."));
			}

			JButton kapatBtn = styledButton("Kapat", Color.GRAY);
			kapatBtn.addActionListener(e -> dlg.dispose());

			dlg.add(new JScrollPane(mainP), BorderLayout.CENTER);
			dlg.add(kapatBtn, BorderLayout.SOUTH);
			dlg.setVisible(true);

		} catch (Exception ex) {
			showError("Detaylar yüklenirken hata oluştu: " + ex.getMessage());
		}
	}

	// Yardımcı metot: Başlıkları belirgin yapmak için
	private JLabel createDetailHeader(String text) {
		JLabel l = new JLabel(text);
		l.setFont(new Font("Segoe UI", Font.BOLD, 15));
		l.setForeground(ACCENT_BLUE);
		l.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		return l;
	}

// ── Tablo Yenileme (Modern ve Kısa Yapı) ───────────────────────────────────

	// Yardımcı Metot: Tüm tablolar için ortak yükleme kuralı
	private <T> void populateTable(DefaultTableModel model, List<T> dataList, Function<T, Object[]> mapper) {
		if (model == null || dataList == null)
			return;
		model.setRowCount(0); // Eski verileri temizle
		for (T item : dataList) {
			model.addRow(mapper.apply(item)); // Veriyi satıra çevir ve ekle
		}
	}

	private void refreshAllTables() {
		refreshHayvanTable();
		refreshMusteriTable();
		refreshCalisanTable();
		refreshBasvuruTable();
		refreshMusteriBasvuruTable();
	}

	private void refreshHayvanTable() {
		populateTable(hayvanModel, yonetici.tumHayvanlariListele(),
				h -> new Object[] { h.getAnimalId(), h.getName(), h.getTur(), h.getYas(),
						((h instanceof Kedi) ? ((Kedi) h).getIrk() : (h instanceof Kopek) ? ((Kopek) h).getIrk() : "-"),
						h.getSaglikDurumu(), h.getDurum() });
	}

	private void refreshMusteriTable() {
		populateTable(musteriModel, yonetici.tumMusterileriListele(), m -> new Object[] { m.getId(), m.getName(),
				m.getEmail(), m.getCustomerId(), m.getAddress(), m.getPhone() });
	}

	private void refreshCalisanTable() {
		populateTable(calisanModel, yonetici.tumCalisanlariListele(), c -> new Object[] { c.getId(), c.getName(),
				c.getEmail(), c.getEmployeeId(), c.getRole(), c.getShift() });
	}

	private void refreshBasvuruTable() {
		populateTable(basvuruModel, yonetici.tumBasvurular(),
				b -> new Object[] { b.getAppId(), (b.getMusteri() != null ? b.getMusteri().getName() : "?"),
						(b.getHayvan() != null ? b.getHayvan().getName() : "Sahiplenildi"), b.getDurum(),
						b.getTarih() });
	}

	private void refreshMusteriBasvuruTable() {
		if (musteriBasvuruModel == null || girisYapanMusteri == null)
			return;

		// Sadece giriş yapan müşterinin başvurularını filtrele
		List<SahiplenmeBasvurusu> filtreliListe = yonetici.tumBasvurular().stream()
				.filter(b -> b.getMusteri() != null && b.getMusteri().getId() == girisYapanMusteri.getId())
				.collect(java.util.stream.Collectors.toList());

		populateTable(musteriBasvuruModel, filtreliListe, b -> new Object[] { b.getAppId(),
				(b.getHayvan() != null ? b.getHayvan().getName() : "Sahiplenildi"), b.getDurum(), b.getTarih() });
	}

	// ── Durum Raporu
	// ──────────────────────────────────────────────────────────────
	private void showDurumRaporu() {
		int hayvan = yonetici.tumHayvanlariListele().size();
		int musteri = yonetici.tumMusterileriListele().size();
		int calisan = yonetici.tumCalisanlariListele().size();
		int basvuru = yonetici.tumBasvurular().size();
		int bekleyen = yonetici.bekleyenBasvurular().size();

		JDialog dlg = dialog("Durum Raporu", 340, 280);
		JPanel p = darkPanel(new GridLayout(6, 2, 10, 10));
		p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		addRaporRow(p, "🐾  Toplam Hayvan", String.valueOf(hayvan));
		addRaporRow(p, "👥  Kayıtlı Müşteri", String.valueOf(musteri));
		addRaporRow(p, "👷  Aktif Çalışan", String.valueOf(calisan));
		addRaporRow(p, "📋  Toplam Başvuru", String.valueOf(basvuru));
		addRaporRow(p, "⏳  Bekleyen Başvuru", String.valueOf(bekleyen));
		addRaporRow(p, "✅  İşlenen Başvuru", String.valueOf(basvuru - bekleyen));

		dlg.add(p);
		dlg.setVisible(true);
	}

	private void addRaporRow(JPanel p, String label, String value) {
		JLabel l = new JLabel(label);
		l.setForeground(TEXT_MUTED);
		l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		JLabel v = new JLabel(value);
		v.setForeground(TEXT_PRIMARY);
		v.setFont(new Font("Segoe UI", Font.BOLD, 15));
		p.add(l);
		p.add(v);
	}

	private static JPanel darkPanel(LayoutManager layout) {
		JPanel p = new JPanel(layout);
		p.setBackground(BG_DARK);
		return p;
	}

	private static JPanel formPanel() {
		JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
		p.setBackground(BG_PANEL);
		p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		return p;
	}

	private static void addFormRow(JPanel form, String label, JComponent field) {
		JLabel lbl = new JLabel(label);
		lbl.setForeground(TEXT_MUTED);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		form.add(lbl);
		form.add(field);
	}

	private static JTextField styledField() {
		JTextField f = new JTextField();
		f.setBackground(BG_CARD);
		f.setForeground(TEXT_PRIMARY);
		f.setCaretColor(TEXT_PRIMARY);
		f.setBorder(fieldBorder());
		f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		return f;
	}

	private static Border fieldBorder() {
		return BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR, 1, true),
				BorderFactory.createEmptyBorder(4, 6, 4, 6));
	}

	private static <E> JComboBox<E> styledCombo(E[] items) {
		JComboBox<E> cb = new JComboBox<>(items);
		cb.setBackground(BG_CARD);
		cb.setForeground(TEXT_PRIMARY);
		cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		return cb;
	}

	private static JButton styledButton(String text, Color bg) {
		JButton b = new JButton(text);
		b.setBackground(bg);
		b.setForeground(Color.WHITE);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setFont(new Font("Segoe UI", Font.BOLD, 12));
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
		b.addMouseListener(new MouseAdapter() {
			Color orig = bg;

			public void mouseEntered(MouseEvent e) {
				b.setBackground(orig.brighter());
			}

			public void mouseExited(MouseEvent e) {
				b.setBackground(orig);
			}
		});
		return b;
	}

	private static JButton cancelButton(JDialog dlg) {
		JButton b = styledButton("İptal", new Color(80, 85, 105));
		b.addActionListener(e -> dlg.dispose());
		return b;
	}

	private JTable styledTable(DefaultTableModel model) {
		JTable t = new JTable(model);
		t.setBackground(BG_CARD);
		t.setForeground(TEXT_PRIMARY);
		t.setSelectionBackground(ACCENT_BLUE.darker());
		t.setSelectionForeground(Color.WHITE);
		t.setGridColor(BORDER_COLOR);
		t.setRowHeight(26);
		t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		t.getTableHeader().setBackground(BG_PANEL);
		t.getTableHeader().setForeground(TEXT_MUTED);
		t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		t.setShowHorizontalLines(true);
		t.setShowVerticalLines(false);
		t.setIntercellSpacing(new Dimension(0, 1));
		return t;
	}

	private void finalizeAndShowDialog(JDialog dlg, JPanel form, JButton actionBtn) {
		JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(actionBtn);
		bottom.add(cancelButton(dlg));

		dlg.add(form, BorderLayout.CENTER);
		dlg.add(bottom, BorderLayout.SOUTH);

		dlg.setVisible(true);
	}

	private JDialog dialog(String title, int w, int h) {
		JDialog dlg = new JDialog(this, title, true);
		dlg.setSize(w, h);
		dlg.setLocationRelativeTo(this);
		dlg.getContentPane().setBackground(BG_PANEL);
		dlg.setLayout(new BorderLayout(0, 8));
		return dlg;
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
	}

	private void setStatus(String msg) {
		statusLabel.setText("  " + msg);
	}

	// ── Renk Renderer (Başvuru Durumu)
	// ────────────────────────────────────────────
	private class StatusCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row,
				int col) {
			super.getTableCellRendererComponent(t, val, sel, foc, row, col);
			setBackground(sel ? ACCENT_BLUE.darker() : BG_CARD);

			String s = val != null ? val.toString() : "";
			setText(s);

			if (s.equalsIgnoreCase("BEKLEMEDE")) {
				setForeground(ACCENT_ORANGE);
			} else if (s.contains("Onay")) {
				setForeground(ACCENT_GREEN);
			} else if (s.contains("Redded")) {
				setForeground(ACCENT_RED);
			} else {
				setForeground(TEXT_PRIMARY);
			}
			setFont(new Font("Segoe UI", Font.BOLD, 12));
			return this;
		}
	}

	private static void showLoginDialog() {
		JDialog dlg = new JDialog();
		dlg.setTitle("🐾 Akıllı Barınak Sistemi - Giriş");
		dlg.setSize(400, 360);
		dlg.setLocationRelativeTo(null);
		dlg.setModal(true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.getContentPane().setBackground(BG_DARK);
		dlg.setLayout(new BorderLayout(0, 0));

		JLabel baslik = new JLabel("🐾  Barınak Sistemi", SwingConstants.CENTER);
		baslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
		baslik.setForeground(TEXT_PRIMARY);
		baslik.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		baslik.setBackground(BG_DARK);
		baslik.setOpaque(true);
		dlg.add(baslik, BorderLayout.NORTH);

		JPanel form = new JPanel(new GridLayout(3, 2, 8, 12));
		form.setBackground(BG_PANEL);
		form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

		JLabel tcLbl = new JLabel("TC Kimlik No:");
		tcLbl.setForeground(TEXT_MUTED);
		tcLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JTextField tcF = styledField();

		JLabel sifreLbl = new JLabel("Şifre:");
		sifreLbl.setForeground(TEXT_MUTED);
		sifreLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());

		JLabel rolLbl = new JLabel("Giriş Türü:");
		rolLbl.setForeground(TEXT_MUTED);
		rolLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		String[] turler = { "Çalışan", "Müşteri" };
		JComboBox<String> turBox = styledCombo(turler);

		form.add(tcLbl);
		form.add(tcF);
		form.add(sifreLbl);
		form.add(sifreF);
		form.add(rolLbl);
		form.add(turBox);
		dlg.add(form, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		btnPanel.setBackground(BG_DARK);

		JButton girisBtn = styledButton("Giriş Yap", ACCENT_GREEN);
		JButton kayitBtn = styledButton("Kayıt Ol", ACCENT_BLUE);
		JButton cikisBtn = styledButton("Çıkış", new Color(80, 85, 105));

		cikisBtn.addActionListener(e -> System.exit(0));

		kayitBtn.addActionListener(e -> showKayitSecimDialog(dlg));

		girisBtn.addActionListener(e -> {
			try {
				long tc = BarinakIstisnasi.uzunSayiCevir(tcF.getText(), "TC Kimlik No");
				String sifre = new String(sifreF.getPassword()).trim();
				boolean calisanMi = turBox.getSelectedIndex() == 0;

				BarinakYoneticisi yonetici = BarinakYoneticisi.getInstance();

				if (calisanMi) {
					Calisan bulunan = null;
					for (Calisan c : yonetici.tumCalisanlariListele()) {
						if (c.getId() == tc && c.getPassword().equals(sifre)) {
							bulunan = c;
							break;
						}
					}
					if (bulunan == null) {
						JOptionPane.showMessageDialog(dlg, "TC veya şifre hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
						return;
					}
					dlg.dispose();
					BarinakGUI gui = new BarinakGUI(bulunan, null);
					gui.setVisible(true);
				} else {
					Musteri bulunan = null;
					for (Musteri m : yonetici.tumMusterileriListele()) {
						if (m.getId() == tc && m.getPassword().equals(sifre)) {
							bulunan = m;
							break;
						}
					}
					if (bulunan == null) {
						JOptionPane.showMessageDialog(dlg, "TC veya şifre hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
						return;
					}
					dlg.dispose();
					BarinakGUI gui = new BarinakGUI(null, bulunan);
					gui.setVisible(true);
				}
			} catch (BarinakIstisnasi ex) {
				JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
			}
		});

		btnPanel.add(girisBtn);
		btnPanel.add(kayitBtn);
		btnPanel.add(cikisBtn);
		dlg.add(btnPanel, BorderLayout.SOUTH);

		dlg.setVisible(true);
	}

	// ── Kayıt Olma İşlemleri (Giriş Ekranı İçin)
	// ───────────────────────────────────
	private static void showKayitSecimDialog(JDialog parent) {
		String[] options = { "👥 Müşteri Olarak Kaydol", "👷 Çalışan Olarak Kaydol" };
		int secim = JOptionPane.showOptionDialog(parent, "Lütfen kayıt türünü seçin:", "Kayıt Ol",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (secim == 0) {
			showStatikMusteriKayit(parent);
		} else if (secim == 1) {
			showStatikCalisanKayit(parent);
		}
	}

	private static void showStatikMusteriKayit(JDialog parent) {
		JDialog dlg = new JDialog(parent, "Müşteri Kayıt", true);
		dlg.setSize(400, 460);
		dlg.setLocationRelativeTo(parent);
		dlg.getContentPane().setBackground(BG_PANEL);
		dlg.setLayout(new BorderLayout(0, 8));

		JPanel form = formPanel();
		JTextField tcF = styledField();
		JTextField adF = styledField();
		JTextField emailF = styledField();
		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());
		JTextField kodF = styledField();
		JTextField adresF = styledField();
		JTextField telF = styledField();

		addFormRow(form, "TC Kimlik No:", tcF);
		addFormRow(form, "Ad Soyad:", adF);
		addFormRow(form, "E-posta:", emailF);
		addFormRow(form, "Şifre:", sifreF);
		addFormRow(form, "Müşteri Kodu:", kodF);
		addFormRow(form, "Adres:", adresF);
		addFormRow(form, "Telefon (10-11):", telF);

		JButton kaydetBtn = styledButton("Kayıt Ol", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				long tc = BarinakIstisnasi.uzunSayiCevir(tcF.getText(), "TC Kimlik No");
				BarinakIstisnasi.tcDogrula(tc);

				BarinakYoneticisi yonetici = BarinakYoneticisi.getInstance();
				for (Musteri m : yonetici.tumMusterileriListele()) {
					if (m.getId() == tc) {
						throw new BarinakIstisnasi("Bu TC ile kayıtlı müşteri zaten var.");
					}
				}

				String ad = adF.getText().trim(), email = emailF.getText().trim(),
						sifre = new String(sifreF.getPassword()).trim();
				String kod = kodF.getText().trim(), adres = adresF.getText().trim(), tel = telF.getText().trim();

				BarinakIstisnasi.bosAlanKontrolu("Ad, E-posta, Şifre ve Kod alanları zorunludur.", ad, email, sifre,
						kod);

				yonetici.musteriEkle(new Musteri(tc, ad, email, sifre, kod, adres, tel));
				JOptionPane.showMessageDialog(dlg, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.", "Başarılı",
						JOptionPane.INFORMATION_MESSAGE);
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
			}
		});

		JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton iptalBtn = styledButton("İptal", new Color(80, 85, 105));
		iptalBtn.addActionListener(e -> dlg.dispose());
		bottom.add(kaydetBtn);
		bottom.add(iptalBtn);

		dlg.add(form, BorderLayout.CENTER);
		dlg.add(bottom, BorderLayout.SOUTH);
		dlg.setVisible(true);
	}

	private static void showStatikCalisanKayit(JDialog parent) {
		JDialog dlg = new JDialog(parent, "Çalışan Kayıt", true);
		dlg.setSize(400, 520);
		dlg.setLocationRelativeTo(parent);
		dlg.getContentPane().setBackground(BG_PANEL);
		dlg.setLayout(new BorderLayout(0, 8));

		JPanel form = formPanel();

		JPasswordField kurumKoduF = new JPasswordField();
		kurumKoduF.setBackground(BG_CARD);
		kurumKoduF.setForeground(TEXT_PRIMARY);
		kurumKoduF.setCaretColor(TEXT_PRIMARY);
		kurumKoduF.setBorder(fieldBorder());

		JTextField tcF = styledField();
		JTextField adF = styledField();
		JTextField emailF = styledField();
		JPasswordField sifreF = new JPasswordField();
		sifreF.setBackground(BG_CARD);
		sifreF.setForeground(TEXT_PRIMARY);
		sifreF.setCaretColor(TEXT_PRIMARY);
		sifreF.setBorder(fieldBorder());
		JTextField kodF = styledField();

		String[] roller = { "Yönetici", "Veteriner", "Bakıcı" };
		JComboBox<String> rolBox = styledCombo(roller);

		JTextField vardiyaF = styledField();
		JTextField extraInfoF = styledField();

		addFormRow(form, "Kurum Onay Kodu:", kurumKoduF);
		addFormRow(form, "TC Kimlik No:", tcF);
		addFormRow(form, "Ad Soyad:", adF);
		addFormRow(form, "E-posta:", emailF);
		addFormRow(form, "Şifre:", sifreF);
		addFormRow(form, "Çalışan Kodu:", kodF);
		addFormRow(form, "Rol Seçimi:", rolBox);
		addFormRow(form, "Vardiya:", vardiyaF);
		addFormRow(form, "Ek Bilgi (Uzmanlık/Bölge):", extraInfoF);

		JButton kaydetBtn = styledButton("Kayıt Ol", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			try {
				String girilenKurumKodu = new String(kurumKoduF.getPassword()).trim();
				if (!girilenKurumKodu.equals("BARINAK2026")) {
					throw new BarinakIstisnasi("Geçersiz Kurum Onay Kodu!");
				}

				long tc = BarinakIstisnasi.uzunSayiCevir(tcF.getText(), "TC Kimlik No");
				BarinakIstisnasi.tcDogrula(tc);

				BarinakYoneticisi yonetici = BarinakYoneticisi.getInstance();
				for (Calisan c : yonetici.tumCalisanlariListele()) {
					if (c.getId() == tc) {
						throw new BarinakIstisnasi("Bu TC ile kayıtlı çalışan zaten var.");
					}
				}

				String ad = adF.getText().trim(), email = emailF.getText().trim(),
						sifre = new String(sifreF.getPassword()).trim();
				String kod = kodF.getText().trim(), vardiya = vardiyaF.getText().trim(),
						extraInfo = extraInfoF.getText().trim();
				int rolSecim = rolBox.getSelectedIndex();

				BarinakIstisnasi.bosAlanKontrolu("Ad, E-posta, Şifre ve Kod zorunludur.", ad, email, sifre, kod);

				Calisan yeniCalisan;
				if (rolSecim == 0) {
					yeniCalisan = new Yonetici(tc, ad, email, sifre, kod, vardiya);
				} else if (rolSecim == 1) {
					BarinakIstisnasi.bosAlanKontrolu("Veteriner uzmanlık alanı zorunludur.", extraInfo);
					yeniCalisan = new Veteriner(tc, ad, email, sifre, kod, vardiya, extraInfo);
				} else {
					BarinakIstisnasi.bosAlanKontrolu("Bakıcı bölge bilgisi zorunludur.", extraInfo);
					yeniCalisan = new Bakici(tc, ad, email, sifre, kod, vardiya, extraInfo);
				}

				yonetici.calisanEkle(yeniCalisan);
				JOptionPane.showMessageDialog(dlg, "Kayıt Başarılı! Giriş yapabilirsiniz.", "Başarılı",
						JOptionPane.INFORMATION_MESSAGE);
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				JOptionPane.showMessageDialog(dlg, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
			}
		});

		JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton iptalBtn = styledButton("İptal", new Color(80, 85, 105));
		iptalBtn.addActionListener(e -> dlg.dispose());
		bottom.add(kaydetBtn);
		bottom.add(iptalBtn);

		dlg.add(form, BorderLayout.CENTER);
		dlg.add(bottom, BorderLayout.SOUTH);
		dlg.setVisible(true);
	}

	// ── Veteriner: Sağlık Kayıtları İşlemleri ────────────────────────────────────
	private void showSaglikKayitlariDialog() {
		int row = hayvanTable.getSelectedRow();
		if (row < 0) {
			showError("Lütfen sağlık kayıtlarını görmek için listeden bir hayvan seçin.");
			return;
		}

		int animalId = (int) hayvanModel.getValueAt(row, 0);
		Hayvan seciliHayvan;
		try {
			seciliHayvan = yonetici.hayvanBul(animalId);
		} catch (BarinakIstisnasi ex) {
			showError(ex.getMessage());
			return;
		}

		JDialog dlg = dialog("🩺 " + seciliHayvan.getName() + " - Sağlık Kayıtları", 550, 400);

		// Sağlık Kayıtları Tablosu
		String[] cols = { "Tarih", "Tanı", "Aşı" };
		DefaultTableModel skModel = new DefaultTableModel(cols, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
		for (SaglikKaydi sk : seciliHayvan.getSaglikKayitlari()) {
			skModel.addRow(new Object[] { sdf.format(sk.getTarih()), sk.getTani(), sk.getAsi() });
		}

		JTable skTable = styledTable(skModel);
		dlg.add(new JScrollPane(skTable) {
			{
				getViewport().setBackground(BG_CARD);
			}
		}, BorderLayout.CENTER);

		JPanel bottom = darkPanel(new FlowLayout(FlowLayout.RIGHT));

		if (girisYapanCalisan instanceof Veteriner) {
			JButton yeniKayitBtn = styledButton("➕ Yeni Kayıt Ekle", ACCENT_GREEN);
			yeniKayitBtn.addActionListener(e -> showYeniSaglikKaydiDialog(seciliHayvan, skModel));
			bottom.add(yeniKayitBtn);
		}

		JButton kapatBtn = styledButton("Kapat", new Color(80, 85, 105));
		kapatBtn.addActionListener(e -> dlg.dispose());

		bottom.add(kapatBtn);
		dlg.add(bottom, BorderLayout.SOUTH);

		dlg.setVisible(true);
		;
	}

	private void showYeniSaglikKaydiDialog(Hayvan hayvan, DefaultTableModel skModel) {
		JDialog dlg = dialog("Yeni Sağlık Kaydı", 350, 250);
		JPanel form = formPanel();

		JTextField taniF = styledField();
		JTextField asiF = styledField();

		addFormRow(form, "Konulan Tanı:", taniF);
		addFormRow(form, "Yapılan Aşı:", asiF);

		JButton kaydetBtn = styledButton("Kaydet", ACCENT_GREEN);
		kaydetBtn.addActionListener(e -> {
			String tani = taniF.getText().trim();
			String asi = asiF.getText().trim();
			if (tani.isEmpty() || asi.isEmpty()) {
				showError("Tanı ve Aşı alanları boş bırakılamaz.");
				return;
			}

			SaglikKaydi yeniKayit = new SaglikKaydi(0, hayvan.getAnimalId(), new java.util.Date(), tani, asi);
			try {
				yonetici.saglikKaydiEkle(yeniKayit, hayvan, girisYapanCalisan);
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
				skModel.addRow(
						new Object[] { sdf.format(yeniKayit.getTarih()), yeniKayit.getTani(), yeniKayit.getAsi() });

				setStatus("✅ " + hayvan.getName() + " için sağlık kaydı eklendi.");
				dlg.dispose();
			} catch (BarinakIstisnasi ex) {
				showError(ex.getMessage());
			}
		});

		finalizeAndShowDialog(dlg, form, kaydetBtn);
	}

	// ── Main
	// ──────────────────────────────────────────────────────────────────────
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception ignored) {
		}

		UIManager.put("TabbedPane.foreground", new Color(240, 246, 252));
		UIManager.put("TabbedPane.selectedForeground", new Color(240, 246, 252));
		UIManager.put("TabbedPane.background", new Color(18, 18, 24));
		UIManager.put("TabbedPane.selected", new Color(35, 35, 45));
		UIManager.put("TabbedPane.contentAreaColor", new Color(18, 18, 24));
		UIManager.put("TabbedPane.light", new Color(48, 54, 61));
		UIManager.put("TabbedPane.shadow", new Color(18, 18, 24));
		UIManager.put("TabbedPane.darkShadow", new Color(18, 18, 24));
		UIManager.put("TabbedPane.focus", new Color(88, 166, 255));

		SwingUtilities.invokeLater(() -> {
			VeritabaniIslemleri.tablolariOlustur();
			showLoginDialog();
		});
	}
}