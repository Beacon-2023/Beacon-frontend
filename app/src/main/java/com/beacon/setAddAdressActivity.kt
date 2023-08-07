package com.beacon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beacon.databinding.ActivitySetAddAdressBinding
import com.beacon.databinding.ActivitySignInBinding

class setAddAdressActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetAddAdressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAddAdressBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

    }
}