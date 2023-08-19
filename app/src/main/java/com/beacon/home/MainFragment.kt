package com.beacon.home

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beacon.R
import com.beacon.databinding.FragmentSettingsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class mainFragment : Fragment(), OnMapReadyCallback {
    var TAG: String = "로그"
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    //<---------------네이버지도------------->
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // 지도에 대한 뷰 역할만을 담당하는 프래그먼트 객체 얻기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        // 프래그먼트(MapFragment)의 getMapAsync() 메서드로 OnMapReadyCallback 을 등록하면
        // => 비동기로 NaverMap 객체를 얻을 수 있다고 한다.
        mapFragment.getMapAsync(this)

        // FusedLocationProviderClient + 지자기 + 가속도 센서를 활용해 최적의 위치를 반환하는 구현체인 FusedLocationSource를 제공
        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
    }

    //NaverMap 객체가 준비되면 => OnMapReady() 콜백 메서드 호출
    override fun onMapReady(naverMap: NaverMap) {
        Log.d(TAG, "onMapReady 시작")
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()) //gps 자동으로 받아오기
        setUpdateLocationListner() //내위치를 가져오는 코드
    }

    //내 위치를 가져오는 코드
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.

    //셋업데이트로케이션 리스너..좌표를 가져온다
    @SuppressLint("MissingPermission")
    fun setUpdateLocationListner() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //높은 정확도
            interval = 1000 //1초에 한번씩 GPS 요청
        }

        //location 요청 함수 호출 (locationRequest, locationCallback)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d(TAG, "${location.latitude}, ${location.longitude}")

                    //binding.curAddress.setText("${location.latitude}, ${location.longitude}")
                    Log.d(TAG, "주소 구하기 시작")
                    getAddress(location.latitude, location.longitude)
                    setLastLocation(location)
                }
            }
        }

        //좌표계를 주기적으로 갱신
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun getAddress(latitude: Double, longitude: Double) {
        // Geocoder 선언
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)

        Log.d(TAG, "getAddress함수 실행")
        // 안드로이드 API 레벨이 33 이상인 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude, longitude, 1
            ) { address ->
                if (address.size != 0) {
                    Log.d(TAG, "getAddress함수 : 33이상")
                    // 반환 값에서 전체 주소만 사용한다.
                    // getAddressLine(0)
                    //Log.d(TAG, "변환된 주소 : ${address[0].getAddressLine(0)}")
                }
            }
        } else { // API 레벨이 33 미만인 경우
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                Log.d(TAG, "getAddress함수 : 33이하")
                //.d(TAG, "변환된 주소 : ${addresses[0].getAddressLine(0)}")
            }
        }
    }


    //마커 생성 & 카메라 위치 이동
    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        //마커 객체를 생성하는
        val marker = Marker()
        //마커 좌표 지정(필수)
        marker.position = myLocation
        marker.map = naverMap
        //마커 텍스트 => marker.captionText = "현재 위치"
        marker.setCaptionAligns(Align.Top)
        marker.width = Marker.SIZE_AUTO
        marker.height = Marker.SIZE_AUTO
        marker.isHideCollidedMarkers = true

        val cameraPosition = CameraPosition(
            LatLng(location.latitude, location.longitude), // 대상 지점
            24.0, // 줌 레벨
        )

//        마커
//        cameraUpdate => 카메라를 어떻게 움직일지를 나타내는 CameraUpdate 객체를 생성해야 합니다. CameraUpdate는 카메라를 이동할 위치, 방법 등을 정의하는 클래스
//        scrollTo => 카메라의 대상 지점을 지정한 좌표로 변경합니다.
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 10.0

        //marker.map = null
    }

    //권한 관련
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.d(TAG, "MainActivity - onRequestPermissionsResult")
        //권한 여부 확인
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨 => Mode none
                Log.d(TAG, "MainActivity - onRequestPermissionsResult 권한 거부됨")
                naverMap.locationTrackingMode = LocationTrackingMode.None
            } else {
                Log.d(TAG, "MainActivity - onRequestPermissionsResult 권한 승인됨")
                naverMap.locationTrackingMode = LocationTrackingMode.Face // 권한 승인 => 현위치 버튼 컨트롤 활성
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            mainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}