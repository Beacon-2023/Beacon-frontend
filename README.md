
# beacon
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/5ff74f37-4c92-45fd-84f9-aed16a0f7c3e)

지역별 실시간 재난 속보 및 안전 대응 앱 <br/>


## UI
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/82611312-d398-4137-bdfa-5e6eae0e7cb8)

- [UI 프로토타입](https://www.figma.com/file/VZmBL1pLq7F9duVavv523g/Beacon?type=design&mode=design&t=s6PSmorZWrUhLXNN-0)

##  💡 프로젝트 동기
기존의 재난알림은 3가지 문제가 존재
  
- [1] 내용
	- 일상적 내용동시에 수신되는 내용 중복
	- 유사한 내용
	- 재난과 무관한 내용
	- 안전한 곳이 어디인지 알려주지 않는 등 내용 불명확

- [2] 수신범위
	- 현 위치에 맞지 않음

- [3] 언어
	- 외국인이 이해 못함

##  📍 기능
### 1. 실시간 해당 지역  재난 감시
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/66f07146-f8f9-4c1b-9f2d-d951a5b2cb54)

### 2. 재난에 따른 가이드라인을 스스로 커스텀 가능
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/632b1386-2509-4eda-9607-7b357b231360)

### 3. 재난 발생 시 기본 가이드라인 및 최근접 대피소를 제공
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/c4f1d9cb-655a-48b4-b7ea-5540f8ebd7b8)

### 4. 5개국 언어 지원
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/160762ee-725c-4596-8223-32231580b735)

## 로직 및 사용된 기술
### 재난상황 인지 로직
[국민재난안전포털](https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679) 에 실시간으로 올라오는 재난문자를 파이썬을 이용해 스크래핑합니다. 그 후 JSON형태로 클라이언트에 보낼 수 있도록 필요한 정보만을 재가공하여 사용자에게 재난 사항을 알려줍니다.

![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/948ca410-dea4-4c09-b511-f113d394759f)


### 싱글톤 패턴을 이용한 통신
싱글톤 패턴을 적용한 OkHttp통신을 이용해 한 개의 인스턴스를 유지하며 통신합니다.
```
object MyOkHttpClient {
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    val instance: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()
}

```

### 구조 
FE : Kotlin<br/>
![image](https://github.com/Beacon-2023/Beacon-frontend/assets/60930743/90192b2a-b975-43c8-a421-e7e08b246084)

BE : Spring, Mysql, python<br/>
Use : GitHub, Slack, FCM..etc <br/>


## Getting started
#### 설치

1.  저장소를 복제합니다.<br/>
    `git clone https://github.com/Beacon-2023/Beacon-frontend.git` 
    
2.  프로젝트 디렉터리로 이동합니다.<br/>
`cd Beacon-frontend`
#### 사용법

1.  프로젝트를 빌드
   
        ./gradlew build` 
        
2.  프로젝트 실행
      
		 ./gradlew run`


-   이제 프로젝트가 실행되는 것을 볼 수 있습니다.

## 라이센스

MIT &copy; [NoHack](mailto:lbjp114@gmail.com)<br/>
앱 아이콘 및 PPT이미지 [flaticon](https://www.flaticon.com/kr/)<br/>
메인 소개 이미지 [Pixabay-memyselfaneye](https://pixabay.com/photos/caution-cone-orange-traffic-white-389408/)

<!-- Stack Icon Refernces -->
