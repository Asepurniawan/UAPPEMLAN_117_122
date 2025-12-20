# 🛒 POS Minimarket Kasir (Java Swing)

Aplikasi **Point of Sale (POS) Kasir Minimarket** berbasis **Java Swing** dengan tampilan modern, berwarna, dan fitur lengkap seperti kasir minimarket pada umumnya.

Aplikasi ini dibuat dalam **1 file Java**, tanpa MouseAdapter, dan seluruh event menggunakan **ActionListener** sehingga aman, stabil, dan mudah dipahami.

---

## 🎯 Fitur Utama

### 🔐 Login
- Login kasir
- Validasi username & password
- Tampilan login modern dengan logo

### 🧾 Transaksi Kasir
- Tambah produk ke keranjang
- Hitung total otomatis
- Checkout transaksi
- Pilih metode pembayaran:
  - Tunai
  - Debit
- Hitung kembalian otomatis
- Reset transaksi setelah pembayaran

### 📦 Manajemen Produk (CRUD)
- Tambah produk
- Edit produk
- Hapus produk
- Data produk langsung terupdate di tabel

### 📑 Laporan Transaksi
- Menyimpan riwayat transaksi
- Menampilkan:
  - Tanggal & waktu
  - Total belanja
  - Metode pembayaran
  - Jumlah bayar
  - Kembalian

### ⏰ Live Clock
- Jam dan tanggal realtime
- Terlihat di header aplikasi

---

## 🎨 Tampilan UI

- Desain modern dan berwarna
- Sidebar navigasi
- Dashboard kasir
- Header dengan logo kasir
- Card layout (rapi & profesional)
- Ikon emoji sebagai identitas POS

---

## ⚙️ Teknologi yang Digunakan

- Java SE
- Java Swing (GUI)
- AWT
- OOP (Object Oriented Programming)
- MVC sederhana (data & UI terpisah secara logika)

---

## 🚫 Tanpa MouseAdapter

> Aplikasi ini **TIDAK menggunakan MouseAdapter atau MouseListener**

✔ Semua event menggunakan `ActionListener`  
✔ Aman dari error mouse  
✔ Stabil saat dijalankan  
✔ Cocok untuk kebutuhan akademik

---

## ▶️ Cara Menjalankan Aplikasi

1. Pastikan Java sudah terinstall  
   ```bash
   java -version
