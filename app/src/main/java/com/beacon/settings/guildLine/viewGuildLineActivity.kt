package com.beacon.settings.guildLine

import android.R
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.beacon.databinding.ActivityViewGuildLineBinding


class viewGuildLineActivity : AppCompatActivity() {
    lateinit var binding : ActivityViewGuildLineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGuildLineBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        val listView = findViewById<ListView>(com.beacon.R.id.listView_safezone)

        //TODO : API로 가이드라인 받기
        //TODO :받아온 JSON형태를 풀어서 나타내자
        val rankItemList: MutableList<RankItem> = ArrayList()
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_1, "대피소1"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_2, "대피소2"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_3, "대피소3"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_4, "대피소4"))
        rankItemList.add(RankItem(com.beacon.R.drawable.icon_5, "대피소5"))

        val adapter = RankListAdapter(this, rankItemList)
        listView.adapter = adapter
    }
}