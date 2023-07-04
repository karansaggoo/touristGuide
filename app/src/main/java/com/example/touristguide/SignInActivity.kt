package com.example.touristguide

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.touristguide.api.LocationHelper
import com.example.touristguide.databinding.ActivitySignInBinding
import com.example.touristguide.model.UserRepository
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private val TAG = this.javaClass.canonicalName
    private lateinit var binding: ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var prefs: SharedPreferences
    private lateinit var userRepository : UserRepository
    private lateinit var locationHelper: LocationHelper
    var validData = true
    var email = ""
    var password = ""
    var acctype = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.locationHelper = LocationHelper.instance
        this.locationHelper.checkPermissions(this)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        prefs=applicationContext.getSharedPreferences(packageName, MODE_PRIVATE)
        userRepository = UserRepository(applicationContext)
        acctype = prefs.getString("USER_ACCOUNT_TYPE","").toString()

        if(prefs.contains("USER_EMAIL")){
            if( acctype=="customer"){
                goToMain()
            }
            if (acctype=="guide"){
                goToGuideProfile()
            }
//            goToMain()
        }
        else{
            binding.signInButton.setOnClickListener {

                validateData()
                saveToPrefs(email)
                userRepository.getDocID(email)
                userRepository.getName(email)
                clearField()

            }
        }

        binding.signUpInSignIn.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun validateData() {
        if (binding.emailEt.text.toString().isEmpty()) {
            binding.emailEt.error = "Email Cannot be Empty"
            validData = false
        } else {
            email = binding.emailEt.text.toString()
        }
        if (binding.passET.text.toString().isEmpty()) {
            binding.passET.error = "Password Cannot be Empty"
            validData = false
        } else {
            password = binding.passET.text.toString()
        }
        if (validData) {
            signIn(email, password)
        } else {
            Toast.makeText(this, "Please provide correct inputs", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: Sign In Successful")
                    if( acctype=="customer"){
                        goToMain()
                    }
                    if (acctype=="guide"){
                        goToGuideProfile()
                    }
//                    goToMain()
                } else {
                    Log.e(TAG, "onComplete: Sign In Failed", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication Failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun goToMain() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
    private fun goToGuideProfile(){
//        val mainIntent = Intent(this, MainActivity::class.java)
//        startActivity(mainIntent)
//        finish()
    }
    private fun clearField(){
        binding.emailEt.setText("")
        binding.passET.setText("")

    }

    private fun saveToPrefs(email: String){
        prefs.edit().putString("USER_EMAIL", email).apply()
    }
}