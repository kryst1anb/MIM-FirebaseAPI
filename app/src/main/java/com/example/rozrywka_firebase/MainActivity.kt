package com.example.rozrywka_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)

        Login_button.setOnClickListener {
            val email = Login_mail.text.toString()
            val password = Login_password.text.toString()

            if(email.isNotEmpty()){
                if(password.isNotEmpty()) {
                    signIn(email, password)
                }
                else {
                    Toast.makeText(applicationContext, "Password is empty!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(applicationContext, "Login is empty!", Toast.LENGTH_SHORT).show()
            }
        }

        CreateAccount_button.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if(task.isSuccessful) {

                val intent = Intent(this, AfterLoginActivity::class.java).apply {
                    val eMAIL = Login_mail.text.toString()
                    putExtra("EMAIL", eMAIL)
                }
                startActivity(intent)
                Login_mail.text?.clear()
                Login_password.text?.clear()
            }
            else{
                Toast.makeText(applicationContext, "Wrong data, please try again!", Toast.LENGTH_SHORT).show()
                Login_mail.text?.clear()
                Login_password.text?.clear()
            }
        }

    }

}
