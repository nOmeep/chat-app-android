package com.example.messenger

import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText

    private lateinit var registerButton : Button
    private lateinit var choosePhotoImageButton : ImageButton

    private lateinit var alreadyHaveAnAccTextView : TextView

    private var selectedPhotoUri : Uri? = null

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        initViews()
        initOnCLickListeners()
    }

    // Some kind of inits

    private fun initViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        registerButton = findViewById(R.id.registerButton)
        choosePhotoImageButton = findViewById(R.id.choosePhotoImageButton)

        alreadyHaveAnAccTextView = findViewById(R.id.alreadyHaveAnAccTextView)

        auth = Firebase.auth
    }

    private fun initOnCLickListeners() {
        registerButton.setOnClickListener {
            performRegister()
        }

        alreadyHaveAnAccTextView.setOnClickListener {
            openLogInDialog()
        }

        choosePhotoImageButton.setOnClickListener {
            getAction.launch("image/*")
        }
    }

    // Photo suction

    private val getAction = registerForActivityResult(ActivityResultContracts.GetContent()) {   pickedUri ->
        selectedPhotoUri = pickedUri

        val source = ImageDecoder.createSource(this.contentResolver, pickedUri)
        val bitmap = ImageDecoder.decodeBitmap(source)

        val bitmapDrawable = BitmapDrawable(applicationContext.resources, bitmap)
        choosePhotoImageButton.background = bitmapDrawable
    }

    // Firebase and Dialogs

    private fun performRegister() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill the gaps before register", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MAIN", "Email: $email")
        Log.d("MAIN", "Password: $password")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                Toast.makeText(baseContext, "Created: ${task.result?.user?.uid}", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(baseContext, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openLogInDialog() {
        val view = View.inflate(this, R.layout.log_in_dialog, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val dialogLogin : EditText = view.findViewById(R.id.dialogLoginEditText)
        val dialogPassword : EditText = view.findViewById(R.id.dialogPasswordEditText)
        val dialogButton : Button = view.findViewById(R.id.dialogLogInButton)

        dialogButton.setOnClickListener {
            performLogIn(dialogLogin.text.toString(), dialogPassword.text.toString())
        }

        dialog.show()
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









