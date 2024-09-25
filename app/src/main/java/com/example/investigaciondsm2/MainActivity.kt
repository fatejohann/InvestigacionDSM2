package com.example.investigaciondsm2

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        refreshButton = findViewById(R.id.refreshButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Método para obtener las fotos
        fun fetchPhotos() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: Response<List<Photo>> = apiService.getPhotos()
                    if (response.isSuccessful) {
                        val photos = response.body() ?: emptyList()
                        runOnUiThread {
                            photoAdapter = PhotoAdapter(photos)
                            recyclerView.adapter = photoAdapter
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error en la respuesta", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Cargar las fotos al iniciar
        fetchPhotos()

        // Refrescar las fotos al hacer clic en el botón
        refreshButton.setOnClickListener {
            fetchPhotos()
        }
    }
}
