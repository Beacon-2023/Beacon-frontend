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
                binding.txtSetDiasasterPhrase1.setText("호우 가이드라인")
            }
            1 -> {
                binding.txtSetDiasasterPhrase1.setText("대설 가이드라인")
            }
            2 -> {
                binding.txtSetDiasasterPhrase1.setText("지진 가이드라인")
            }
            3 -> {
                binding.txtSetDiasasterPhrase1.setText("태풍 가이드라인")
            }
            4 -> {
                binding.txtSetDiasasterPhrase1.setText("산불 가이드라인")
            }
            5 -> {
                binding.txtSetDiasasterPhrase1.setText("민방위 가이드라인")
            }
        }
    }
}
