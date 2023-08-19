package com.beacon.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beacon.databinding.ActivitySignUpBinding
import com.beacon.notUse.setLocationActivity

class signUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, setLocationActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        //binding.btnNext.isEnabled = false

        var Id = binding.inputId.text.toString()
        var pw = binding.inputPw.text.toString()

//        if(Id.length >= 6 && pw.length >= 6){
//            binding.btnNext.isEnabled = true
//            binding.btnNext.setBackgroundResource(R.drawable.border_radius_box)
//        }
    }
}