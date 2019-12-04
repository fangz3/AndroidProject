package com.example.gameprojectdemov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        register_btn_register.setOnClickListener {
            val username = register_username.text.toString()
            val password = register_password.text.toString()
            val passwordRetype = register_confirm_password.text.toString()

            if (password != passwordRetype) {
                Toast.makeText(baseContext, "Passwords must match", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            username + " has been registered",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }

        register_btn_cancel.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
