package com.beacon.settings.guildLine

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import com.beacon.R
import com.beacon.databinding.ActivitySetGuildLineBinding
import com.beacon.settings.disaster.DisasterData
import com.beacon.settings.disaster.ListViewAdapter

class setGuildLineActivity : BaseActivity() {
    private lateinit var binding: ActivitySetGuildLineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGuildLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val frameData = arrayListOf(
            DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_heavy_rain), 0),
            DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_Earthquake), 0),
            DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_Typhoon), 0),
            DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_forest_fires), 0),
            DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_civil_defense), 0)
        )

        binding.listview.adapter = ListViewAdapter_GulidLine(this, frameData)
    }
}