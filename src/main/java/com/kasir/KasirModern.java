package com.kasir;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class KasirModern extends JFrame {

    // --- 1. KONFIGURASI DAN DATA ---
    private final String FILE_PRODUK = "produk.csv";
    private final String FILE_LAPORAN = "laporan.csv";
    private final String FILE_EMPLOYEE = "employee.csv";

    // Warna Modern (Palette UI)
    private final Color COL_BG_MAIN    = new Color(241, 245, 249);
    private final Color COL_SIDEBAR    = new Color(15, 23, 42);
    private final Color COL_ACCENT     = new Color(59, 130, 246); // Biru
    private final Color COL_SUCCESS    = new Color(34, 197, 94);  // Hijau
    private final Color COL_DANGER     = new Color(239, 68, 68);  // Merah

    // Font
    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);

    // Data List
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<CartItem> cartList = new ArrayList<>();
    private ArrayList<Employee> employeeList = new ArrayList<>();

    // Komponen UI Utama
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);
    
    // Komponen Halaman Kasir
    private JPanel panelProdukGrid;
    private JTextField txtSearchKasir;
    private JPanel panelCartItems;
    private JLabel lblTotalBayar;
    
    // Komponen Halaman Admin
    private DefaultTableModel modelProdukAdmin;
    private JTextField txtSearchAdmin;
    private DefaultTableModel modelLaporan;

    // --- 2. CONSTRUCTOR ---
    public KasirModern() {
        setTitle("Aplikasi Kasir Pro");
        setSize(1366, 768); // Resolusi standar laptop
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Generate Data Dummy jika file kosong (Agar tidak blank saat pertama run)
        initDummyData(); 

        // 2. Load Data dari CSV
        loadDataProduk();
        loadDataEmployee();

        // 3. Tampilkan Login Screen
        setContentPane(initLoginScreen());
        setVisible(true);
    }

    private void initDummyData() {
        File f = new File(FILE_PRODUK);
        if(!f.exists()) {
            try(PrintWriter pw = new PrintWriter(f)) {
                pw.println("Nasi Goreng Spesial,25000");
                pw.println("Ayam Bakar Madu,30000");
                pw.println("Es Teh Manis,5000");
                pw.println("Kopi Latte,18000");
                pw.println("Mie Goreng Jawa,22000");
                pw.println("Jus Alpukat,15000");
                pw.println("Kentang Goreng,12000");
                pw.println("Burger Beef,35000");
            } catch(Exception e){}
        }
    }

    // ===============================================================
    // BAGIAN A: LOGIN SCREEN (FIXED)
    // ===============================================================
    private JPanel initLoginScreen() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(COL_BG_MAIN);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Logo
        JLabel lblLogo = new JLabel(LogoGenerator.createLogoIcon(120));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        root.add(lblLogo, gbc);

        // Judul
        JLabel lblTitle = new JLabel("SYSTEM LOGIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COL_ACCENT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        root.add(lblTitle, gbc);

        // Username (Default: admin)
        gbc.gridwidth = 1; gbc.gridy = 2;
        root.add(new JLabel("Username:"), gbc);
        JTextField txtUsername = new JTextField("admin", 20); // Auto-fill
        styleInput(txtUsername);
        gbc.gridx = 1;
        root.add(txtUsername, gbc);

        // Password (Default: admin)
        gbc.gridx = 0; gbc.gridy = 3;
        root.add(new JLabel("Password:"), gbc);
        JPasswordField txtPassword = new JPasswordField("admin", 20); // Auto-fill
        styleInput(txtPassword);
        gbc.gridx = 1;
        root.add(txtPassword, gbc);

        // Tombol Login
        JButton btnLogin = new JButton("MASUK APLIKASI");
        styleButton(btnLogin, COL_ACCENT);
        
        // --- LOGIKA LOGIN ANTI-GAGAL ---
        btnLogin.addActionListener(e -> {
            String u = txtUsername.getText().trim();
            String p = new String(txtPassword.getPassword()).trim();
            boolean valid = false;

            // Cek 1: Hardcoded Admin (Pasti berhasil)
            if (u.equalsIgnoreCase("admin") && p.equals("admin")) {
                valid = true;
            } 
            // Cek 2: Dari Data CSV
            else {
                for (Employee emp : employeeList) {
                    if (emp.getUsername().equals(u) && emp.getPassword().equals(p)) {
                        valid = true; break;
                    }
                }
            }

            if (valid) {
                // Pindah ke Menu Utama
                setContentPane(initMainLayout());
                renderProdukGrid(""); // Render produk awal
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Login Gagal! Gunakan user: admin / pass: admin");
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        root.add(btnLogin, gbc);

        return root;
    }

    // ===============================================================
    // BAGIAN B: LAYOUT UTAMA & SIDEBAR
    // ===============================================================
    private JPanel initMainLayout() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(initSidebar(), BorderLayout.WEST);

        // Tambahkan halaman ke CardLayout
        contentPanel.add(initPageKasir(), "KASIR");
        contentPanel.add(initPageProduk(), "PRODUK");
        contentPanel.add(initPageLaporan(), "LAPORAN");

        // Show default page
        cardLayout.show(contentPanel, "KASIR");

        root.add(contentPanel, BorderLayout.CENTER);
        return root;
    }

    private JPanel initSidebar() {
        JPanel side = new JPanel();
        side.setBackground(COL_SIDEBAR);
        side.setPreferredSize(new Dimension(90, 0));
        side.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));

        side.add(Box.createVerticalStrut(20));

        // Logo kecil di sidebar
        JLabel lblLogoSide = new JLabel(LogoGenerator.createLogoIcon(48));
        lblLogoSide.setPreferredSize(new Dimension(60, 60));
        side.add(lblLogoSide);

        side.add(createNavBtn("🖥️", "KASIR", "Kasir"));
        side.add(createNavBtn("📦", "PRODUK", "Stok"));
        side.add(createNavBtn("📊", "LAPORAN", "Data"));
        
        side.add(Box.createVerticalStrut(300)); 
        
        JButton logout = createNavBtn("🚪", "LOGOUT", "Keluar");
        logout.setBackground(COL_DANGER);
        logout.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Logout dari aplikasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION)==0) {
                setContentPane(initLoginScreen());
                revalidate();
            }
        });
        side.add(logout);
        return side;
    }

    private JButton createNavBtn(String icon, String key, String tooltip) {
        JButton b = new JButton(icon);
        b.setPreferredSize(new Dimension(60, 60));
        // Emoji kadang tidak ter-render di beberapa font/JDK, jadi pakai fallback aman.
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 26);
        if (emojiFont.canDisplayUpTo(icon) == -1) {
            b.setFont(emojiFont);
        } else {
            b.setText(navFallbackText(key));
            b.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        b.setBackground(COL_SIDEBAR);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setToolTipText(tooltip);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if(!key.equals("LOGOUT")) {
            b.addActionListener(e -> {
                cardLayout.show(contentPanel, key);
                // Refresh data saat pindah halaman
                if(key.equals("KASIR")) { txtSearchKasir.setText(""); renderProdukGrid(""); refreshPanelCartItems(); }
                if(key.equals("PRODUK")) { txtSearchAdmin.setText(""); refreshTableProduk(""); }
            });
        }
        return b;
    }

    private String navFallbackText(String key) {
        if (key == null) return "?";
        return switch (key) {
            case "KASIR" -> "K";
            case "PRODUK" -> "P";
            case "LAPORAN" -> "L";
            case "LOGOUT" -> "X";
            default -> key.substring(0, 1);
        };
    }

    // ===============================================================
    // BAGIAN C: HALAMAN KASIR
    // ===============================================================
    private JPanel initPageKasir() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COL_BG_MAIN);

        // -- HEADER --
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(COL_BG_MAIN);
        header.setBorder(new EmptyBorder(15, 20, 5, 20));
        
        txtSearchKasir = new JTextField();
        styleInput(txtSearchKasir);
        txtSearchKasir.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { renderProdukGrid(txtSearchKasir.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { renderProdukGrid(txtSearchKasir.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { renderProdukGrid(txtSearchKasir.getText()); }
        });

        header.add(new JLabel("<html><b>Pencarian Menu</b></html>"), BorderLayout.NORTH);
        header.add(txtSearchKasir, BorderLayout.CENTER);

        // -- GRID PRODUK (KIRI) --
        panelProdukGrid = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 Kolom
        panelProdukGrid.setBackground(COL_BG_MAIN);
        panelProdukGrid.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JScrollPane scrollGrid = new JScrollPane(panelProdukGrid);
        scrollGrid.setBorder(null);
        scrollGrid.getVerticalScrollBar().setUnitIncrement(16);

        // -- KERANJANG BELANJA (KANAN) --
        JPanel panelCart = new JPanel(new BorderLayout());
        panelCart.setPreferredSize(new Dimension(400, 0));
        panelCart.setBackground(Color.WHITE);
        panelCart.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        panelCartItems = new JPanel();
        panelCartItems.setLayout(new BoxLayout(panelCartItems, BoxLayout.Y_AXIS));
        panelCartItems.setBackground(Color.WHITE);

        JScrollPane scrollCart = new JScrollPane(panelCartItems);
        scrollCart.setBorder(null);
        scrollCart.getVerticalScrollBar().setUnitIncrement(16);

        // -- FOOTER KERANJANG --
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblTotalBayar = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblTotalBayar.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTotalBayar.setForeground(COL_ACCENT);

        JButton btnBayar = new JButton("BAYAR SEKARANG");
        styleButton(btnBayar, COL_SUCCESS);
        btnBayar.setPreferredSize(new Dimension(100, 45));
        btnBayar.addActionListener(e -> actionBayar());

        JButton btnReset = new JButton("Reset");
        styleButton(btnReset, COL_DANGER);
        btnReset.addActionListener(e -> { cartList.clear(); refreshPanelCartItems(); });

        JPanel btnRow = new JPanel(new BorderLayout(10, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnReset, BorderLayout.WEST);
        btnRow.add(btnBayar, BorderLayout.CENTER);

        footer.add(new JLabel("Total Tagihan:"), BorderLayout.NORTH);
        footer.add(lblTotalBayar, BorderLayout.CENTER);
        footer.add(btnRow, BorderLayout.SOUTH);

        panelCart.add(new JLabel("  Daftar Pesanan", JLabel.CENTER), BorderLayout.NORTH);
        panelCart.add(scrollCart, BorderLayout.CENTER);
        panelCart.add(footer, BorderLayout.SOUTH);

        // -- SUSUN LAYOUT --
        JPanel leftArea = new JPanel(new BorderLayout());
        leftArea.setBackground(COL_BG_MAIN);
        leftArea.add(header, BorderLayout.NORTH);
        leftArea.add(scrollGrid, BorderLayout.CENTER);

        root.add(leftArea, BorderLayout.CENTER);
        root.add(panelCart, BorderLayout.EAST);
        return root;
    }

    private void renderProdukGrid(String keyword) {
        panelProdukGrid.removeAll();
        String k = keyword.toLowerCase();

        java.util.List<Product> filtered = productList.stream()
            .filter(p -> p.getName().toLowerCase().contains(k))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Produk tidak ditemukan.", SwingConstants.CENTER);
            panelProdukGrid.add(empty);
        }

        for (Product p : filtered) {
            JButton card = new JButton();
            card.setLayout(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(new LineBorder(new Color(220, 220, 220), 1));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.setPreferredSize(new Dimension(140, 100));

            String html = "<html><center>"
                        + "<div style='font-size:12px; font-weight:bold; color:#334155; margin-bottom:5px'>" + p.getName() + "</div>"
                        + "<div style='color:#10b981; font-size:11px'>" + formatRupiah(p.getPrice()) + "</div>"
                        + "</center></html>";
            card.setText(html);
            
            // Efek Hover
            card.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { card.setBorder(new LineBorder(COL_ACCENT, 2)); }
                public void mouseExited(MouseEvent e) { card.setBorder(new LineBorder(new Color(220, 220, 220), 1)); }
            });
            
            card.addActionListener(e -> addToCart(p));
            panelProdukGrid.add(card);
        }
        panelProdukGrid.revalidate();
        panelProdukGrid.repaint();
    }

    private void addToCart(Product p) {
        boolean found = false;
        for (CartItem c : cartList) {
            if (c.name.equals(p.getName())) {
                c.qty++;
                c.subtotal = c.qty * c.price;
                found = true; break;
            }
        }
        if (!found) cartList.add(new CartItem(p.getName(), 1, p.getPrice()));
        refreshPanelCartItems();
    }

    private void refreshPanelCartItems() {
        panelCartItems.removeAll();
        int total = 0;

        for (CartItem c : cartList) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0, new Color(240,240,240)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            itemPanel.setPreferredSize(new Dimension(0, 60));

            // Info Item
            JLabel lblItem = new JLabel("<html><b style='font-size:11px'>"+c.name+"</b><br><span style='font-size:10px;color:gray'>@ " + formatRupiah(c.price) + " x " + c.qty + "</span></html>");
            lblItem.setFont(FONT_UI);

            // Subtotal
            JLabel lblSubtotal = new JLabel(formatRupiah(c.subtotal), SwingConstants.RIGHT);
            lblSubtotal.setFont(FONT_BOLD);
            lblSubtotal.setForeground(COL_ACCENT);

            // Kontrol Quantity (- / +)
            JButton btnMinus = new JButton("−");
            btnMinus.setFont(new Font("Arial", Font.BOLD, 18));
            btnMinus.setBackground(Color.WHITE);
            btnMinus.setForeground(COL_DANGER);
            btnMinus.setBorder(null);
            btnMinus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMinus.setPreferredSize(new Dimension(30, 30));
            btnMinus.addActionListener(e -> {
                if (c.qty <= 1) {
                    cartList.remove(c);
                } else {
                    c.qty--;
                    c.subtotal = c.qty * c.price;
                }
                refreshPanelCartItems();
            });

            JButton btnPlus = new JButton("+");
            btnPlus.setFont(new Font("Arial", Font.BOLD, 16));
            btnPlus.setBackground(Color.WHITE);
            btnPlus.setForeground(COL_SUCCESS);
            btnPlus.setBorder(null);
            btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPlus.setPreferredSize(new Dimension(30, 30));
            btnPlus.addActionListener(e -> {
                c.qty++;
                c.subtotal = c.qty * c.price;
                refreshPanelCartItems();
            });

            // Tombol Hapus (X)
            JButton btnRemove = new JButton("×");
            btnRemove.setFont(new Font("Arial", Font.BOLD, 16));
            btnRemove.setBackground(Color.WHITE);
            btnRemove.setForeground(COL_DANGER);
            btnRemove.setBorder(null);
            btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnRemove.setPreferredSize(new Dimension(30, 30));
            btnRemove.addActionListener(e -> {
                cartList.remove(c);
                refreshPanelCartItems();
            });

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(btnMinus);
            rightPanel.add(btnPlus);
            rightPanel.add(lblSubtotal);
            rightPanel.add(btnRemove);

            itemPanel.add(lblItem, BorderLayout.WEST);
            itemPanel.add(rightPanel, BorderLayout.EAST);

            panelCartItems.add(itemPanel);
            total += c.subtotal;
        }

        lblTotalBayar.setText(formatRupiah(total));
        panelCartItems.revalidate();
        panelCartItems.repaint();
    }
    
    private void actionBayar() {
        if(cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }
        
        int t = cartList.stream().mapToInt(c -> c.subtotal).sum();

        int tunai = 0;
        while (true) {
            String input = JOptionPane.showInputDialog(
                this,
                "Masukkan Uang Tunai (angka):\nTotal: " + formatRupiah(t),
                "Pembayaran Tunai",
                JOptionPane.QUESTION_MESSAGE
            );
            if (input == null) return; // cancel

            Integer parsed = parseRupiahToInt(input);
            if (parsed == null) {
                JOptionPane.showMessageDialog(this, "Input tidak valid. Masukkan angka tunai, contoh: 50000");
                continue;
            }
            tunai = parsed;
            if (tunai < t) {
                JOptionPane.showMessageDialog(this, "Uang tunai kurang!\nKurang: " + formatRupiah(t - tunai));
                continue;
            }
            break;
        }

        int kembalian = tunai - t;
        
        // Simpan ke Laporan
        simpanLaporan(t);
        
        JOptionPane.showMessageDialog(
            this,
            "Pembayaran Berhasil!\n" +
            "Total: " + formatRupiah(t) + "\n" +
            "Tunai: " + formatRupiah(tunai) + "\n" +
            "Kembalian: " + formatRupiah(kembalian)
        );
        cartList.clear(); 
        refreshPanelCartItems();
    }

    private Integer parseRupiahToInt(String input) {
        if (input == null) return null;
        String cleaned = input
            .replace("Rp", "")
            .replace("rp", "")
            .replaceAll("[^0-9]", "")
            .trim();
        if (cleaned.isEmpty()) return null;
        try {
            long v = Long.parseLong(cleaned);
            if (v < 0 || v > Integer.MAX_VALUE) return null;
            return (int) v;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ===============================================================
    // BAGIAN D: HALAMAN ADMIN (PRODUK)
    // ===============================================================
    private JPanel initPageProduk() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(COL_BG_MAIN);

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBackground(COL_BG_MAIN);
        
        JLabel lbl = new JLabel("Database Produk");
        lbl.setFont(FONT_HEADER);
        
        txtSearchAdmin = new JTextField();
        styleInput(txtSearchAdmin);
        txtSearchAdmin.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { refreshTableProduk(txtSearchAdmin.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { refreshTableProduk(txtSearchAdmin.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { refreshTableProduk(txtSearchAdmin.getText()); }
        });
        
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(COL_BG_MAIN);
        searchWrap.add(new JLabel("Cari Barang:  "), BorderLayout.WEST);
        searchWrap.add(txtSearchAdmin, BorderLayout.CENTER);
        searchWrap.setPreferredSize(new Dimension(300, 35));

        top.add(lbl, BorderLayout.WEST);
        top.add(searchWrap, BorderLayout.EAST);

        modelProdukAdmin = new DefaultTableModel(new String[]{"Nama Produk", "Harga Satuan"}, 0);
        JTable tbl = new JTable(modelProdukAdmin);
        styleTable(tbl);
        refreshTableProduk("");

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acts.setBackground(COL_BG_MAIN);
        JButton btnAdd = new JButton("Tambah Baru"); styleButton(btnAdd, COL_ACCENT);
        JButton btnEdit = new JButton("Edit"); styleButton(btnEdit, Color.ORANGE);
        JButton btnDel = new JButton("Hapus"); styleButton(btnDel, COL_DANGER);

        btnAdd.addActionListener(e -> crudAction("ADD", null));
        btnEdit.addActionListener(e -> crudAction("EDIT", tbl));
        btnDel.addActionListener(e -> crudAction("DEL", tbl));

        acts.add(btnAdd); acts.add(btnEdit); acts.add(btnDel);

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(tbl), BorderLayout.CENTER);
        root.add(acts, BorderLayout.SOUTH);
        return root;
    }

    private void refreshTableProduk(String keyword) {
        modelProdukAdmin.setRowCount(0);
        String k = keyword.toLowerCase();
        for(Product p : productList) {
            if(p.getName().toLowerCase().contains(k))
                modelProdukAdmin.addRow(new Object[]{p.getName(), formatRupiah(p.getPrice())});
        }
    }

    private void crudAction(String mode, JTable tbl) {
        if(mode.equals("ADD")) {
            JTextField n = new JTextField(); JTextField h = new JTextField();
            Object[] message = {"Nama Produk:", n, "Harga (Angka):", h};
            if(JOptionPane.showConfirmDialog(this, message, "Tambah Produk", JOptionPane.OK_CANCEL_OPTION)==0){
                try {
                    productList.add(new Product(n.getText(), Integer.parseInt(h.getText())));
                    simpanProdukCSV(); refreshTableProduk("");
                } catch(Exception e) { JOptionPane.showMessageDialog(this, "Harga harus berupa angka!"); }
            }
        } else {
            int r = tbl.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Pilih baris produk dulu!"); return; }
            String selectedName = (String) tbl.getValueAt(r, 0);
            Product p = productList.stream().filter(x -> x.getName().equals(selectedName)).findFirst().orElse(null);
            if(p == null) return;

            if(mode.equals("EDIT")) {
                JTextField n = new JTextField(p.getName()); JTextField h = new JTextField(String.valueOf(p.getPrice()));
                Object[] message = {"Nama Produk:", n, "Harga:", h};
                if(JOptionPane.showConfirmDialog(this, message, "Edit Produk", JOptionPane.OK_CANCEL_OPTION)==0){
                    p.setName(n.getText()); p.setPrice(Integer.parseInt(h.getText()));
                    simpanProdukCSV(); refreshTableProduk(txtSearchAdmin.getText());
                }
            } else if(mode.equals("DEL")) {
                if(JOptionPane.showConfirmDialog(this, "Yakin hapus produk ini?")==0){
                    productList.remove(p);
                    simpanProdukCSV(); refreshTableProduk(txtSearchAdmin.getText());
                }
            }
        }
    }

    // ===============================================================
    // BAGIAN E: HALAMAN LAPORAN
    // ===============================================================
    private JPanel initPageLaporan() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(20,20,20,20));
        root.setBackground(COL_BG_MAIN);
        
        JLabel l = new JLabel("Riwayat Transaksi"); l.setFont(FONT_HEADER);
        root.add(l, BorderLayout.NORTH);
        
        modelLaporan = new DefaultTableModel(new String[]{"Waktu Transaksi", "Total Pendapatan"},0);
        JTable t = new JTable(modelLaporan);
        styleTable(t);
        loadLaporanCSV();
        
        root.add(new JScrollPane(t), BorderLayout.CENTER);
        return root;
    }

    // ===============================================================
    // BAGIAN F: UTILITIES & DATA HANDLING
    // ===============================================================
    private void styleInput(JTextField t) {
        t.setFont(FONT_UI);
        t.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }
    private void styleButton(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFont(FONT_BOLD); b.setFocusPainted(false);
        b.setBorderPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    private void styleTable(JTable t) {
        t.setRowHeight(30); t.setFont(FONT_UI);
        t.getTableHeader().setFont(FONT_BOLD);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(230, 240, 255));
    }
    private String formatRupiah(int v) {
        return NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(v).replace("Rp", "Rp ");
    }
    


    // --- CSV OPERATIONS ---
    private void simpanProdukCSV() {
        try(PrintWriter pw=new PrintWriter(FILE_PRODUK)){
            for(Product p:productList) pw.println(p.getName()+","+p.getPrice());
        }catch(Exception e){ e.printStackTrace(); }
    }
    
    private void loadDataProduk() {
        productList.clear();
        try(BufferedReader br=new BufferedReader(new FileReader(FILE_PRODUK))){
            String l; while((l=br.readLine())!=null){
                String[] d=l.split(",");
                if(d.length>1) productList.add(new Product(d[0],Integer.parseInt(d[1])));
            }
        }catch(Exception e){}
    }
    
    private void loadDataEmployee() {
        employeeList.clear();
        // BACKDOOR: Selalu tambahkan Admin default ke Memory (bukan file)
        employeeList.add(new Employee("admin", "admin", "Administrator"));
        employeeList.add(new Employee("kasir", "kasir", "Kasir"));
        
        try(BufferedReader br=new BufferedReader(new FileReader(FILE_EMPLOYEE))){
            String l; while((l=br.readLine())!=null){
                String[] d=l.split(",");
                if(d.length>2) employeeList.add(new Employee(d[0], d[1], d[2]));
            }
        }catch(Exception e){}
    }
    
    private void simpanLaporan(int t) {
        try(PrintWriter pw=new PrintWriter(new FileWriter(FILE_LAPORAN,true))){
            String d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            pw.println(d+","+t); 
            modelLaporan.addRow(new Object[]{d,formatRupiah(t)});
        }catch(Exception e){}
    }
    
    private void loadLaporanCSV() {
        modelLaporan.setRowCount(0);
        try(BufferedReader br=new BufferedReader(new FileReader(FILE_LAPORAN))){
            String l; while((l=br.readLine())!=null) {
                String[] d=l.split(","); 
                if(d.length>1) modelLaporan.addRow(new Object[]{d[0],formatRupiah(Integer.parseInt(d[1]))});
            }
        }catch(Exception e){}
    }

    // --- INNER CLASSES (MODEL DATA) ---
    static class Product { 
        private String name; 
        private int price;
        public Product(String n, int p) { this.name=n; this.price=p; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }
    }

    static class Employee {
        String username, password, role;
        public Employee(String u, String p, String r) { username=u; password=p; role=r; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }

    static class CartItem { 
        String name; int qty, price, subtotal; 
        CartItem(String n,int q,int p){name=n;qty=q;price=p;subtotal=q*p;}
    }

    // --- MAIN METHOD ---
    public static void main(String[] args) {
        // Mengaktifkan Antialiasing agar font halus
        System.setProperty("awt.useSystemAAFontSettings","on");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(KasirModern::new);
    }
}