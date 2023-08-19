package com.beacon.settings.disaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beacon.R
import com.beacon.databinding.ActivitySetDisasterBinding
import com.beacon.settings.disaster.Data
import com.beacon.settings.disaster.ListViewAdapter

class setDisasterActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySetDisasterBinding
    var DataList = arrayListOf(
        Data(R.drawable.icon_check_mark, "홍수", 0),
        Data(R.drawable.icon_check_mark, "지진", 1),
        Data(R.drawable.icon_check_mark, "산사태", 0),
        Data(R.drawable.icon_check_mark, "화산폭발", 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDisasterBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.listview.adapter = ListViewAdapter(this, DataList)
    }
}