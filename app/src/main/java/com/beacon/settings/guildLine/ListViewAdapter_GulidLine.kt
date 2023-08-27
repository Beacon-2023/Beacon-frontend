package com.beacon.settings.guildLine

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.beacon.R
import com.beacon.data.*
import com.beacon.settings.disaster.DisasterData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewAdapter_GulidLine(val context: Context, val DataList: ArrayList<DisasterData>) : BaseAdapter() {
    override fun getCount() = DataList.size

    override fun getItem(position: Int) = DataList[position]

    override fun getItemId(position: Int) = 0L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.listview_disasater, null)
        //val profile = view.findViewById<ImageView>(R.id.checkbox)
        val name = view.findViewById<TextView>(R.id.txt_disaster)
        val data = DataList[position]

        name.text = data.name

        view.setOnClickListener {
            val intent = Intent(context, AddCustomGulidLineActivity::class.java)
            intent.putExtra("Idx", position)
            context.startActivity(intent)
        }

        return view
    }

}