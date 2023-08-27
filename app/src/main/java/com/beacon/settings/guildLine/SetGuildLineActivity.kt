package com.beacon.settings.guildLine

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import com.beacon.R
import com.beacon.databinding.ActivitySetGuildLineBinding
import com.beacon.settings.disaster.DisasterData
import com.beacon.settings.disaster.ListViewAdapter

class setGuildLineActivity : BaseActivity() {
    private lateinit var binding : ActivitySetGuildLineBinding
    var frameData = arrayListOf(
        DisasterData(R.drawable.icon_check_mark, "호우", 0),
        DisasterData(R.drawable.icon_check_mark, "대설", 0),
        DisasterData(R.drawable.icon_check_mark, "지진", 0),
        DisasterData(R.drawable.icon_check_mark, "태풍", 0),
        DisasterData(R.drawable.icon_check_mark, "산불", 0),
        DisasterData(R.drawable.icon_check_mark, "민방위", 0),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_guild_line)
        binding = ActivitySetGuildLineBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.listview.adapter = ListViewAdapter_GulidLine(this@setGuildLineActivity, frameData)
    }
}