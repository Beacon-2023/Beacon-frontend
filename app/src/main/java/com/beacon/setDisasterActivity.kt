package com.beacon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.beacon.databinding.ActivitySetDisasterBinding
import com.beacon.databinding.ActivityStartBinding

class setDisasterActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySetDisasterBinding
    var DataList = arrayListOf(
        Data(R.drawable.icon_check_mark, "홍수"),
        Data(R.drawable.icon_check_mark, "지진"),
        Data(R.drawable.icon_check_mark, "산사태"),
        Data(R.drawable.icon_check_mark, "화산폭발")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDisasterBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        binding.listview.adapter = ListViewAdapter(this, DataList)
    }
}