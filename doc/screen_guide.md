# 화면 가이드 (Screen Guide)

## Task 1. 전역 디자인 시스템 구축 (core:ui)
모든 화면의 기초가 되는 디자인 토큰과 공통 컴포넌트를 정의합니다.

- [ ] **1.1. Color Palette 정의 (Color.kt)**
  - [ ] **Stone50 (#F9F8F6)**: 따뜻하고 편안한 느낌의 전체 배경색(Main Background) 정의
  - [ ] **Amber500 (#F59E0B)**: 앱의 아이덴티티를 나타내는 핵심 포인트 및 하이라이트 컬러(Primary) 정의
  - [ ] **Rose400 (#FB7185)**: 감성적인 포인트, '좋아요' 아이콘, 녹음 버튼 등 강조색(Accent) 정의
  - [ ] **Stone900 (#1C1917)**: 높은 가독성을 위한 메인 타이틀 및 중요 본문 텍스트용 정의
  - [ ] **Stone400 (#A8A29E)**: 부제목, 날짜 정보, 비활성 텍스트 등 보조 텍스트용 정의
  - [ ] Light/Dark Mode 대응을 위한 테마 컬러 시스템 구축

- [ ] **1.2. Typography 설정 (Type.kt)**
  - [ ] 본문용 Serif 폰트 리소스 추가 및 BodyLarge 설정 (다이어리용)
  - [ ] 시스템용 Sans-serif(Pretendard 등) 폰트 설정 (UI용)

- [ ] **1.3. 공통 UI 컴포넌트 제작**
  - [ ] `MomentsButton`: 둥근 모서리가 적용된 커스텀 버튼
  - [ ] `MomentsChip`: AI 태그용 칩 컴포넌트 (Stone100 배경)

## Task 2. 메인 구조 및 내비게이션 (feature:home)
앱의 외곽 틀과 화면 전환 로직을 구현합니다.

- [ ] **2.1. 메인 Scaffold 구현**
  - [ ] 전체 배경색 Stone50 적용 및 기본 레이아웃 구성

- [ ] **2.2. Bottom Navigation Bar 제작**
  - [ ] 피드(홈), 리포트 탭 아이콘 및 선택 상태 UI 구현
  - [ ] 중앙 플로팅 버튼(FAB) 디자인 및 배치

- [ ] **2.3. Top App Bar (Sticky Header)**
  - [ ] "Moments" 로고(Serif Italic) 및 프로필 버튼 구현

- [ ] **2.4. MVI 기초 구조 세팅**
  - [ ] HomeState, HomeIntent, HomeViewModel 클래스 생성

## Task 3. 하이브리드 타임라인 구현 (feature:calendar)
피드 중심의 UI와 확장 가능한 달력을 구현합니다.

- [ ] **3.1. MomentFeedCard UI 개발**
  - [ ] 이미지 1:1/4:5 비율 영역 및 둥근 모서리(32.dp) 적용
  - [ ] 하단 일기 텍스트(Serif, Italic) 영역 구현
  - [ ] 좋아요(Rose400) 및 공유 버튼 인터랙션

- [ ] **3.2. ExpandableCalendar (주간/월간) 제작**
  - [ ] `WeeklyView`: 가로 스크롤 형태의 7일치 날짜 뷰
  - [ ] `MonthlyView`: 그리드 형태의 전체 달력 뷰
  - [ ] `animateContentSize`를 활용한 확장/축소 전환 애니메이션

- [ ] **3.3. Sticky Date Header 연동**
  - [ ] 스크롤 위치에 따라 상단 연/월 정보가 갱신되는 로직 구현

## Task 4. 기록 및 AI 분석 프로세스 (feature:moment, feature:diary)
음성 녹음과 AI 분석 시각화 피드백을 구현합니다.

- [ ] **4.1. Recording Bottom Sheet**
  - [ ] 녹음 시작 시 하단에서 부드럽게 올라오는 반원형 시트 구현

- [ ] **4.2. 실시간 오디오 Waveform 애니메이션**
  - [ ] Canvas를 이용해 입력 진폭값에 따라 변하는 파형 렌더링

- [ ] **4.3. STT 실시간 텍스트 프리뷰**
  - [ ] 음성 인식 중인 텍스트가 흐르듯이 나타나는 애니메이션 뷰

- [ ] **4.4. AI 분석 로딩 상태 UI**
  - [ ] "사진 속 사물을 분석 중..." 등 단계별 메시지 노출 스피너 구현

- [ ] **4.5. AI 결과 리포트 카드**
  - [ ] 분석된 감정/장소 태그 칩 그룹 및 최종 일기 프리뷰

## Task 5. Empty State 및 최종 폴리싱
데이터가 없을 때의 경험과 전체적인 품질을 개선합니다.

- [ ] **5.1. Empty State UI 구현**
  - [ ] 화면 중앙 Stone100 아이콘 및 가이드 문구 배치

- [ ] **5.2. 유도 애니메이션 추가**
  - [ ] 하단 버튼을 가리키는 화살표 바운스 애니메이션 구현

- [ ] **5.3. 전체 UX 디테일 개선**
  - [ ] 이미지 로딩 시 스켈레톤 UI 적용
  - [ ] 화면 전환 및 버튼 클릭 시의 햅틱 피드백 추가
