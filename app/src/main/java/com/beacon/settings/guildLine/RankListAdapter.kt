package com.beacon.settings.guildLine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.beacon.R


class RankListAdapter(private val context: Context, private val rankItemList: List<RankItem>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return rankItemList.size
    }

    override fun getItem(position: Int): Any {
        return rankItemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.listview_safezone, parent, false)
        }
        val rankImage = convertView!!.findViewById<ImageView>(R.id.rankImage)
        val rankName = convertView.findViewById<TextView>(R.id.rankName)
        val item: RankItem = rankItemList[position]

        // Set rank image and name here
        rankImage.setImageResource(item.getRankImageResource())
        rankName.text = item.getRankName()
        return convertView
    }
}