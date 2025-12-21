# ChildMomentSnap

**아이와 추억을 기록하는 일기 (Child Memory Recording Diary)**

ChildMomentSnap은 AI 기술을 활용하여 아이와의 소중한 순간들을 기록하고 관리할 수 있는 Android 애플리케이션입니다.

## 📱 프로젝트 개요

부모와 아이가 함께하는 일상의 소중한 순간들을 사진과 음성을 통해 기록하고, AI의 도움으로 더욱 풍성한 일기를 작성할 수 있는 앱입니다. 캘린더 기반의 직관적인 인터페이스를 통해 추억을 쉽게 찾아보고 관리할 수 있습니다.

## ✨ 핵심 기능

### 1. AI 기반 일기 작성
- **사진 분석**: Google Cloud Vision API를 통한 이미지 분석 및 텍스트 제안
- **음성 인식**: Google Cloud Speech-to-Text로 음성을 텍스트로 변환
- **지능형 제안**: 사진 분석과 음성 내용을 결합한 일기 작성 도움말

### 2. 캘린더 기반 메모리 관리
- **시각적 캘린더**: 일기가 있는 날짜 표시 및 사진 썸네일 미리보기
- **직관적 탐색**: 캘린더에서 날짜 클릭으로 해당 일기 조회/편집
- **완전한 CRUD**: 일기 생성, 조회, 수정, 삭제 기능

### 3. 미래 계획 기능
- 위치 기반 육아 추천 서비스 (날씨 연동)
- AI 분석 기반 최적 육아 스케줄링
- 스마트 알림 및 리마인더

## 🛠 기술 스택

### 개발 환경
- **언어**: Kotlin
- **UI 프레임워크**: Jetpack Compose + Material 3
- **빌드 시스템**: Gradle with Kotlin DSL
- **호환성**: Java 11, Android SDK 27-36

### 아키텍처
- **패턴**: Clean Architecture + MVVM + MVI
- **모듈화**: Feature-based 모듈 구조
- **의존성 주입**: Hilt
- **데이터베이스**: Room

### AI 서비스
- **Google Cloud Vision API**: 이미지 분석 및 객체 인식
- **Google Cloud Speech-to-Text**: 음성 텍스트 변환 (한국어 지원)

## 📁 프로젝트 구조

```
ChildMomentSnap/
├── app/                           # 메인 앱 모듈
├── core/                          # 핵심 모듈
│   ├── common/                    # 공통 유틸리티, 확장함수
│   ├── data/                      # 데이터 레이어 추상화 (Repository 포함)
│   ├── database/                  # Room 데이터베이스
│   ├── datastore/                 # SharedPreferences 대체
│   ├── model/                     # 데이터 모델 정의
│   ├── network/                   # 네트워크 레이어
│   └── ui/                        # 공통 UI 컴포넌트
└── feature/                       # 기능별 모듈
    ├── calendar/                  # 📅 달력 및 일기 목록 뷰
    ├── diary/                     # ✏️ 일기 작성/편집/삭제
    ├── my/                        # 📖 나의 정보 관리
    ├── home/                      # 🏠 홈 화면 (Bottom Navigation 관리)
    └── moment/                    # 📸🎤 사진 촬영/갤러리, 음성 녹음/변환
```

### 모듈별 역할

#### Core Modules
- **common**: 프로젝트 전반에서 사용되는 유틸리티와 확장 함수
- **data**: 리포지토리 패턴을 통한 데이터 접근 추상화 및 비즈니스 로직
- **database**: Room을 활용한 로컬 데이터베이스 관리
- **datastore**: 사용자 설정 및 프리퍼런스 관리
- **model**: 프로젝트 전반에서 사용되는 데이터 모델 정의
- **network**: 네트워크 통신 및 API 호출 관리 (Google Cloud AI 서비스 포함)
- **ui**: 재사용 가능한 Compose UI 컴포넌트

#### Feature Modules
- **calendar**: 메인 캘린더 화면과 일기 목록 표시
- **diary**: 일기 작성, 편집, 삭제 기능
- **dairy-detail**: 일기 상세 보기 및 CRUD 기능
- **home**: 앱 메인 홈 화면
- **photo**: 카메라 촬영 및 갤러리에서 사진 선택
- **voice**: 음성 녹음 및 텍스트 변환 기능

## 🏗 아키텍처 특징

### 모듈화 설계
- **기능별 분리**: 각 기능을 독립적인 모듈로 구성하여 확장성과 유지보수성 향상
- **Clean Architecture**: Core 모듈을 통한 계층형 아키텍처 적용
- **재사용성**: Core 모듈을 통한 공통 기능 재사용

### 상태 관리
- **MVI**: ViewModel과 UI State를 통한 단방향 데이터 플로우
- **Reactive Programming**: Kotlin Coroutines와 Flow를 활용한 비동기 처리

### 품질 관리
- **테스트**: JUnit(단위 테스트) + Espresso(UI 테스트)

---