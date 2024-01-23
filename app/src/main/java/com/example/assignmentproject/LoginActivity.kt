package com.example.assignmentproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.assignmentproject.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    val auth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient : GoogleSignInClient
    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        registerActivityForGoogleSignIn()


        loginBinding.loginButton.setOnClickListener {

//            val userName = loginBinding.nameText.text.toString()

            val userEmail = loginBinding.emailText.text.toString()
            val userPassword = loginBinding.passwordText.text.toString()

            signInUser(userEmail,userPassword)
        }
//
//        loginBinding.googleSignInButton.setOnClickListener {
//
//            signInGoogle()
//
//        }
    }

    fun signInUser(userEmail: String, userPassword : String) {

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->

            if (task.isSuccessful) {
 //               Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MapsActivity::class.java)
  //              intent.putExtra(MapsActivity.USERNAME, userName)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if(user != null){
 //           Toast.makeText(applicationContext,"Welcome ",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity,MapsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInGoogle(){

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("666464501269-27iellp7l5riitqp5tcs1k88c07o51en.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        signIn()

    }

    private fun signIn(){

        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun registerActivityForGoogleSignIn(){

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->

                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == RESULT_OK && data != null){

                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignWithGoogle(task)
                }
            })
    }

    private fun firebaseSignWithGoogle(task: Task<GoogleSignInAccount>){

        try{
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        }catch (e : ApiException){
            Toast.makeText(applicationContext,e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }


    private fun firebaseGoogleAccount(account : GoogleSignInAccount){

        val authCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->

            if (task.isSuccessful){

                val user = auth.currentUser
                updateUI(user)
            }

        }
    }

    private fun updateUI(user: FirebaseUser?) {

        if(user != null) {
            startActivity(Intent(this,MapsActivity::class.java))
            finish()
        }
        else {
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
    }

}