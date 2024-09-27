package com.example.investigaciondsm2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
    private lateinit var postPhotoButton: Button
    private lateinit var photoTitleEditText: EditText
    private lateinit var photoUrlEditText: EditText
    private lateinit var searchPhotoEditText: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        refreshButton = findViewById(R.id.refreshButton)
        postPhotoButton = findViewById(R.id.postPhotoButton)
        searchPhotoEditText = findViewById(R.id.searchPhotoEditText)
        searchButton = findViewById(R.id.searchButton)
        photoTitleEditText = findViewById(R.id.photoTitle)
        photoUrlEditText = findViewById(R.id.photoUrl)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Método para obtener fotos
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

        // Método para postear una nueva foto
        fun postPhoto(photo: Photo) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response: Response<Photo> = apiService.postPhoto(photo)
                    if (response.isSuccessful) {
                        val newPhoto = response.body()
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Foto subida exitosamente", Toast.LENGTH_LONG).show()

                            // Agregar la nueva foto a la lista local si es necesario
                            if (newPhoto != null) {
                                val updatedPhotos = photoAdapter.photos.toMutableList()
                                updatedPhotos.add(newPhoto)
                                photoAdapter.updatePhotos(updatedPhotos)

                                findUploadedPhotoByTitleOrUrl(newPhoto.title, newPhoto.url)
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error al subir la foto", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Error en la conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Método para buscar una foto por título o URL
        searchButton.setOnClickListener {
            val searchQuery = searchPhotoEditText.text.toString()
            if (searchQuery.isNotBlank()) {
                findUploadedPhotoByTitleOrUrl(searchQuery, searchQuery)
            } else {
                Toast.makeText(this, "Por favor, introduce un título o URL para buscar", Toast.LENGTH_LONG).show()
            }
        }


        // Cargar las fotos al iniciar
        fetchPhotos()

        // Botón para refrescar fotos
        refreshButton.setOnClickListener {
            fetchPhotos()
        }

        // Botón para postear una nueva foto
        postPhotoButton.setOnClickListener {
            val title = photoTitleEditText.text.toString()
            val url = photoUrlEditText.text.toString()

            if (title.isNotBlank() && url.isNotBlank()) {
                val newPhoto = Photo(
                    albumId = 1,  // Puedes usar un ID fijo o generar uno
                    id = 0,  // El servidor normalmente asigna el ID
                    title = title,
                    url = url,
                    thumbnailUrl = url
                )
                postPhoto(newPhoto)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show()
            }
        }
    }
    // Método para buscar por título o URL
    fun findUploadedPhotoByTitleOrUrl(title: String, url: String) {
        val uploadedPhoto = photoAdapter.photos.find { it.title == title || it.url == url }
        if (uploadedPhoto != null) {
            Toast.makeText(this, "Foto encontrada: ${uploadedPhoto.title}", Toast.LENGTH_LONG).show()
            // Aquí puedes hacer algo como navegar a una vista detallada
        } else {
            Toast.makeText(this, "No se encontró la foto", Toast.LENGTH_LONG).show()
        }
    }
}
