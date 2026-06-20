package com.frostdev.sukamus.activities;

import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.frostdev.sukamus.R;
import com.frostdev.sukamus.database.KamusHelper;
import com.frostdev.sukamus.model.ModelKamus;
import com.frostdev.sukamus.utils.PreferencesManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

// TODO: Tambahkan UI/UX baru di sini
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load data kamus ke SQLite saat pertama kali install
        new LoadData().execute();
    }

    /**
     * Membaca file raw (english_indonesia / indonesia_english) dan
     * memasukkan semua data ke SQLite database saat pertama kali app dijalankan.
     * Hanya berjalan SEKALI — setelah itu data sudah ada di database.
     */
    private class LoadData extends AsyncTask<Void, Void, Void> {

        KamusHelper kamusHelper;
        PreferencesManager preferencesManager;

        @Override
        protected void onPreExecute() {
            kamusHelper = new KamusHelper(getApplicationContext());
            preferencesManager = new PreferencesManager(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean firstRun = preferencesManager.getFirstTimeLoad();
            if (firstRun) {
                ArrayList<ModelKamus> kamusEnglish = preLoadRaw(R.raw.english_indonesia);
                ArrayList<ModelKamus> kamusIndonesia = preLoadRaw(R.raw.indonesia_english);

                try {
                    kamusHelper.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                kamusHelper.insertTransaction(kamusEnglish, true);
                kamusHelper.insertTransaction(kamusIndonesia, false);
                kamusHelper.close();

                preferencesManager.setFirstTimeLoad(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO: Tampilkan UI utama setelah data siap
        }
    }

    /**
     * Membaca file raw (tab-separated) dan mengembalikan list ModelKamus.
     * Format file: [kata]\t[terjemahan]
     */
    public ArrayList<ModelKamus> preLoadRaw(int rawResId) {
        ArrayList<ModelKamus> listKamus = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(rawResId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2) {
                    listKamus.add(new ModelKamus(parts[0], parts[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listKamus;
    }
}
