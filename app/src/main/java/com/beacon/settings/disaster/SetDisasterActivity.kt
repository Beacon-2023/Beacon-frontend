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
    private lateinit var binding : ActivitySetDisasterBinding
    private lateinit var disasterRepository: DataRepository

    var DataList = arrayListOf(
        DisasterData(R.drawable.icon_check_mark, "호우", 0),
        DisasterData(R.drawable.icon_check_mark, "대설", 1),
        DisasterData(R.drawable.icon_check_mark, "지진", 0),
        DisasterData(R.drawable.icon_check_mark, "태풍", 1),
        DisasterData(R.drawable.icon_check_mark, "산불", 0),
        DisasterData(R.drawable.icon_check_mark, "민방위", 0),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disasterRepository = DataRepository(AppDatabase.getDatabase(this).dataDao())

        CoroutineScope(Dispatchers.Main).launch {
            val isCheckedList = disasterRepository.getIsCheckedList()
            Log.d("재난", isCheckedList.toString())

            // DataList을 반복문을 통해 업데이트
            for (i in 0 until minOf(DataList.size, isCheckedList.size)) {
                DataList[i].ischecked = isCheckedList[i]
            }

            // DataList가 업데이트되었으므로 UI 요소를 설정
            binding = ActivitySetDisasterBinding.inflate(layoutInflater)
            var view = binding.root
            setContentView(view)

            binding.listview.adapter = ListViewAdapter(this@setDisasterActivity, DataList)

            binding.btnBack.setOnClickListener {
                super.onBackPressed()
            }
        }
    }

}