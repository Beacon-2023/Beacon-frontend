package com.beacon.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beacon.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class messageFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //여기서 재난 관련 API를 사용해서 재난 정보를 받아오고 items들을 구성한다!!
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_main)
        val items = mutableListOf<CardViewItem>()

        val data = listOf(
            Triple("호우", "23년 9월 1일 10:20", "서울 지역 호우 주의"),
            Triple("대설", "23년 9월 1일 14:11", "서울 지역 대설 주의"),
            Triple("지진", "23년 9월 1일 15:33", "서울 지역 지진 주의"),
            Triple("태풍", "23년 9월 1일 16:11", "서울 지역 태풍 주의"),
            Triple("산불", "23년 9월 1일 20:12", "서울 지역 산불 주의")
        )

        for (triple in data) {
            val iconName = triple.first
            val timestamp = triple.second
            val message = triple.third

            // Map the iconName to the appropriate resource ID
            val iconResourceId = when (iconName) {
                "호우" -> R.drawable.icon_rain
                "대설" -> R.drawable.icon_heavysnow
                "지진" -> R.drawable.icon_earthquake
                "태풍" -> R.drawable.icon_typhoon
                "산불" -> R.drawable.icon_buring
                "민방위" -> R.drawable.icon_war
                else -> R.drawable.icon_nodisaster
            }

            val cardViewItem = CardViewItem(iconResourceId, timestamp, message)
            items.add(cardViewItem)
        }

        val adapter = CardViewAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //        val items = mutableListOf(
//            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 10분", "'서울' 지역에 '폭우' 경보"),
//            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 12분", "'서울' 지역에 '폭우' 경보"),
//            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 13분", "'서울' 지역에 '폭우' 경보"),
//            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 14분", "'서울' 지역에 '폭우' 경보"),
//            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 15분", "'서울' 지역에 '폭우' 경보")
//        )

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            messageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}