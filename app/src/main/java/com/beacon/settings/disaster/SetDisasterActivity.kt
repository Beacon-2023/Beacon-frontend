package com.beacon.settings.disaster

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import android.util.Log
import com.beacon.R
import com.beacon.data.AppDatabase
import com.beacon.databinding.ActivitySetDisasterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.beacon.data.*

class setDisasterActivity : BaseActivity() {
    private lateinit var binding: ActivitySetDisasterBinding
    private lateinit var disasterRepository: DataRepository
    private lateinit var listViewAdapter: ListViewAdapter
    private val dataList = ArrayList<DisasterData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDisasterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disasterRepository = DataRepository(AppDatabase.getDatabase(this).dataDao())
        listViewAdapter = ListViewAdapter(this, dataList)
        binding.listview.adapter = listViewAdapter

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        // CoroutineScope(Dispatchers.Main) 내에서 UI 업데이트 및 데이터 처리
        CoroutineScope(Dispatchers.Main).launch {
            val isCheckedList = disasterRepository.getIsCheckedList()
            Log.d("재난", isCheckedList.toString())

            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_heavy_rain), isCheckedList[0]))
            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_heavy_snow), isCheckedList[1]))
            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_Earthquake), isCheckedList[2]))
            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_Typhoon), isCheckedList[3]))
            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_forest_fires), isCheckedList[4]))
            dataList.add(DisasterData(R.drawable.icon_check_mark, getString(R.string.disas_civil_defense), isCheckedList[5]))

            listViewAdapter.notifyDataSetChanged() // 데이터 변경 알림
        }
    }
}
