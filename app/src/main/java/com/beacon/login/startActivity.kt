package com.beacon.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.beacon.NaviActivity
import com.beacon.databinding.ActivityStartBinding
import com.beacon.signup.signUpActivity

class startActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    val MY_PERMISSION_ACCESS_ALL = 100

    companion object {
        const val DENIED = "denied"
        const val EXPLAINED = "explained"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        var view = binding.root
        setContentView(view)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Request ignore battery optimizations
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }

        // 백그라운드 퍼미션
        val permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 0)
        }

        //버튼 리스너
        binding.btnSignin.setOnClickListener {
            val intent = Intent(this, signInActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, signUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnNologin.setOnClickListener {
            val intent = Intent(this, NaviActivity::class.java)
            startActivity(intent)
        }

        binding.btnEraseWork.setOnClickListener {
            Log.d("테스트", "지금까지 대기열에 있는 작업을 삭제합니다.")
            val workManager = WorkManager.getInstance(this)
            workManager.cancelAllWork()
        }
    }

    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 단순히 권한이 거부 되었을 때
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                }
            }
            else -> {
                // 모든 권한이 허가 되었을 때
            }
        }
    }

}
