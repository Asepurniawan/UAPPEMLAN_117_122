<<<<<<< HEAD
=======
package com.kasir;
>>>>>>> 382eb3f (Gilang dan Asep Update maven)

import java.io.*;
import java.util.*;
import javax.swing.*;

public class DataManager {
    private static final String FILE_PRODUK = "produk.csv";
    private static final String FILE_TRANSAKSI = "transaksi.csv";
    private static final String FILE_EMPLOYEES = "employees.csv";
    private static final String CSV_HEADER_PRODUK = "ID,Nama Produk,Harga,Stok";
    private static final String CSV_HEADER_TRANSAKSI = "Tanggal,ID Produk,Nama Produk,Jumlah,Total Harga";

    // --- Load Data Produk ---
    public static ArrayList<Product> loadProducts() {
        ArrayList<Product> list = new ArrayList<>();
        File file = new File(FILE_PRODUK);
        if (!file.exists()) return list;

        try (Scanner scanner = new Scanner(file)) {
            // Skip header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                // Simple CSV parsing (handle basic cases)
                String[] parts = parseCSVLine(line);
                if (parts.length == 4) {
                    try {
                        list.add(new Product(
                            parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())
                        ));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        return list;
    }

    // --- Load Data Karyawan ---
    public static ArrayList<Employee> loadEmployees() {
        ArrayList<Employee> list = new ArrayList<>();
        File file = new File(FILE_EMPLOYEES);
        if (!file.exists()) return list;

        try (Scanner scanner = new Scanner(file)) {
            // Skip header
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = parseCSVLine(line);
                if (parts.length >= 3) {
                    list.add(new Employee(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
        return list;
    }

    // --- Save Data Produk (Rewrite dengan Header CSV) ---
    public static void saveProducts(ArrayList<Product> products) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PRODUK))) {
            // Tulis header CSV
            writer.println(CSV_HEADER_PRODUK);
            
            // Tulis data produk
            for (Product p : products) {
                writer.println(String.format("\"%s\",\"%s\",%.2f,%d",
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getStock()
                ));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data produk!");
        }
    }

    // --- Append Transaksi ke File CSV ---
    public static void saveTransaction(String transactionDetail) {
        File file = new File(FILE_TRANSAKSI);
        boolean fileExists = file.exists();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_TRANSAKSI, true))) {
            // Tulis header jika file belum ada
            if (!fileExists) {
                writer.println(CSV_HEADER_TRANSAKSI);
            }
            writer.println(transactionDetail);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan transaksi!");
        }
    }

    // --- Load History Transaksi ---
    public static ArrayList<String> loadHistory() {
        ArrayList<String> list = new ArrayList<>();
        File file = new File(FILE_TRANSAKSI);
        if (!file.exists()) return list;

        try (Scanner scanner = new Scanner(file)) {
            // Skip header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    list.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // --- Helper: Parse CSV Line (handle quoted values) ---
    private static String[] parseCSVLine(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
}
