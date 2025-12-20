import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class KasirModern extends JFrame {

    // ===== FILE CSV =====
    String FILE_PRODUK = "produk.csv";
    String FILE_LAPORAN = "laporan.csv";

    // ===== THEME =====
    Color BG = new Color(241,245,249);
    Color SIDEBAR = new Color(15,23,42);
    Color CARD = Color.WHITE;
    Color PRIMARY = new Color(37,99,235);
    Color GREEN = new Color(34,197,94);
    Color RED = new Color(239,68,68);
    Color YELLOW = new Color(234,179,8);
    Color BORDER = new Color(226,232,240);

    Font FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // ===== DATA =====
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<CartItem> cart = new ArrayList<>();

    // ===== UI =====
    CardLayout contentLayout = new CardLayout();
    JPanel content = new JPanel(contentLayout);

    JTable tblProduk, tblCart, tblReport;
    DefaultTableModel mdlProduk, mdlCart, mdlReport;
    JLabel lblTotal, lblJam;

    public KasirModern() {
        setTitle("POS Kasir Minimarket");
        setSize(1280,760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(loginPage());
        setVisible(true);

        loadProdukCSV();
        startLiveClock();
    }

    // ================= LIVE JAM =================
    void startLiveClock() {
        lblJam = new JLabel();
        lblJam.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblJam.setForeground(new Color(100,116,139));

        new javax.swing.Timer(1000, e -> {
            lblJam.setText(new SimpleDateFormat(
                "EEEE, dd/MM/yyyy  HH:mm:ss"
            ).format(new Date()));
        }).start();
    }

    // ================= LOGIN =================
JPanel loginPage() {
    JPanel root = new JPanel(new GridBagLayout());
    root.setBackground(BG);

    JPanel card = cardPanel(420,520);
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

    // ===== LOGO =====
    JLabel icon = new JLabel("🛒", SwingConstants.CENTER);
    icon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
    icon.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel title = new JLabel("POS MINIMARKET");
    title.setFont(new Font("Segoe UI", Font.BOLD, 28));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitle = new JLabel("Login Kasir");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    subtitle.setForeground(new Color(100,116,139));
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== INPUT =====
    JTextField user = inputField("Username");
    JPasswordField pass = passwordField();

    // ===== BUTTON =====
    JButton login = btn(PRIMARY, "MASUK");
    login.setAlignmentX(Component.CENTER_ALIGNMENT);
    login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

    login.addActionListener(e -> {
        if (user.getText().equals("kasir") &&
                new String(pass.getPassword()).equals("123")) {
            setContentPane(mainPage());
            revalidate();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Username atau Password salah",
                "Login Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }
    });

    // ===== FOOTER =====
    JLabel footer = new JLabel("© 2025 POS Kasir Modern");
    footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    footer.setForeground(new Color(148,163,184));
    footer.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== LAYOUT =====
    card.add(Box.createVerticalStrut(10));
    card.add(icon);
    card.add(Box.createVerticalStrut(12));
    card.add(title);
    card.add(Box.createVerticalStrut(4));
    card.add(subtitle);
    card.add(Box.createVerticalStrut(30));
    card.add(user);
    card.add(Box.createVerticalStrut(16));
    card.add(pass);
    card.add(Box.createVerticalStrut(28));
    card.add(login);
    card.add(Box.createVerticalGlue());
    card.add(footer);

    root.add(card);
    return root;
}

    // ================= MAIN =================
    JPanel mainPage() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(sidebar(),BorderLayout.WEST);

        content.add(kasirPage(),"KASIR");
        content.add(barangPage(),"BARANG");
        content.add(laporanPage(),"LAPORAN");

        root.add(content,BorderLayout.CENTER);
        contentLayout.show(content,"KASIR");
        return root;
    }

    // ================= SIDEBAR =================
    JPanel sidebar() {
        JPanel side = new JPanel();
        side.setBackground(SIDEBAR);
        side.setPreferredSize(new Dimension(220,0));
        side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));

        side.add(sideBtn("🧾 Transaksi","KASIR"));
        side.add(sideBtn("📦 Barang","BARANG"));
        side.add(sideBtn("📑 Laporan","LAPORAN"));
        return side;
    }

    JButton sideBtn(String text,String page){
        JButton b = new JButton(text);
        b.setBackground(SIDEBAR);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.addActionListener(e -> contentLayout.show(content,page));
        return b;
    }

    // ================= KASIR =================
    JPanel kasirPage() {
    JPanel root = new JPanel(new BorderLayout(16,16));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(16,16,16,16));

    // ===== HEADER =====
    JLabel title = new JLabel("🧾 Transaksi Kasir");
    title.setFont(new Font("Segoe UI", Font.BOLD, 20));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(title, BorderLayout.WEST);
    header.add(lblJam, BorderLayout.EAST);

    // ===== TABLE =====
    mdlCart = new DefaultTableModel(
            new String[]{"Produk","Qty","Subtotal"},0);
    tblCart = styledTable(mdlCart);

    JScrollPane scroll = new JScrollPane(tblCart);
    scroll.setBorder(new LineBorder(BORDER));

    // ===== FOOTER =====
    lblTotal = new JLabel("Rp 0");
    lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));

    JButton bayar = btn(GREEN,"💳 Bayar");
    bayar.setPreferredSize(new Dimension(140,42));
    bayar.addActionListener(e -> bayar());

    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    footer.add(lblTotal, BorderLayout.WEST);
    footer.add(bayar, BorderLayout.EAST);

    root.add(header,BorderLayout.NORTH);
    root.add(scroll,BorderLayout.CENTER);
    root.add(footer,BorderLayout.SOUTH);
    return root;
}


    // ================= BARANG =================
    JPanel barangPage() {
    JPanel root = new JPanel(new BorderLayout(16,16));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(16,16,16,16));

    // ===== HEADER =====
    JLabel title = new JLabel("📦 Manajemen Produk");
    title.setFont(new Font("Segoe UI", Font.BOLD, 20));

    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.add(title, BorderLayout.WEST);

    // ===== TABLE =====
    mdlProduk = new DefaultTableModel(
            new String[]{"Nama Produk","Harga"},0);
    tblProduk = styledTable(mdlProduk);
    refreshProduk();

    JScrollPane scroll = new JScrollPane(tblProduk);
    scroll.setBorder(new LineBorder(BORDER));

    // ===== ACTION BUTTON =====
    JButton add = btn(PRIMARY,"➕ Tambah");
    JButton edit = btn(YELLOW,"✏ Edit");
    JButton del = btn(RED,"🗑 Hapus");
    JButton cartBtn = btn(GREEN,"🛒 Ke Kasir");

    add.addActionListener(e -> tambahProduk());
    del.addActionListener(e -> hapusProduk());
    cartBtn.addActionListener(e -> tambahKeranjang());

    edit.addActionListener(e -> {
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        Product p = products.get(r);
        String nama = JOptionPane.showInputDialog(this,"Nama Produk",p.nama);
        int harga = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Harga",p.harga));
        p.nama = nama;
        p.harga = harga;
        refreshProduk();
        simpanProdukCSV();
    });

    JPanel action = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
    action.setOpaque(false);
    action.add(add);
    action.add(edit);
    action.add(del);
    action.add(cartBtn);

    root.add(header,BorderLayout.NORTH);
    root.add(scroll,BorderLayout.CENTER);
    root.add(action,BorderLayout.SOUTH);
    return root;
}


    // ================= LAPORAN =================
JPanel laporanPage() {
    JPanel root = new JPanel(new BorderLayout(16,16));
    root.setBackground(BG);
    root.setBorder(new EmptyBorder(16,16,16,16));

    JLabel title = new JLabel("📑 Laporan Transaksi");
    title.setFont(new Font("Segoe UI", Font.BOLD, 20));

    mdlReport = new DefaultTableModel(
            new String[]{"Tanggal","Total","Metode","Bayar","Kembali"},0);
    tblReport = styledTable(mdlReport);
    loadLaporanCSV();

    JScrollPane scroll = new JScrollPane(tblReport);
    scroll.setBorder(new LineBorder(BORDER));

    root.add(title,BorderLayout.NORTH);
    root.add(scroll,BorderLayout.CENTER);
    return root;
}


    // ================= LOGIC =================
    void tambahProduk(){
        String nama = JOptionPane.showInputDialog(this,"Nama Produk");
        int harga = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Harga"));
        products.add(new Product(nama,harga));
        refreshProduk();
        simpanProdukCSV();
    }

    void hapusProduk(){
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        products.remove(r);
        refreshProduk();
        simpanProdukCSV();
    }

    void tambahKeranjang() {
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        int qty = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Jumlah"));
        Product p = products.get(r);
        cart.add(new CartItem(p.nama,qty,qty*p.harga));
        mdlCart.addRow(new Object[]{p.nama,qty,qty*p.harga});
        updateTotal();
    }

    void bayar() {
        int total = getTotal();
        if(total==0) return;

        int bayar = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Bayar"));
        int kembali = bayar - total;

        String tanggal = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        mdlReport.addRow(new Object[]{
                tanggal,total,"Tunai",bayar,kembali
        });

        simpanLaporanCSV(tanggal,total,"Tunai",bayar,kembali);

        cart.clear();
        mdlCart.setRowCount(0);
        updateTotal();
    }

    int getTotal(){ return cart.stream().mapToInt(c->c.subtotal).sum(); }
    void updateTotal(){ lblTotal.setText("Rp "+getTotal()); }

    void refreshProduk(){
        mdlProduk.setRowCount(0);
        for(Product p: products)
            mdlProduk.addRow(new Object[]{p.nama,p.harga});
    }

    // ================= CSV =================
    void simpanProdukCSV(){
        try(PrintWriter pw = new PrintWriter(new FileWriter(FILE_PRODUK))){
            pw.println("nama,harga");
            for(Product p:products)
                pw.println(p.nama+","+p.harga);
        }catch(Exception e){}
    }

    void loadProdukCSV(){
        File f = new File(FILE_PRODUK);
        if(!f.exists()) return;
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine();
            String s;
            while((s=br.readLine())!=null){
                String[] d=s.split(",");
                products.add(new Product(d[0],Integer.parseInt(d[1])));
            }
        }catch(Exception e){}
    }

    void simpanLaporanCSV(String t,int total,String m,int b,int k){
        try(PrintWriter pw = new PrintWriter(new FileWriter(FILE_LAPORAN,true))){
            if(new File(FILE_LAPORAN).length()==0)
                pw.println("tanggal,total,metode,bayar,kembali");
            pw.println(t+","+total+","+m+","+b+","+k);
        }catch(Exception e){}
    }

    void loadLaporanCSV(){
        File f=new File(FILE_LAPORAN);
        if(!f.exists()) return;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            br.readLine();
            String s;
            while((s=br.readLine())!=null){
                String[] d=s.split(",");
                mdlReport.addRow(new Object[]{
                        d[0],d[1],d[2],d[3],d[4]
                });
            }
        }catch(Exception e){}
    }

    // ================= COMPONENT =================
    JPanel cardPanel(int w,int h){
        JPanel p=new JPanel();
        p.setBackground(CARD);
        p.setBorder(new EmptyBorder(16,16,16,16));
        return p;
    }

    JTextField inputField(String hint){
        return new JTextField(hint);
    }

    JPasswordField passwordField(){
        return new JPasswordField();
    }

    JButton btn(Color c,String t){
        JButton b=new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        return b;
    }

    // ================= MODEL =================
    static class Product{
        String nama; int harga;
        Product(String n,int h){nama=n;harga=h;}
    }
    static class CartItem{
        String nama; int qty,subtotal;
        CartItem(String n,int q,int s){nama=n;qty=q;subtotal=s;}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KasirModern::new);
    }
    // ================= TABLE STYLE =================
JTable styledTable(DefaultTableModel mdl){
    JTable t = new JTable(mdl);
    t.setRowHeight(32);
    t.setFont(FONT);
    t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,13));
    t.getTableHeader().setBackground(new Color(248,250,252));
    t.getTableHeader().setBorder(new LineBorder(BORDER));
    t.setGridColor(BORDER);
    return t;
}

}
