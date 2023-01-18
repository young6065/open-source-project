### < 오픈소스sw 주제정하기 >

오픈소스 - 채팅오픈소스

실제 제공되고있는 서비스를 따라만들고 배우면서 이해하고
나만의 채팅어플을 만들겠습니다.

Socket.io를 이용하여 실시간 양방향 통신 기능을 구현해볼생각입니다.
그러기에 가장 적합한 것이 채팅이라고 생각하였습니다.
그래서 가장 흔하게 사용하고 있는 카카오톡 채팅 기능을 구현해보려고 합니다.



### 중간보고 발표자료

주제변경이유 - 대학교 과목주제가 공개되어있는 오픈소스를 가져와 이해하고 더 발전시켜 보기인데 지금에 채팅오픈소스에서 무엇을 더 발전시켜야하는지 잘모르겠고 소스이해 하기도 아직은 좀 어렵습니다..

. 주제
- 회원가입 이메일 인증처리 구현 오픈소스를 이용한 모바일 신분증 만들기

. 참고 사이트 및 소스
- https://moonong.tistory.com/45 (사실 사이트 경로를..)

. 진행 중인 기존 소스와 차별점
- 기존 오픈소스는 회원가입시 본인 신원확인을 위한 이메일 인증소스 였습니다.
  저는 거기에 추가로 개인정보 저장 및 더 확실한 본인 인증확인을 위해 생체인식 인증을 추가하려고합니다.

___

# Mobile ID Card
> 모바일 신분증
대학교 프로젝트 기간중 뉴스를 보다가 모바일 운전면허증이 실용화 된다라는걸 보고 이부분을 만들면 재밌겠다 라는생각에 찾아보던중
이메일 인증이라는 오픈소스를 우연히 발견한후 그앱을 토대로 안드로이 스튜디오를 같이 공부하면 앱을 발전시켜 보았습니다.

# 목차
Ⅰ) 기술 스택

Ⅱ) 프로젝트 사용해보기

Ⅲ) 주요 기능

Ⅳ) UI/UX

Ⅴ) 시연 영상

Ⅵ) 소감

# Ⅰ) 기술 스택
### 사용언어
- Java
### 개발환경
- Android Studio


# Ⅱ) 프로젝트 사용해보기
- 코드 다운로드 후 안드로이드 스튜디오로 열면  바로 이용하실수있습니다.
- 다음 ID로 로그인하여 서비스 이용이 가능합니다.
     + ID : netmarble123@naver.com    
     + PW : 12341234     
     
     
     + ID : koy6414@naver.com
     + PW : 123123
     
- 회원가입을 통해서도 서비스 이용이 가능합니다.


# Ⅲ) 주요 기능
- 회원가입 화면 -> 회원가입은 최초 1회 이메일 인증을 한 후에 가입이 가능합니다. 본인이 가지고 있는 이메일을 가지고 앱에서 회원가입을 할 수 있습니다.
- 회원가입시 이메일 인증화면 -> 회원가입시 입력한 이메일에 본인인증 메일이 오면 링크를 통해 이메일 주소를 인증해야 회원가입이 완료가 됩니다.
- 로그인 화면 -> 회원가입을 하고 이메일 인증을 한 후 로그인을 하는 화면입니다.
- 지문인식 화면 -> 휴대폰 기기에 등록된 지문을 기반으로 사용자가 지문인증을 해야 이 앱을 사용할 수 있게 하였습니다.
- 개인정보 등록화면 -> 개인정보를 모바일 핸드폰에 입력후 저장하는 화면입니다.
- 법률안내 화면 -> 모바일 신분증에 관련한 중요한 법조항을 한눈에 볼수있게 만들어 보았습니다.


# Ⅳ) UI/UX
### 1.회원가입
> 회원가입 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213260007-46dea7c9-af55-4676-ad76-8f08668fcf3e.png)

### 2.이메일 인증
> 최초1회 이메일 인증 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213260482-d3f86907-5fa7-4282-ad49-e82f0fd1416f.png)
![image](https://user-images.githubusercontent.com/101784840/213260502-ddf0b5a2-e93e-4efe-aeeb-a61d46d4198e.png)

### 3.로그인
> 로그인 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213260612-3b168f6d-ff5a-4389-b7e9-3baf869b566e.png)

### 4.지문인식
> 지문인식 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213260682-b60dc0c9-1722-4078-af7b-2e441c6c72ab.png)  ![image](https://user-images.githubusercontent.com/101784840/213260737-2aa59c3a-3e3c-4cef-b059-a7c06eb3f250.png)

### 5.개인정보 등록
> 정보 등록 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213261074-5f03ad22-3944-4f12-84b7-7072c16b2764.png)  ![image](https://user-images.githubusercontent.com/101784840/213261086-d94adb6f-5da4-419e-8c54-4a67ee1696dc.png)

### 6.개인정보 등록
> 법률 화면입니다.

![image](https://user-images.githubusercontent.com/101784840/213261180-0b26d275-3bbd-4737-80cc-98b40a1b75cb.png)  ![image](https://user-images.githubusercontent.com/101784840/213261191-c800a9d1-f5f4-4b12-a4b2-e52ca0c0326b.png)


# Ⅴ) 시연 영상
> 시연 영상 입니다.

https://user-images.githubusercontent.com/101784840/213261329-01dddc06-69cf-465c-9b8f-218654b033f2.mp4

# Ⅵ) 소감
> 프로젝트 소감

이번 오픈소스 소프트웨어 프로젝트에서 짧지만 정말 많은걸 배울수 있었던 시간인였던 것 같습니다.
처음엔 무엇가를 만들어야 한다는 생각에 정말 머리가 아프고 무섭고 어떻게 해야할지 몰랐지만 하나하나 배우고 접하면서
코딩에 재미도 나름 붙였던 좋은 시간이였던것 같습니다.
이제 졸업좀 합시다...



