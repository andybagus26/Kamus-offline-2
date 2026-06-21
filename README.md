# Kamus — English ↔ Indonesian Dictionary

Aplikasi kamus Android offline dua arah (Inggris ↔ Indonesia) yang bekerja **tanpa koneksi internet**.

---

## 🏗️ Status Project

> **UI Redesign & Migrasi Kotlin Selesai.**  
> Struktur kode lama berbasis Java kini telah sepenuhnya dimigrasikan ke Kotlin. Tampilan UI baru yang bersih dan responsif telah diimplementasikan menggunakan `RecyclerView` untuk menampilkan hasil pencarian secara real-time.

---

## 🧠 Arsitektur Inti

```
res/raw/ (data kamus mentah)
     ↓
DatabaseHelper.kt   → setup SQLite (db_kamus)
     ↓
KamusHelper.kt      → CRUD & pencarian kata  ← otak utama
     ↓
MainActivity.kt     → Mengatur UI, memproses pencarian real-time,
                       dan inisialisasi data SQLite
     ↓
KamusAdapter.kt     → Mengikat & menampilkan hasil kata di RecyclerView
```

---

## 📁 Struktur File

```
app/src/main/
├── kotlin/com/frostdev/sukamus/
│   ├── activities/
│   │   ├── MainActivity.kt       ← Activity utama, inisialisasi data, & handling input
│   │   └── KamusAdapter.kt       ← Adapter RecyclerView untuk menampilkan daftar kata & arti
│   ├── database/
│   │   ├── DatabaseContract.kt   ← Definisi nama tabel (tb_inggris/tb_indonesia) & kolom
│   │   ├── DatabaseHelper.kt     ← Setup skema SQLite database
│   │   └── KamusHelper.kt        ← Helper query database (CRUD & Search) ⭐
│   ├── model/
│   │   └── ModelKamus.kt         ← Model data untuk item kata (id, kata, deskripsi)
│   └── utils/
│       └── PreferencesManager.kt ← Pengaturan SharedPreferences untuk pengecekan first-run
└── res/
    ├── layout/
    │   ├── activity_main.xml     ← Layout utama (Search bar, Language Switch, RecyclerView)
    │   └── item_kamus.xml        ← Desain layout untuk baris item di RecyclerView
    └── raw/
        ├── english_indonesia     ← Sumber data kamus Inggris → Indonesia (tab-separated)
        └── indonesia_english     ← Sumber data kamus Indonesia → Inggris (tab-separated)
```

---

## 🔧 Cara Pakai KamusHelper

```kotlin
val helper = KamusHelper(context)
helper.open()

// Ambil semua kata (true = Inggris ↔ Indonesia, false = Indonesia ↔ Inggris)
val semua = helper.selectAll(true)

// Cari kata berdasarkan prefix
val hasil = helper.selectByKata("hello", true)

helper.close()
```

---

## 🛠️ Tech Stack

- **Bahasa:** Kotlin (Migrasi penuh dari Java)
- **Platform:** Android
- **Database:** SQLite (via `SQLiteOpenHelper`)
- **Data:** File raw tab-separated (`.tsv`)
- **UI Components:** `RecyclerView` untuk penayangan data dinamis, `Switch` untuk pertukaran arah bahasa.

---

## 📌 Catatan Pengembang

- Data kamus di-load ke SQLite **hanya saat pertama kali install/dijalankan** menggunakan `AsyncTask` di `MainActivity.kt`.
- `PreferencesManager` menyimpan flag boolean `first_time_load` untuk mencegah reload data berulang pada pemuatan selanjutnya.
- Pencarian kata menggunakan query `LIKE 'kata%'` (prefix match) yang berjalan secara real-time saat pengguna mengetik di search bar (menggunakan `TextWatcher`).
