package com.beacon.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.beacon.R

class cardViewAdapter() :
    RecyclerView.Adapter<cardViewAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var itemimage: ImageView = itemView.findViewById(R.id.item_image)
        public var itemtitle: TextView = itemView.findViewById(R.id.item_title)
        public var itemdetail: TextView = itemView.findViewById(R.id.item_detail)
    }

    // 1. Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)

        return MyViewHolder(cardView)
    }

    // 2. Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemimage.setImageResource(R.drawable.icon_rain)
        holder.itemtitle.setText("8월 13일 23시 10분")
        holder.itemdetail.setText("'서울' 지역에 '폭우' 경보")
    }

    // 3. Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return 5
    }
}