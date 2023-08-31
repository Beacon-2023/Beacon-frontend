package com.beacon.settings.guildLine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beacon.R
import com.beacon.basicStart.BaseActivity
import com.beacon.databinding.ActivityAddCustomGulidLineBinding
import com.beacon.databinding.ActivityStartBinding

class AddCustomGulidLineActivity : BaseActivity() {
    private lateinit var binding: ActivityAddCustomGulidLineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomGulidLineBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val clickedIndex = intent.getIntExtra("Idx", -1)

        when (clickedIndex) {
            0 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_heavy_rain)
            }
            1 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_heavy_snow)
            }
            2 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_Earthquake)
            }
            3 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_Typhoon)
            }
            4 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_forest_fires)
            }
            5 -> {
                binding.txtSetDiasasterPhrase1.text = getString(R.string.disas_civil_defense) + " 가이드라인"
            }
        }
    }
}
