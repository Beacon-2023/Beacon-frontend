package com.beacon.settings.disaster

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.beacon.R
import com.beacon.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 프로필사진(이미지뷰), 이름(텍스트뷰)
// 이미지를 숫자로 참조하기 위해 int로 받음
class Data(val profile: Int, val name: String, var ischecked: Int)

class ListViewAdapter(val context: Context, val DataList: ArrayList<Data>) : BaseAdapter() {
    override fun getCount() = DataList.size

    override fun getItem(position: Int) = DataList[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_disasater, null)
        val profile = view.findViewById<ImageView>(R.id.checkbox)
        val name = view.findViewById<TextView>(R.id.txt_disaster)
        val data = DataList[position]

        if (data.ischecked == 1) {
            profile.setImageResource(data.profile)
        }
        else if (data.ischecked == 0) {
            profile.setImageResource(R.drawable.icon_blank_circle)
        }
        name.text = data.name

        view.setOnClickListener {
            // 아이템을 클릭하면 체크 상태를 토글합니다.
            DataList[position].ischecked =
                if (data.ischecked == 0) 1
                else 0

            val dataDao = AppDatabase.getDatabase(this.context).dataDao()
            val dataRepository = DataRepository(dataDao)

            CoroutineScope(Dispatchers.IO).launch {
                dataRepository.updateIsCheckedValue(position+1, 1)
                //Log.d("재난", "$position")
            }

            notifyDataSetChanged() // 어댑터에게 업데이트된 데이터를 알려줍니다.


        }


        return view
    }

}