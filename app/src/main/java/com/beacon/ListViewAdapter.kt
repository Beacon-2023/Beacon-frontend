package com.beacon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

// 프로필사진(이미지뷰), 이름(텍스트뷰)
// 이미지를 숫자로 참조하기 위해 int로 받음
class Data(val profile: Int, val name: String)

class ListViewAdapter(val context: Context, val DataList: ArrayList<Data>) : BaseAdapter() {

    override fun getCount() = DataList.size

    override fun getItem(position: Int) = DataList[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_disasater, null)
        val profile = view.findViewById<ImageView>(R.id.checkbox)
        val name = view.findViewById<TextView>(R.id.txt_disaster)
        val data = DataList[position]

        profile.setImageResource(data.profile)
        name.text = data.name
        return view
    }

}