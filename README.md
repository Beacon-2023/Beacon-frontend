
# beacon
![image](https://github.com/Boknami/beacon/assets/60930743/cdbde486-9b8f-4540-93cf-6fd000c77b9c)
지역별 실시간 재난 속보 및 안전 대응 앱 <br/>


## UI
![image](https://github.com/Boknami/beacon/assets/60930743/8ee2526f-1b0e-446c-9903-9eba25b9be78)
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
![image](https://github.com/Boknami/beacon/assets/60930743/abff315a-574f-4cf8-957b-ae919e5c2229)

### 2. 재난에 따른 가이드라인을 스스로 커스텀 가능
![image](https://github.com/Boknami/beacon/assets/60930743/1dba4d42-8e3e-4d8b-9341-c9fc6e1eb268)

### 3. 재난 발생 시 기본 가이드라인 및 최근접 대피소를 제공
![image](https://github.com/Boknami/beacon/assets/60930743/388d43b3-d4a8-4c7f-932e-69a213fe63e0)

### 4. 5개국 언어 지원
![image](https://github.com/Boknami/beacon/assets/60930743/27d5779b-abbf-4b5e-b3a2-11e58cf65f28)


## 로직 및 사용된 기술
### 재난상황 인지 로직
[국민재난안전포털](https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679) 에 실시간으로 올라오는 재난문자를 파이썬을 이용해 스크래핑합니다. 그 후 JSON형태로 클라이언트에 보낼 수 있도록 필요한 정보만을 재가공하여 사용자에게 재난 사항을 알려줍니다.

![image](https://github.com/Boknami/beacon/assets/60930743/1b8b7fa2-dd95-43c9-927a-8f090afdf13d)


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
