<h1 align="center"><b>시민 화폐, 광산</b></h1>

<p align="center">
    <h3 align="center">
        <b>광주광역시 광산구 주민 화합을 위한 지역 화폐 거래 관리 서비스<br>
       </b>
    </h3>
    <br>
    <p>
        <b>시민 화폐, 광산</b>는 광주광역시 광산구 주민 화합을 위해 <br>
        스타트업 동아리가 지역 화폐 서비스입니다.<br> 시민들은 광산구 지역 관계자들의 승인을 통해 회원가입할 수 있으며,<br> 인앱 가상화폐인 '광산'을 이용하여 지인들과 물건 혹은 서비스를 거래할 수 있습니다. 
    </p>
    <h3 align="center"><img width="700" height="400" alt="스크린샷 2025-11-29 오후 10 05 49" src="https://github.com/user-attachments/assets/172d82d5-b9df-4606-b51a-ed04c1257a66" /></h3>
</p>
<br>
<br>

<h2>
    Installation 🎁 
</h2>

- PlayStore: [시민 화폐, 광산](https://play.google.com/store/apps/details?id=com.school_of_company.expo_android)

<br>
<h2>
Architecture
</h2>
<img src = "https://user-images.githubusercontent.com/82383983/220412681-daafd612-8375-4496-86ea-286b4b05e169.png"/>

Android 공식문서에 서술된 [Android App Architecture](https://developer.android.com/topic/architecture?hl=ko#recommended-app-arch)를 기반으로 작성되었습니다.

* Minumun SDK 26
* Language: ```Kotlin```
* Async: ```Coroutine```
* DI: ```Dagger-Hilt```
* Network: ```Retrofit2```, ```OKhttp3```
* Image: ```Coil```
* AndroidX Jetpack
* CI, CD: ```Github action```
* Cooperation: ```Git```, ```Github```, ```GitFlow```
* Architecture: ```Google App Architecture```, ```MVVM```
<br>
<br>

## 🗂️ Packages
```
Expo Android
 ┣ 📂app
 ┃ ┣ 📂navigation
 ┃ ┣ 📂ui
 ┃ ┗ 📂activity
 ┣ 📂build-logic
 ┣ 📂core
 ┃ ┣ 📂common
 ┃ ┣ 📂data
 ┃ ┃ ┣ 📂di
 ┃ ┃ ┗ 📂repository
 ┃ ┣ 📂datastore
 ┃ ┃ ┣ 📂di
 ┃ ┃ ┗ 📂proto
 ┃ ┣ 📂design-system
 ┃ ┃ ┣ 📂component
 ┃ ┃ ┣ 📂icon
 ┃ ┃ ┣ 📂theme
 ┃ ┃ ┗ 📂util
 ┃ ┣ 📂model
 ┃ ┃ ┣ 📂enum
 ┃ ┃ ┣ 📂request
 ┃ ┃ ┣ 📂response
 ┃ ┃ ┗ 📂util
 ┃ ┣ 📂network
 ┃ ┃ ┣ 📂api
 ┃ ┃ ┣ 📂datasource
 ┃ ┃ ┣ 📂di
 ┃ ┃ ┣ 📂dto
 ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┗ 📂util
 ┃ ┗ 📂ui
 ┗ 📂feature
 ┃ ┗ 📂project element

```
