package com.frostdev.sukamus.activities

import android.database.SQLException
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frostdev.sukamus.R
import com.frostdev.sukamus.database.KamusHelper
import com.frostdev.sukamus.model.ModelKamus
import com.frostdev.sukamus.utils.PreferencesManager
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var kamusHelper: KamusHelper
    private lateinit var adapter: KamusAdapter

    private lateinit var etSearch: EditText
    private lateinit var switchLanguage: Switch
    private lateinit var rvKamus: RecyclerView
    private lateinit var progressLoading: ProgressBar
    private lateinit var tvEmpty: TextView

    /** true = Indonesia (sesuai default switch checked di XML), false = English */
    private var isIndonesia = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.et_search)
        switchLanguage = findViewById(R.id.switch_language)
        rvKamus = findViewById(R.id.rv_kamus)
        progressLoading = findViewById(R.id.progress_loading)
        tvEmpty = findViewById(R.id.tv_empty)

        kamusHelper = KamusHelper(applicationContext)

        adapter = KamusAdapter(emptyList())
        rvKamus.layoutManager = LinearLayoutManager(this)
        rvKamus.adapter = adapter

        setupSearch()
        setupLanguageSwitch()

        // Load data kamus ke SQLite saat pertama kali install
        LoadData().execute()
    }

    private fun setupLanguageSwitch() {
        isIndonesia = switchLanguage.isChecked

        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            isIndonesia = isChecked
            // Refresh hasil sesuai bahasa yang baru dipilih
            performSearch(etSearch.text.toString())
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Mencari kata sesuai input dan bahasa yang sedang dipilih.
     * Dijalankan tiap kali teks di search bar berubah (real-time).
     */
    private fun performSearch(query: String) {
        kamusHelper.open()
        val results = if (query.isBlank()) {
            kamusHelper.selectAll(isIndonesia)
        } else {
            kamusHelper.selectByKata(query, isIndonesia)
        }
        kamusHelper.close()

        adapter.updateData(results)
        tvEmpty.visibility = if (results.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    /**
     * Membaca file raw (english_indonesia / indonesia_english) dan
     * memasukkan semua data ke SQLite database saat pertama kali app dijalankan.
     * Hanya berjalan SEKALI — setelah itu data sudah ada di database.
     */
    private inner class LoadData : AsyncTask<Void?, Void?, Void?>() {
        var preferencesManager: PreferencesManager? = null
        var firstRun: Boolean = false

        override fun onPreExecute() {
            preferencesManager = PreferencesManager(applicationContext)
            progressLoading.visibility = android.view.View.VISIBLE
        }

        override fun doInBackground(vararg voids: Void?): Void? {
            firstRun = preferencesManager!!.getFirstTimeLoad()
            if (firstRun) {
                val kamusEnglish = preLoadRaw(R.raw.english_indonesia)
                val kamusIndonesia = preLoadRaw(R.raw.indonesia_english)

                try {
                    kamusHelper.open()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }

                kamusHelper.insertTransaction(kamusEnglish, true)
                kamusHelper.insertTransaction(kamusIndonesia, false)
                kamusHelper.close()

                preferencesManager!!.setFirstTimeLoad(false)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            progressLoading.visibility = android.view.View.GONE
            // Tampilkan semua data sesuai bahasa default begitu data sudah siap
            performSearch(etSearch.text.toString())
        }
    }

    /**
     * Membaca file raw (tab-separated) dan mengembalikan list ModelKamus.
     * Format file: [kata]\t[terjemahan]
     */
    fun preLoadRaw(rawResId: Int): ArrayList<ModelKamus?> {
        val listKamus = ArrayList<ModelKamus?>()
        try {
            val inputStream = resources.openRawResource(rawResId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                val parts: Array<String?> =
                    line!!.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts.size >= 2) {
                    listKamus.add(ModelKamus(kata = parts[0], deskripsi = parts[1]))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listKamus
    }
}