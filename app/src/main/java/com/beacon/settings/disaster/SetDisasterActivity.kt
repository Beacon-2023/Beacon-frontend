package com.beacon.settings.disaster

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import com.beacon.R
import com.beacon.databinding.ActivitySetDisasterBinding

class setDisasterActivity : BaseActivity() {
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