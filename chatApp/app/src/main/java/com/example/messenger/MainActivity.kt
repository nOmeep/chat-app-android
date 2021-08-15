package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    lateinit var nameEditText: EditText;
    lateinit var emailEditText : EditText;
    lateinit var passwordEditText: EditText;
    lateinit var registerButton : Button;
    lateinit var alreadyHaveAnAccTextView : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        initViews()
        initOnCLickListeners()
    }

    private fun initViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)
        alreadyHaveAnAccTextView = findViewById(R.id.alreadyHaveAnAccTextView)
    }

    private fun initOnCLickListeners() {
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("MAIN", "Email: $email")
            Log.d("MAIN", "Password: $password")
        }

        alreadyHaveAnAccTextView.setOnClickListener {
            openLogInDialog()
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
            Log.d("DIALOG", "ONCLICK")
        }

        dialog.show()
    }
}









