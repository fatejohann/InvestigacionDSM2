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

                            //  mensaje (200)
                            Toast.makeText(this@MainActivity, "api conectada: codigo 200", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // mostrar mensaje según el codigo
                        runOnUiThread {
                            when (response.code()) {
                                400 -> Toast.makeText(this@MainActivity, "Error: Solicitud incorrecta (400)", Toast.LENGTH_LONG).show()
                                500 -> Toast.makeText(this@MainActivity, "Error: Error del servidor (500)", Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(this@MainActivity, "Error: Código ${response.code()}", Toast.LENGTH_LONG).show()
                            }
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

        // Refrescar las fotos al hacer clic en el botón y mostrar mensajes de estado
        refreshButton.setOnClickListener {
            Toast.makeText(this, "Intentando conectar con la API...", Toast.LENGTH_SHORT).show()
            fetchPhotos()
        }
    }
}
