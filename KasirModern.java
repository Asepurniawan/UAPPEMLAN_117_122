import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class KasirModern extends JFrame {

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
    Font TITLE = new Font("Segoe UI", Font.BOLD, 18);

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

        products.add(new Product("Indomie",3500));
        products.add(new Product("Teh Botol",5000));

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

        JPanel card = cardPanel(360,420);
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("🛒", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI",Font.PLAIN,48));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("POS MINIMARKET");
        title.setFont(new Font("Segoe UI",Font.BOLD,22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField user = inputField("Username");
        JPasswordField pass = passwordField();

        JButton login = btn(PRIMARY,"LOGIN");
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        login.addActionListener(e -> {
            if(user.getText().equals("kasir") &&
               new String(pass.getPassword()).equals("123")) {
                setContentPane(mainPage());
                revalidate();
            } else {
                JOptionPane.showMessageDialog(this,"Login gagal");
            }
        });

        card.add(logo);
        card.add(Box.createVerticalStrut(15));
        card.add(title);
        card.add(Box.createVerticalStrut(25));
        card.add(user);
        card.add(Box.createVerticalStrut(12));
        card.add(pass);
        card.add(Box.createVerticalStrut(20));
        card.add(login);

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

        JLabel logo = new JLabel(" 🛒 KASIR");
        logo.setFont(new Font("Segoe UI",Font.BOLD,18));
        logo.setForeground(Color.WHITE);
        logo.setBorder(new EmptyBorder(25,20,25,20));

        side.add(logo);
        side.add(sideBtn("🧾 Transaksi","KASIR"));
        side.add(sideBtn("📦 Barang","BARANG"));
        side.add(sideBtn("📑 Laporan","LAPORAN"));
        return side;
    }

    JButton sideBtn(String text,String page){
        JButton b = new JButton(text);
        b.setBackground(SIDEBAR);
        b.setForeground(Color.WHITE);
        b.setFont(FONT);
        b.setBorder(new EmptyBorder(12,20,12,20));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.addActionListener(e -> contentLayout.show(content,page));
        return b;
    }

    // ================= HEADER DENGAN LOGO =================
    JPanel pageWrap(String title){
        JPanel card = cardPanel(0,0);
        card.setLayout(new BorderLayout(12,12));

        JLabel logo = new JLabel("🛒");
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 26));

        JLabel appName = new JLabel("POS MINIMARKET KASIR");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel pageTitle = new JLabel(title);
        pageTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pageTitle.setForeground(new Color(100,116,139));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(appName);
        titleBox.add(pageTitle);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        leftHeader.setOpaque(false);
        leftHeader.add(logo);
        leftHeader.add(titleBox);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(leftHeader,BorderLayout.WEST);
        header.add(lblJam,BorderLayout.EAST);

        card.add(header,BorderLayout.NORTH);
        return card;
    }

    // ================= KASIR =================
    JPanel kasirPage() {
        JPanel wrap = pageWrap("🧾 Transaksi Kasir");

        mdlCart = new DefaultTableModel(
                new String[]{"Produk","Qty","Subtotal"},0);
        tblCart = table(mdlCart);

        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI",Font.BOLD,22));

        JButton bayar = smallBtn("💰","Bayar",GREEN);
        bayar.addActionListener(e -> bayar());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(lblTotal,BorderLayout.WEST);
        bottom.add(bayar,BorderLayout.EAST);

        wrap.add(new JScrollPane(tblCart),BorderLayout.CENTER);
        wrap.add(bottom,BorderLayout.SOUTH);
        return wrap;
    }

    // ================= BARANG =================
    JPanel barangPage() {
        JPanel wrap = pageWrap("📦 Data Barang");

        mdlProduk = new DefaultTableModel(
                new String[]{"Nama","Harga"},0);
        tblProduk = table(mdlProduk);
        refreshProduk();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        actions.setOpaque(false);

        JButton add = smallBtn("➕","Tambah",PRIMARY);
        JButton edit = smallBtn("✏","Edit",YELLOW);
        JButton del = smallBtn("🗑","Hapus",RED);
        JButton cartBtn = smallBtn("🧾","Ke Kasir",GREEN);

        add.addActionListener(e -> tambahProduk());
        edit.addActionListener(e -> editProduk());
        del.addActionListener(e -> hapusProduk());
        cartBtn.addActionListener(e -> tambahKeranjang());

        actions.add(add);
        actions.add(edit);
        actions.add(del);
        actions.add(cartBtn);

        wrap.add(new JScrollPane(tblProduk),BorderLayout.CENTER);
        wrap.add(actions,BorderLayout.SOUTH);
        return wrap;
    }

    // ================= LAPORAN =================
    JPanel laporanPage() {
        JPanel wrap = pageWrap("📑 Laporan Transaksi");

        mdlReport = new DefaultTableModel(
                new String[]{"Tanggal","Total","Metode","Bayar","Kembali"},0);
        tblReport = table(mdlReport);

        wrap.add(new JScrollPane(tblReport),BorderLayout.CENTER);
        return wrap;
    }

    // ================= LOGIC =================
    void tambahProduk(){
        String nama = JOptionPane.showInputDialog(this,"Nama Produk");
        if(nama==null) return;
        int harga = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Harga"));
        products.add(new Product(nama,harga));
        refreshProduk();
    }

    void editProduk(){
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        Product p = products.get(r);
        p.nama = JOptionPane.showInputDialog(this,"Nama",p.nama);
        p.harga = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Harga",p.harga));
        refreshProduk();
    }

    void hapusProduk(){
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        products.remove(r);
        refreshProduk();
    }

    void tambahKeranjang() {
        int r = tblProduk.getSelectedRow();
        if(r==-1) return;
        int qty = Integer.parseInt(
                JOptionPane.showInputDialog(this,"Jumlah beli"));
        Product p = products.get(r);
        cart.add(new CartItem(p.nama,qty,qty*p.harga));
        mdlCart.addRow(new Object[]{p.nama,qty,qty*p.harga});
        updateTotal();
    }

    void bayar() {
        int total = getTotal();
        if(total==0) return;

        String[] metode = {"Tunai","Debit"};
        String pilih = (String) JOptionPane.showInputDialog(
                this,"Metode Pembayaran","Bayar",
                JOptionPane.QUESTION_MESSAGE,null,metode,metode[0]);

        if(pilih==null) return;
        int bayar = total, kembali = 0;

        if(pilih.equals("Tunai")){
            bayar = Integer.parseInt(
                    JOptionPane.showInputDialog(this,"Total Rp "+total+"\nBayar"));
            if(bayar<total){
                JOptionPane.showMessageDialog(this,"Uang kurang");
                return;
            }
            kembali = bayar-total;
        }

        mdlReport.addRow(new Object[]{
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                total,pilih,bayar,kembali
        });

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

    // ================= COMPONENT =================
    JPanel cardPanel(int w,int h){
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(16,16,16,16)
        ));
        if(w>0) p.setPreferredSize(new Dimension(w,h));
        return p;
    }

    JTable table(DefaultTableModel m){
        JTable t = new JTable(m);
        t.setRowHeight(26);
        t.setFont(FONT);
        return t;
    }

    JTextField inputField(String hint){
        JTextField f = new JTextField(hint);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(203,213,225)),
                new EmptyBorder(8,10,8,10)
        ));
        return f;
    }

    JPasswordField passwordField(){
        JPasswordField p = new JPasswordField();
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(203,213,225)),
                new EmptyBorder(8,10,8,10)
        ));
        return p;
    }

    JButton btn(Color c,String t){
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(FONT);
        b.setFocusPainted(false);
        return b;
    }

    JButton smallBtn(String icon,String text,Color c){
        JButton b = new JButton(icon+" "+text);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(FONT);
        b.setFocusPainted(false);
        return b;
    }

    // ================= MODEL =================
    static class Product {
        String nama; int harga;
        Product(String n,int h){nama=n;harga=h;}
    }
    static class CartItem {
        String nama; int qty, subtotal;
        CartItem(String n,int q,int s){nama=n;qty=q;subtotal=s;}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KasirModern::new);
    }
}
