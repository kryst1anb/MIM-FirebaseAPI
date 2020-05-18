package com.example.rozrywka_firebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_after_login.*

class AfterLoginActivity: AppCompatActivity(){

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var email = ""
    lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var listOfBook: MutableList<Book>

    private var flagActivity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)
        flagActivity = 1

        recyclerView = findViewById(R.id.recyclerView_books)

        email = intent.getStringExtra("EMAIL")!!
        val login = email.split("@",".")

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.myToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Books";

        val firebase = FirebaseDatabase.getInstance()
        databaseReference = firebase.getReference(login[0] + login[1] + login[2])
        recyclerView.layoutManager = GridLayoutManager(applicationContext, 1)


        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError){
                Toast.makeText(applicationContext, "Database Error", Toast.LENGTH_SHORT).show()
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot){

                listOfBook = ArrayList()

                for(row in dataSnapshot.children){
                    val newRow = row.getValue(Book::class.java)
                    listOfBook.add(newRow!!)
                }

                if(listOfBook.isNotEmpty()) {
                    click_here.text = ""
                }else{
                    click_here.text = "Click (+) to add new book"
                }
                setupAdapter(listOfBook)
            }
        })

    }

    private fun setupAdapter(mutableData:MutableList<Book>){
        recyclerView.adapter = Adapter(mutableData, this, email)
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
            val intent = Intent(this, AddActivity::class.java).apply {

                putExtra("EMAIL", email)
            }

            startActivity(intent)
            return true
        }

        else{
            if(flagActivity != 1){

                val intent = Intent(this, AfterLoginActivity::class.java).apply {
                    putExtra("EMAIL", email)
                }

                startActivity(intent)
                return true
            }
            else{
                Toast.makeText(applicationContext, "You are on this page", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}