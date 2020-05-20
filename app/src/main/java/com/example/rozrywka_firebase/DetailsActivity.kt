package com.example.rozrywka_firebase

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_details.*


class DetailsActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setTitle("Details");

        email = intent?.getStringExtra("EMAIL")!!
        val login = email.split("@",".")

        val mTitle = intent.getStringExtra("title")
        val mAuthor = intent.getStringExtra("author")
        val mISBN = intent.getStringExtra("isbn")
        val mYear = intent.getStringExtra("year")
        val mPages = intent.getStringExtra("pages")
        val mLanguage = intent.getStringExtra("language")
        val mNotes = intent.getStringExtra("notes")
        val mRead = intent.getStringExtra("read")!!

        details_Title.setText(mTitle)
        details_Author.setText(mAuthor)
        details_Isbn.setText(mISBN)
        details_Year.setText(mYear)
        details_Pages.setText(mPages)
        details_Language.setText(mLanguage)
        details_Notes.setText(mNotes)

        if(mRead == "1") {
            details_Read.setChecked(true)
        }
        else{
            details_Read.setChecked(false)
        }

        val firebase = FirebaseDatabase.getInstance()
        databaseReference = firebase.getReference(login[0] + login[1] + login[2])
        val query: Query = databaseReference.orderByChild("isbn").equalTo(mISBN)

        val result: HashMap<String, Any> = HashMap()

        details_Read.setOnClickListener{
            query.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError){
                    Toast.makeText(applicationContext, "Database Error", Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(dataSnapshot: DataSnapshot){
                    for (record in dataSnapshot.children) {
                        if(record.child("read").getValue().toString() == "1") {
                            result["read"] = "0"
                        }
                        else{
                            result["read"] = "1"
                        }
                        record.ref.updateChildren(result)
                        break
                    }
                    finish()
                }
            })
        }

        Delete_button.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Warning")
            builder.setMessage("Are you sure to delete this book?")
            builder.setPositiveButton("YES"){_, _ ->
                query.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(databaseError: DatabaseError){
                        Toast.makeText(applicationContext, "Database Error", Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot){
                        for (record in dataSnapshot.children) {
                            record.ref.removeValue()
                        }
                        Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
            }
            builder.setNeutralButton("Cancel"){_,_ ->
            }
            val dialog: AlertDialog = builder.create()

            dialog.show()
        }

        details_Notes.setMovementMethod(ScrollingMovementMethod())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == R.id.logout_action){
            firebaseAuth.signOut()
            firebaseAuth.addAuthStateListener {
                if(firebaseAuth.currentUser == null){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            return true
        }

        else if(id == R.id.add_action){
            val intent = Intent(this, AddActivity::class.java).apply{
                putExtra("EMAIL", email)
            }
            startActivity(intent)
            return true
        }
        else{
            val intent = Intent(this, AfterLoginActivity::class.java).apply{
                putExtra("EMAIL", email)
            }
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
