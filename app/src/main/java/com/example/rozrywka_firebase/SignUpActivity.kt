package com.example.rozrywka_firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("Sign Up");

        SignUp_button.setOnClickListener {
            val email = SignUp_mail.text.toString()
            val password = SignUp_password.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Please fill all fields!!!", Toast.LENGTH_SHORT).show()
                SignUp_mail.text?.clear()
                SignUp_password.text?.clear()
            }
            if(password.length < 6){
                Toast.makeText(applicationContext, "Minimum password length: 6 characters!!!", Toast.LENGTH_SHORT).show()
                SignUp_password.text?.clear()
            }
            else {
                createAccount(email, password)
            }
        }

        BackLogin_button.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext, "User created successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
