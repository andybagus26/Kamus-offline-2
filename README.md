# Kamus — English ↔ Indonesian Dictionary

Aplikasi kamus Android offline dua arah (Inggris ↔ Indonesia) yang bekerja **tanpa koneksi internet**.

---

## 🏗️ Status Project

> **UI sedang dalam proses redesign.**  
> Lapisan UI/UX lama telah dihapus. Hanya logika inti yang dipertahankan, siap untuk dibangun ulang dengan desain baru.

---

## 🧠 Arsitektur Inti

```
res/raw/ (data kamus mentah)
     ↓
DatabaseHelper   → setup SQLite (db_kamus)
     ↓
KamusHelper      → CRUD & pencarian kata  ← otak utama
     ↓
MainActivity     → placeholder UI (siap diisi desain baru)
```

---

## 📁 Struktur File

```
app/src/main/
├── java/com/frostdev/sukamus/
│   ├── activities/
│   │   └── MainActivity.java       ← placeholder, siap didesain ulang
│   ├── database/
│   │   ├── DatabaseContract.java   ← nama tabel & kolom
│   │   ├── DatabaseHelper.java     ← setup SQLite
│   │   └── KamusHelper.java        ← otak pencarian & CRUD ⭐
│   ├── model/
│   │   └── ModelKamus.java         ← model data: kata + terjemahan
│   └── utils/
│       └── PreferencesManager.java ← cek first run
└── res/
    ├── layout/
    │   └── activity_main.xml       ← layout kosong, siap diisi
    └── raw/
        ├── english_indonesia       ← data kamus Inggris → Indonesia
        └── indonesia_english       ← data kamus Indonesia → Inggris
```

---

## 🔧 Cara Pakai KamusHelper

```java
KamusHelper helper = new KamusHelper(context);
helper.open();

// Ambil semua kata (true = Inggris→Indo, false = Indo→Inggris)
ArrayList<ModelKamus> semua = helper.selectAll(true);

// Cari kata berdasarkan prefix
ArrayList<ModelKamus> hasil = helper.selectByKata("hello", true);

helper.close();
```

---

## 🛠️ Tech Stack

- **Bahasa:** Java
- **Platform:** Android
- **Database:** SQLite (via `SQLiteOpenHelper`)
- **Data:** File raw tab-separated (`.tsv`)

---

## 📌 Catatan Pengembang

- Data kamus di-load ke SQLite **hanya saat pertama kali install** menggunakan `AsyncTask` di `MainActivity`
- `PreferencesManager` menyimpan flag `FIRST_TIME_KEY` untuk mencegah reload berulang
- Pencarian menggunakan query `LIKE 'kata%'` (prefix match)
