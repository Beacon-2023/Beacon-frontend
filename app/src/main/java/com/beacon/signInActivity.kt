package com.beacon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beacon.databinding.ActivitySignInBinding
import com.beacon.databinding.ActivitySignUpBinding

class signInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnSignup.setOnClickListener{
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }
    }
}