[PRD] ChildMomentSnap: AI 기반 아이 성장 기록 서비스

1. 프로젝트 개요 (Project Overview)

ChildMomentSnap은 사진 분석(Vision)과 음성 인식(STT) 기술을 결합하여, 바쁜 부모들이 아이의 소중한 순간을 쉽고 감성적으로 기록할 수 있도록 돕는 안드로이드 애플리케이션입니다.

제품 비전: 기록의 부담을 없애고, 기술을 통해 추억의 가치를 증폭시킨다.

핵심 목표:

사진 촬영 직후의 생생한 감정을 음성으로 포착.

AI를 통한 자동 일기 초안 생성으로 작성 시간 단축.

피드와 캘린더가 결합된 직관적인 추억 탐색 경험 제공.

2. 타겟 사용자 및 사용자 페르소나 (Target Audience)

주 타겟: 0~7세 자녀를 둔 밀레니얼/Z세대 부모.

사용자 니즈:

아이의 사진은 많지만 정작 그 당시의 상황이나 감정을 글로 남기기엔 시간이 부족함.

인스타그램처럼 사진 중심의 UI에 익숙하지만, 개인적이고 안전한 기록 공간을 원함.

3. 사용자 스토리 (User Stories)

기록하기: "나는 아이의 결정적 순간을 찍은 뒤, 손으로 타이핑하지 않고 목소리만으로 그 상황을 일기로 완성하고 싶다."

둘러보기: "나는 오늘 일기를 쓰는 것뿐만 아니라, 지난 추억들을 인스타그램처럼 편하게 스크롤하며 회상하고 싶다."

찾아보기: "나는 특정 날짜에 무엇을 했는지 알고 싶을 때, 캘린더를 펼쳐서 해당 날짜의 기록을 즉시 찾고 싶다."

회고하기: "나는 우리 아이가 한 달 동안 주로 어떤 기분이었는지, 어디에 자주 갔는지 AI 리포트로 확인하고 싶다."

4. 기능적 요구사항 (Functional Requirements)

4.1. 홈 및 하이브리드 타임라인

피드 보기 (Primary): 사진이 강조된 카드 형태의 피드. 무한 스크롤 지원.

확장형 캘린더 (Secondary):

상단 주간(Week) 뷰 노출.

아래로 당기거나 버튼 클릭 시 월간(Month) 뷰로 확장.

날짜 클릭 시 해당 시점의 피드로 자동 스크롤 이동.

데이터 부재 시 (Empty State): 기록 유도 문구와 애니메이션 가이드 노출.

4.2. AI 모먼트 기록 (핵심 루프)

촬영/업로드: 인앱 카메라 또는 갤러리 선택.

음성 녹음: 촬영 직후 마이크 활성화. Google STT를 이용한 실시간 텍스트 프리뷰 제공.

AI 분석 및 생성:

Vision AI: 사물, 장소, 얼굴 표정 분석.

LLM Synthesis: Vision 데이터 + STT 텍스트를 조합해 다정한 문체의 일기 생성.

저장 및 편집: AI가 제안한 일기를 사용자가 최종 검토 및 수정하여 저장.

4.3. 인사이트 및 리포트

월간 테마 추출: AI가 한 달간의 기록을 분석하여 핵심 키워드(예: "성장의 달") 선정.

감정/장소 통계: 가장 많이 느낀 감정(Smile Score), 주요 방문 장소 데이터 시각화.

5. 비기능적 요구사항 (Non-functional Requirements)

사용성: 모든 핵심 액션은 한 손으로 조작 가능해야 함 (Bottom-heavy UI).

성능: AI 분석 및 일기 생성 시간은 최대 10초 이내여야 하며, 진행 상황을 시각적으로 노출(Skeleton UI 등).

안정성: 오프라인 상태에서도 기록 작성이 가능해야 하며(Local DB), 네트워크 연결 시 AI 분석 수행.

6. 기술 명세 (Technical Specifications)

### 개발 환경
- **OS**: Android (SDK 27-36)
- **언어**: Kotlin
- **빌드 시스템**: Gradle with Kotlin DSL
- **호환성**: Java 11

### UI 프레임워크
- **UI**: Jetpack Compose
- **디자인 시스템**: Material 3

### 아키텍처 패턴
- **아키텍처**: Clean Architecture + MVVM + MVI
- **모듈화**: Feature-based 모듈 구조
- **의존성 주입**: Hilt
- **상태 관리**: MVI 패턴 (단방향 데이터 플로우)
- **비동기 처리**: Kotlin Coroutines & Flow

### 데이터 레이어
- **로컬 DB**: Room
- **설정 관리**: DataStore
- **Repository 패턴**: 데이터 접근 추상화

### 외부 API
- **Google Cloud Vision API**: 이미지 분석 및 객체 인식
- **Google Cloud Speech-to-Text**: 음성 텍스트 변환 (한국어 지원)
- **Google Gemini API**: 일기 생성용 LLM

### 모듈 구조

#### Core 모듈
- **common**: 공통 유틸리티, 확장 함수
- **data**: Repository 패턴을 통한 데이터 접근 추상화
- **database**: Room 데이터베이스 관리
- **datastore**: 사용자 설정 및 프리퍼런스 관리
- **model**: 프로젝트 전반의 데이터 모델 정의
- **network**: 네트워크 통신 및 API 호출 관리
- **ui**: 재사용 가능한 Compose UI 컴포넌트

#### Feature 모듈
- **calendar**: 캘린더 화면 및 일기 목록 표시
- **diary**: 일기 작성, 편집, 삭제 기능
- **diary-detail**: 일기 상세 보기 및 CRUD 기능
- **home**: 앱 메인 홈 화면 (Bottom Navigation 관리)
- **moment**: 사진 촬영/갤러리 선택, 음성 녹음/변환 기능

### 네비게이션 아키텍처
- **Type-safe Navigation**: Compose Navigation을 활용한 타입 안전 화면 전환
- **화면 플로우**:
  - Home → Calendar (기본 화면)
  - Calendar → Diary Detail (날짜 선택)
  - Calendar → Diary Write (새 일기 작성)
  - Diary Write → Camera/Gallery (사진 추가)
  - Diary Write → Voice Recording (음성 녹음)

7. UI/UX 디자인 전략

Concept: Warm Minimalism

Color: Stone-50 (BG), Amber-500 (Point), Rose-400 (Recording)

Typography: Serif(제목/일기 본문), Sans-serif(시스템 UI)

Interaction:

녹음 시 실시간 오디오 비주얼라이저.

캘린더 확장/축소 시 부드러운 전환 애니메이션.

8. 성공 지표 (Success Metrics)

기록 완료율: 일기 작성 시작 후 중도 포기 없이 저장 버튼을 누르는 비율.

재방문율(Retention): 주간 3회 이상 앱을 열어 피드를 확인하는 비율.

AI 만족도: AI가 생성한 일기 초안을 수정하지 않고 그대로 저장하는 비율.

9. 미래 기능 계획 (Future Features)

### 위치 기반 육아 추천 서비스
- **날씨 연동**: 현재 날씨 정보를 기반으로 실내/실외 활동 추천
- **주변 시설 추천**: 아이와 함께 갈 수 있는 놀이터, 카페, 박물관 등 추천
- **거리 기반 필터링**: 사용자 위치 기반 근처 육아 시설 정보 제공

### AI 기반 최적 육아 스케줄링
- **패턴 분석**: 과거 일기 데이터를 분석하여 아이의 활동 패턴 파악
- **맞춤형 일정 제안**: 아이의 성향과 부모의 스케줄을 고려한 활동 제안
- **성장 단계별 추천**: 아이의 연령대에 맞는 발달 활동 추천

### 스마트 알림 및 리마인더
- **일기 작성 리마인더**: 사진을 찍은 후 일정 시간이 지나면 일기 작성 알림
- **추억 회상 알림**: 1년 전 오늘의 일기 알림 (On This Day)
- **중요 이벤트 리마인더**: 아이의 생일, 기념일 등 자동 알림