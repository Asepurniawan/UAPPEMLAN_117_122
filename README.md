🧾 Aplikasi Kasir Modern (Java Swing)

Aplikasi Kasir Modern berbasis Java Swing dengan tampilan UI modern, fitur lengkap kasir dan admin, serta penyimpanan data menggunakan file CSV (tanpa database). Cocok untuk pembelajaran GUI Java, CRUD, dan simulasi sistem kasir desktop.

✨ Fitur Utama
🔐 Login System

Login Admin default

username: admin
password: admin


Login karyawan dari file employee.csv

Validasi login anti-gagal (aman untuk demo)

🖥️ Halaman Kasir

Pencarian produk real-time

Tampilan produk berbentuk card/grid

Keranjang belanja:

Tambah / kurang jumlah

Hapus item

Hitung total otomatis

Pembayaran tunai

Hitung kembalian otomatis

Simpan transaksi ke laporan

Jam live real-time di sidebar

📦 Halaman Admin Produk

Tabel produk

CRUD Produk:

Tambah

Edit

Hapus

Pencarian produk

Data tersimpan otomatis ke produk.csv

📊 Halaman Laporan

Riwayat transaksi

Menampilkan:

Waktu transaksi

Total pendapatan

Data diambil dari laporan.csv

🧠 Teknologi yang Digunakan

Java SE

Java Swing (GUI)

AWT

CSV File Storage

CardLayout

Stream API

OOP (Object-Oriented Programming)

📂 Struktur File
📁 project/
│
├── KasirModern.java        # Class utama aplikasi
├── produk.csv              # Data produk
├── laporan.csv             # Data transaksi
├── employee.csv            # Data login karyawan
│
└── README.md               # Dokumentasi proyek

🚀 Cara Menjalankan Aplikasi
1️⃣ Compile
javac KasirModern.java

2️⃣ Run
java KasirModern


⚠️ Pastikan semua file .csv berada di folder yang sama dengan file .java

🧪 Data Default Otomatis

Saat pertama kali dijalankan:

Jika produk.csv belum ada, aplikasi akan membuat data dummy otomatis

Admin default selalu tersedia, meskipun file employee kosong

🧩 Penjelasan Arsitektur Singkat

KasirModern
JFrame utama + pengatur seluruh halaman

CardLayout
Mengatur perpindahan halaman:

Kasir

Produk

Laporan

Inner Class

Product → data barang

Employee → data login

CartItem → item keranjang

CSV Handling

Tanpa database

Mudah dipahami & diedit manual

📸 Tampilan Aplikasi

UI modern (warna soft, layout rapi)

Sidebar navigasi icon

Tampilan responsif desktop

👨‍💻 Author

Gilang Saputra
Asep Kurniawan

📄 Lisensi

Project ini dibuat untuk tujuan pembelajaran dan bebas dikembangkan lebih lanjut.