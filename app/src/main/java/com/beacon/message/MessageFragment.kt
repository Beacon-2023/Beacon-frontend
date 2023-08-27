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

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_main)
        val items = mutableListOf(
            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 10분", "'서울' 지역에 '폭우' 경보"),
            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 12분", "'서울' 지역에 '폭우' 경보"),
            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 13분", "'서울' 지역에 '폭우' 경보"),
            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 14분", "'서울' 지역에 '폭우' 경보"),
            CardViewItem(R.drawable.icon_rain, "8월 13일 23시 15분", "'서울' 지역에 '폭우' 경보")
        )

        val adapter = CardViewAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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