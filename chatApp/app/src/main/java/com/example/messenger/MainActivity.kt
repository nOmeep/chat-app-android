package com.example.messenger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText

    private lateinit var createAccountTextView: TextView

    private lateinit var mainLogInButton :Button

    private var selectedPhotoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        initViews()

        createAccountTextView.setOnClickListener {
            openRegisterDialog()
        }

        mainLogInButton.setOnClickListener {
            performLogIn(emailEditText.text.toString(), passwordEditText.text.toString())
        }

    }

    // Some kind of initiations

    private fun initViews() {
        emailEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        createAccountTextView = findViewById(R.id.createAccountTextView)

        mainLogInButton = findViewById(R.id.mainLogInButton)
    }

    // Photo suction

    private lateinit var circleImage : CircleImageView
    private lateinit var dialogChoosePhotoButton : Button
    private lateinit var dialogRegisterName : EditText

    private val getPickedPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { pickedUri ->
        selectedPhotoUri = pickedUri

        circleImage.setImageURI(pickedUri)

        if (pickedUri != null) {
            dialogChoosePhotoButton.visibility = View.INVISIBLE
        }
    }

    // Firebase and Dialogs

    private fun openRegisterDialog() {
        val view = View.inflate(this@MainActivity, R.layout.register_dialog_layout, null)

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val dialogLoginEditText : EditText = view.findViewById(R.id.dialogRegisterLoginEditText)
        val dialogPasswordEditText : EditText = view.findViewById(R.id.dialogRegisterPasswordEditText)
        val dialogRegisterButton : Button = view.findViewById(R.id.dialogRegisterRegisterButton)
        dialogChoosePhotoButton = view.findViewById(R.id.dialogRegisterChoosePhotoButton)
        circleImage = view.findViewById(R.id.dialogSelectedPhotoInRegister)
        dialogRegisterName = view.findViewById(R.id.dialogRegisterNameEditText)


        dialogChoosePhotoButton.setOnClickListener {
            getPickedPhoto.launch("image/*")
        }

        dialogRegisterButton.setOnClickListener {
            if (performRegister(dialogLoginEditText.text.toString(), dialogPasswordEditText.text.toString(), dialogRegisterName.text.toString())) {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun performRegister(email : String, password: String, name : String) : Boolean {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Fill the gaps before register", Toast.LENGTH_SHORT).show()
            return false
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                Toast.makeText(baseContext, "Created: ${task.result?.user?.uid}", Toast.LENGTH_SHORT).show()

                uploadImageToStorage()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        return true
    }

    private fun performLogIn(email : String, password : String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill the gaps before log in", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication success", Toast.LENGTH_SHORT).show()

                    // 152th line clears activity's stack
                    val messagesActivityIntent = Intent(this, MessagesActivity::class.java)
                    messagesActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(messagesActivityIntent)
                } else {
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
    }

    // Uploading image to firebase
    private fun uploadImageToStorage() {
        if (selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString()

        val storageReference = Firebase.storage.getReference("/images/$fileName")
        val putFileAction = storageReference.putFile(selectedPhotoUri!!)
        putFileAction.addOnSuccessListener { file ->
            Log.d("EASY", "IMAGE ${file.metadata?.path}")

            storageReference.downloadUrl.addOnSuccessListener {
                Log.d("EASY", "File loc $it")

                saveUserToDataBase(it.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(baseContext, "Failed to load file", Toast.LENGTH_SHORT).show()
        }
    }

    // Saving to our custom database on Firebase
    private fun saveUserToDataBase(profileImage: String) {
        val uid = Firebase.auth.uid ?: ""

        val ref = Firebase.database.getReference("/users/$uid")

        val user = User(uid, dialogRegisterName.text.toString(), profileImage)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("USERS", "We saved user to firebase database")
            }.addOnFailureListener {
                Log.d("USERS", "We failed to save user to firebase database")
                Toast.makeText(baseContext, "Failed to add user to database", Toast.LENGTH_SHORT).show()
            }
    }
}







