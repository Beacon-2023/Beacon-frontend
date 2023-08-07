package com.beacon

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
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