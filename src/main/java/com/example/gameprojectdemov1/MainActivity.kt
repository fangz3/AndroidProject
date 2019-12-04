package com.example.gameprojectdemov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var backgroundOne:ImageView
    private lateinit var backgroundTwo:ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        var svc = Intent(this, StartActivityMusic::class.java)
        startService(svc)
        login_btn_cancle.setOnClickListener {
            Intent()
        }
        login_btn_register.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        login_btn_login.setOnClickListener{
            val username = login_username.text.toString()
            val password = login_password.text.toString()
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this,NavigationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }

                }
        }





    }

}
