package com.nasupe.gamequest

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val IMAGE_PICK_CODE = 1000

class FirstFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    // Declaraciones de vistas
    private lateinit var imageViewProfile: ImageView
    private lateinit var buttonSelectPhoto: Button
    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextDOB: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var buttonSaveData: Button

    private var userData: Map<String, Any>? = null     // Variable miembro para almacenar los datos del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño de fragmento_first.xml en la vista
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        // Obtener referencias a vistas
        imageViewProfile = view.findViewById(R.id.imageViewProfile)
        buttonSelectPhoto = view.findViewById(R.id.buttonSelectPhoto)
        editTextName = view.findViewById(R.id.editTextName)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextDOB = view.findViewById(R.id.editTextDOB)
        spinnerGender = view.findViewById(R.id.spinnerGender)
        buttonSaveData = view.findViewById(R.id.buttonSaveData)

        // Configurar el click listener para el botón "Seleccionar foto"
        buttonSelectPhoto.setOnClickListener {
            selectImageFromGallery()
        }

        // Configurar el click listener para el botón "Guardar datos"
        buttonSaveData.setOnClickListener {
            saveData()
        }

        // Configurar el click listener para el bton de recursos guardados
        val buttonSaveGames: Button = view.findViewById(R.id.buttonSaveGames)
        buttonSaveGames.setOnClickListener {
            val intent = Intent(requireContext(), SavedGamesActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    // Método para seleccionar una imagen de la galería
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Método llamado después de seleccionar una imagen de la galería
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                Log.d("ActivityResult", "Image URI: $imageUri")
                imageViewProfile.setImageURI(imageUri)
                // Subir la imagen a Firebase Storage y guardar la URL en Firestore
                uploadImageToFirebaseStorage(imageUri)
            } else {
                Log.e("ActivityResult", "Image URI is null")
                Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para subir la imagen a Firebase Storage y guardar la URL en Firestore
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("profile_images")
                .child("$userId.jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtener la URL de descarga de la imagen
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Guardar la URL de la imagen en Firestore
                        saveProfileImageUrlToFirestore(uri)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error al subir la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Método para guardar la URL de la imagen de perfil en Firestore
    private fun saveProfileImageUrlToFirestore(imageUrl: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val newData = hashMapOf(
                "profileImageUrl" to imageUrl.toString()
            )

            val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocument.set(newData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "URL de la imagen de perfil guardada exitosamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error al guardar la URL de la imagen de perfil: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Método para guardar los datos del perfil
    private fun saveData() {
        if (isAdded && context != null) {
         val user = FirebaseAuth.getInstance().currentUser
         val userId = user?.uid

        if (userId != null) {
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)
            val gender = spinnerGender.selectedItem?.toString() ?: ""
            val newData = hashMapOf(
                "name" to editTextName.text.trim().toString(),
                "description" to editTextDescription.text.trim().toString(),
                "dateOfBirth" to editTextDOB.text.trim().toString(),
                "email" to editTextEmail.text.trim().toString(),
                "gender" to gender
            )

            userDocument.set(newData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Datos actualizados exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar los datos: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                requireContext(),
                "Usuario no autenticado",
                Toast.LENGTH_SHORT
            ).show()
        }
        } else {
            Log.e(TAG, "Fragment not attached to a context")
        }
    }

    override fun onPause() {
        super.onPause()
        // Guardar los datos del perfil en Firestore
        saveData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Cargar los datos del perfil desde Firestore y mostrarlos en las vistas
        loadData()

        // También necesitas cargar la imagen de perfil si está disponible
        val profileImageUrl = userData?.get("profileImageUrl") as? String
        if (!profileImageUrl.isNullOrEmpty()) {
            // Crear una referencia al Firebase Storage
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl)

            // Descargar la imagen y establecerla en imageViewProfile
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                try {
                    Glide.with(requireContext())
                        .load(uri)
                        .into(imageViewProfile)
                } catch (e: Exception) {
                    Log.e("FirstFragment", "Error al cargar la imagen: ${e.message}")
                    // Si ocurre un error al cargar la imagen con Glide, mostrar la imagen predeterminada
                    imageViewProfile.setImageResource(R.drawable.default_profile_image)
                }
            }.addOnFailureListener { exception ->
                Log.e("FirstFragment", "Error al descargar la imagen desde Firebase Storage: ${exception.message}")
                // Si falla la descarga, mostrar la imagen predeterminada
                imageViewProfile.setImageResource(R.drawable.default_profile_image)
            }
        }
    }

    private fun loadData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.data
                        // Mostrar los datos del usuario en las vistas correspondientes
                        userData?.let {
                            editTextName.setText(it["name"] as? String ?: "")
                            editTextDescription.setText(it["description"] as? String ?: "")
                            editTextEmail.setText(it["email"] as? String ?: "")
                            editTextDOB.setText(it["dateOfBirth"] as? String ?: "")

                            // Cargar la foto de perfil si está disponible
                            val profileImageUrl = it["profileImageUrl"] as? String
                            if (!profileImageUrl.isNullOrEmpty()) {
                                // Cargar la imagen en un hilo aparte usando corutinas
                                lifecycleScope.launch {
                                    try {
                                        val bitmap = withContext(Dispatchers.IO) {
                                            // Realizar la operación de red en un hilo aparte
                                            val url = URL(profileImageUrl)
                                            val connection = url.openConnection() as HttpURLConnection
                                            connection.doInput = true
                                            connection.connect()
                                            val inputStream = connection.inputStream
                                            BitmapFactory.decodeStream(inputStream)
                                        }
                                        imageViewProfile.setImageBitmap(bitmap)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        // Manejar errores al cargar la imagen
                                        imageViewProfile.setImageResource(R.drawable.default_profile_image)
                                    }
                                }
                            } else {
                                imageViewProfile.setImageResource(R.drawable.default_profile_image)
                            }

                            // Cargar el sexo si está disponible
                            val gender = it["gender"] as? String
                            if (!gender.isNullOrEmpty()) {
                                // Buscar la posición del sexo en la lista de opciones
                                val genderOptions = resources.getStringArray(R.array.gender_options)
                                val genderPosition = genderOptions.indexOf(gender)
                                // Seleccionar la opción correspondiente en el Spinner
                                spinnerGender.setSelection(genderPosition)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores al cargar los datos
                    Toast.makeText(requireContext(), "Error al cargar los datos: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }

        // Leer el JSON de los juegos guardados
        val managerSavedGames = ManagerSavedGames(requireContext())
        val savedGames = managerSavedGames.getSavedGames()
        Log.d("FirstFragment", "JSON de juegos guardados: $savedGames")
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

