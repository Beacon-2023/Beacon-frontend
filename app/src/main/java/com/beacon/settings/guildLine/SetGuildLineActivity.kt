package com.beacon.settings.guildLine

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import android.widget.Button
import com.beacon.R

class setGuildLineActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_guild_line)

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            super.onBackPressed()
        }
    }
}