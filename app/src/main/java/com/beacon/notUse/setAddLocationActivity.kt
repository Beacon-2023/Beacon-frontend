package com.beacon.notUse

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beacon.databinding.ActivitySetAddLocationBinding

class setAddLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddHome.setOnClickListener {
            val intent = Intent(this, setAddAdressActivity::class.java)
            startActivity(intent)
        }
    }
}