package com.example.messenger

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
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText

    private lateinit var createAccountTextView: TextView

    private lateinit var mainLogInButton :Button

    private var selectedPhotoUri : Uri? = null

    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
    }

    // Photo suction

    private lateinit var circleImage : CircleImageView
    private lateinit var dialogChoosePhotoButton : Button

    private val getPickedPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { pickedUri ->
        selectedPhotoUri = pickedUri

        circleImage.setImageURI(pickedUri)
        circleImage.bringToFront()
        dialogChoosePhotoButton.visibility = View.GONE
    }

    // Firebase and Dialogs

    private fun openRegisterDialog() {
        val view = View.inflate(this@MainActivity, R.layout.register_dialog_layout, null)

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val dialogRegisterName : EditText = view.findViewById(R.id.dialogRegisterNameEditText)
        val dialogLoginEditText : EditText = view.findViewById(R.id.dialogRegisterLoginEditText)
        val dialogPasswordEditText : EditText = view.findViewById(R.id.dialogRegisterPasswordEditText)
        val dialogRegisterButton : Button = view.findViewById(R.id.dialogRegisterRegisterButton)
        dialogChoosePhotoButton = view.findViewById(R.id.dialogRegisterChoosePhotoButton)
        circleImage = view.findViewById(R.id.dialogSelectedPhotoInRegister)


        dialogChoosePhotoButton.setOnClickListener {
            getPickedPhoto.launch("image/*")
        }

        dialogRegisterButton.setOnClickListener {
            if (performRegister(dialogLoginEditText.text.toString(), dialogPasswordEditText.text.toString())) {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun performRegister(email : String, password: String) : Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill the gaps before register", Toast.LENGTH_SHORT).show()
            return false
        }

        Log.d("MAIN", "Email: $email")
        Log.d("MAIN", "Password: $password")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                Toast.makeText(baseContext, "Created: ${task.result?.user?.uid}", Toast.LENGTH_SHORT).show()

                uploadImageToFirerbase()
            }
            .addOnFailureListener {
                Toast.makeText(baseContext, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        return true
    }

    private fun uploadImageToFirerbase() {

    }

    private fun performLogIn(email : String, password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication success", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
    }
}









