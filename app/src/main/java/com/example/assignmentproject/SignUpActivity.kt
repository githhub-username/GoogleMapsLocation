package com.example.assignmentproject

import PhoneOtpActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.assignmentproject.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {

 //           val userName = binding.nameText.text.toString()

            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()

            signupWithFirebase(email, password)
        }

        binding.loginButton.setOnClickListener {

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding.phoneText.setOnClickListener {
            val intent = Intent(this@SignUpActivity, PhoneOtpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signupWithFirebase(email: String, password : String){

        binding.progressBarSignUp.visibility = View.VISIBLE
        binding.button.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Your account has been created", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MapsActivity::class.java)
          //      intent.putExtra(MapsActivity.USERNAME, userName)
                startActivity(intent)

 //               Toast.makeText(applicationContext,"Welcome ${userName}", Toast.LENGTH_SHORT).show()

                finish()
                binding.progressBarSignUp.visibility = View.INVISIBLE
                binding.button.isClickable = true
            }
            else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()

                binding.button.isClickable = true
            }
        }
    }


//    override fun onStart() {
//        super.onStart()
//
//        val user = auth.currentUser
//        if(user != null){
//  //          Toast.makeText(applicationContext,"Welcome ", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this,MapsActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
}