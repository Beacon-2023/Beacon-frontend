package com.beacon

import com.beacon.basicStart.BaseActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.beacon.databinding.ActivityNaviBinding
import com.beacon.home.mainFragment
import com.beacon.message.messageFragment
import com.beacon.settings.settingsFragment

private const val TAG_MESSAGE = "message_fragment"
private const val TAG_HOME = "main_fragment"
private const val TAG_SETTINGS = "settings_fragment"

class NaviActivity : BaseActivity() {

    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Beacon)
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, mainFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, mainFragment())
                R.id.messageFragment -> setFragment(TAG_MESSAGE, messageFragment())
                R.id.settingsFragment -> setFragment(TAG_SETTINGS, settingsFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val message = manager.findFragmentByTag(TAG_MESSAGE)
        val home = manager.findFragmentByTag(TAG_HOME)
        val settings = manager.findFragmentByTag(TAG_SETTINGS)

        if (message != null){
            fragTransaction.hide(message)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (settings != null) {
            fragTransaction.hide(settings)
        }

        if (tag == TAG_MESSAGE) {
            if (message!=null){
                fragTransaction.show(message)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_SETTINGS){
            if (settings != null){
                fragTransaction.show(settings)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}