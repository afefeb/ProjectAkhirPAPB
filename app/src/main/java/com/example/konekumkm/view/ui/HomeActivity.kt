package com.example.konekumkm.view.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.konekumkm.data.local.AppDatabase
import com.example.konekumkm.data.local.entity.UMKM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val umkmDao = db.umkmDao()

        // State untuk menampilkan data di Compose
        val umkmList = mutableStateListOf<UMKM>()

        // Coroutine untuk insert + ambil data
        CoroutineScope(Dispatchers.IO).launch {
            // Insert sample data
            umkmDao.insertUMKM(UMKM(name = "UMKM Test 1"))
            umkmDao.insertUMKM(UMKM(name = "UMKM Test 2"))

            // Ambil semua data
            val data = umkmDao.getAllUMKM()
            umkmList.addAll(data) // update state untuk Compose
        }

        // Set UI Compose
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text("List UMKM:", style = MaterialTheme.typography.titleMedium)
                        umkmList.forEach {
                            Text("• ${it.name} (id=${it.id})", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
