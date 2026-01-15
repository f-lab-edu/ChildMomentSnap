# [TRD] ChildMomentSnap: 기술 요구사항 문서

## 1. 문서 개요

본 문서는 ChildMomentSnap 프로젝트의 기술적 구현 사항을 상세히 기술합니다. 시스템 아키텍처, 모듈 구조, 데이터 플로우, API 통합 방법 등 개발에 필요한 모든 기술적 명세를 포함합니다.

## 2. 시스템 아키텍처

### 2.1 Clean Architecture

프로젝트는 Clean Architecture 원칙을 따라 계층을 분리합니다:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModel, Compose Screens)       │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          Domain Layer                   │
│    (Use Cases, Business Logic)          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│           Data Layer                    │
│  (Repository, Data Sources, API)        │
└─────────────────────────────────────────┘
```

**계층별 책임:**
- **Presentation**: UI 렌더링, 사용자 인터랙션 처리, ViewModel을 통한 상태 관리
- **Domain**: 비즈니스 로직, Use Case 실행, 데이터 변환
- **Data**: 데이터 소스 관리 (로컬 DB, 네트워크 API), Repository 패턴 구현

### 2.2 MVVM + MVI 패턴

**MVVM (Model-View-ViewModel):**
- **View**: Jetpack Compose로 구현된 UI
- **ViewModel**: UI 상태 관리 및 비즈니스 로직 호출
- **Model**: 데이터 모델 및 Repository

**MVI (Model-View-Intent):**
- **단방향 데이터 플로우**: User Intent → ViewModel → UI State → View
- **불변 상태**: UI State는 불변 객체로 관리
- **예측 가능한 상태 변화**: 모든 상태 변화는 명시적 Intent를 통해 발생

```kotlin
// MVI 패턴 예시
data class DiaryUiState(
    val isLoading: Boolean = false,
    val diary: Diary? = null,
    val error: String? = null
)

sealed interface DiaryIntent {
    data class LoadDiary(val id: String) : DiaryIntent
    data class SaveDiary(val diary: Diary) : DiaryIntent
    object DeleteDiary : DiaryIntent
}
```

### 2.3 모듈 의존성 그래프

```
app
 ├─> feature:feed ──────┐
 ├─> feature:diary ─────┤
 ├─> feature:home ──────┼─> core:ui ──────┐
 ├─> feature:moment ────┤                 │
 │                      │                 ├─> core:model
 │                      └─> core:data ────┤
 │                           ├─> core:database
 │                           ├─> core:network
 │                           └─> core:datastore
 │
 └─> core:common (모든 모듈에서 접근 가능)
```

## 3. 모듈 명세

### 3.1 Core 모듈

#### core:common
**역할**: 프로젝트 전반에서 사용되는 공통 유틸리티 및 확장 함수

**주요 구성요소:**
- Kotlin 확장 함수 (String, Date, Context 등)
- 공통 상수 정의
- 유틸리티 클래스 (DateUtils, ImageUtils 등)

**의존성**: 없음 (최하위 모듈)

#### core:model
**역할**: 프로젝트 전반에서 사용되는 데이터 모델 정의

**주요 모델:**
```kotlin
data class Diary(
    val id: String,
    val date: LocalDate,
    val title: String,
    val content: String,
    val imageUri: String?,
    val voiceUri: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class VisionAnalysisResult(
    val labels: List<String>,
    val objects: List<DetectedObject>,
    val faces: List<FaceAnnotation>
)

data class SpeechToTextResult(
    val transcript: String,
    val confidence: Float
)
```

**의존성**: core:common

#### core:data
**역할**: Repository 패턴을 통한 데이터 접근 추상화

**Repository 인터페이스:**
```kotlin
interface DiaryRepository {
    fun getDiaries(): Flow<List<Diary>>
    fun getDiaryById(id: String): Flow<Diary?>
    suspend fun insertDiary(diary: Diary)
    suspend fun updateDiary(diary: Diary)
    suspend fun deleteDiary(id: String)
}

interface VisionRepository {
    suspend fun analyzeImage(imageUri: Uri): Result<VisionAnalysisResult>
}

interface SpeechRepository {
    suspend fun transcribeAudio(audioUri: Uri): Result<SpeechToTextResult>
}
```

**의존성**: core:model, core:database, core:network, core:datastore

#### core:database
**역할**: Room을 활용한 로컬 데이터베이스 관리

**데이터베이스 스키마:**
```kotlin
@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey val id: String,
    val date: Long, // LocalDate를 Long으로 저장
    val title: String,
    val content: String,
    val imageUri: String?,
    val voiceUri: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diaries ORDER BY date DESC")
    fun getAllDiaries(): Flow<List<DiaryEntity>>
    
    @Query("SELECT * FROM diaries WHERE id = :id")
    fun getDiaryById(id: String): Flow<DiaryEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: DiaryEntity)
    
    @Update
    suspend fun updateDiary(diary: DiaryEntity)
    
    @Query("DELETE FROM diaries WHERE id = :id")
    suspend fun deleteDiary(id: String)
}
```

**의존성**: core:model, core:common

#### core:network
**역할**: 네트워크 통신 및 Google Cloud API 호출 관리

**API 서비스:**
```kotlin
interface VisionApiService {
    suspend fun annotateImage(request: AnnotateImageRequest): AnnotateImageResponse
}

interface SpeechApiService {
    suspend fun recognize(request: RecognizeRequest): RecognizeResponse
}

interface GeminiApiService {
    suspend fun generateDiary(prompt: String): GenerateContentResponse
}
```

**의존성**: core:model, core:common

#### core:datastore
**역할**: 사용자 설정 및 프리퍼런스 관리

**저장 데이터:**
- 사용자 설정 (테마, 언어 등)
- 앱 상태 (온보딩 완료 여부 등)
- 캐시 데이터

**의존성**: core:common

#### core:ui
**역할**: 재사용 가능한 Compose UI 컴포넌트

**주요 컴포넌트:**
- `AppButton`: 공통 버튼 스타일
- `AppTextField`: 공통 텍스트 입력 필드
- `LoadingIndicator`: 로딩 인디케이터
- `ErrorDialog`: 에러 다이얼로그
- `DatePicker`: 날짜 선택기

**의존성**: core:model, core:common

### 3.2 Feature 모듈

#### feature:home
**역할**: 앱 메인 홈 화면 및 Bottom Navigation 관리

**주요 화면:**
- `HomeScreen`: Bottom Navigation을 포함한 메인 컨테이너

**의존성**: core:ui, core:common

#### feature:feed
**역할**: 피드 화면, 캘린더 및 일기 목록 통합 표시

**주요 화면:**
- `FeedScreen`: 캘린더와 피드 리스트가 결합된 메인 화면

**ViewModel:**
```kotlin
class FeedViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    
    // ...
}
```

**의존성**: core:data, core:ui, core:model, core:common

#### feature:diary
**역할**: 일기 작성, 편집, 삭제 기능

**주요 화면:**
- `DiaryWriteScreen`: 일기 작성/편집 화면
- `DiaryDetailScreen`: 일기 상세 보기

**ViewModel:**
```kotlin
class DiaryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val visionRepository: VisionRepository,
    private val speechRepository: SpeechRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {
    
    fun analyzeImageAndGenerateDiary(imageUri: Uri, voiceText: String) {
        viewModelScope.launch {
            // 1. Vision API로 이미지 분석
            val visionResult = visionRepository.analyzeImage(imageUri)
            
            // 2. Gemini API로 일기 생성
            val prompt = buildPrompt(visionResult, voiceText)
            val generatedDiary = geminiRepository.generateDiary(prompt)
            
            // 3. UI 상태 업데이트
            _uiState.update { it.copy(generatedContent = generatedDiary) }
        }
    }
}
```

**의존성**: core:data, core:ui, core:model, core:common

#### feature:moment
**역할**: 사진 촬영/갤러리 선택, 음성 녹음/변환 기능

**주요 화면:**
- `CameraScreen`: 카메라 촬영 화면
- `VoiceRecordingScreen`: 음성 녹음 화면

**CameraX 통합:**
```kotlin
@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    
    // CameraX Preview 및 Capture 로직
}
```

**의존성**: core:data, core:ui, core:model, core:common

## 4. 데이터 플로우

### 4.1 일기 작성 플로우

```
1. 사용자가 사진 촬영/선택
   ↓
2. 사용자가 음성 녹음
   ↓
3. Speech-to-Text API 호출 (음성 → 텍스트)
   ↓
4. Vision API 호출 (이미지 분석)
   ↓
5. Gemini API 호출 (Vision 결과 + STT 텍스트 → 일기 생성)
   ↓
6. 사용자가 AI 생성 일기 검토/수정
   ↓
7. Room DB에 저장
```

### 4.2 캘린더 조회 플로우

```
1. 사용자가 캘린더 화면 진입
   ↓
2. ViewModel이 Repository에서 일기 목록 요청
   ↓
3. Repository가 Room DB에서 데이터 조회
   ↓
4. Flow를 통해 UI에 실시간 업데이트
   ↓
5. 캘린더에 일기가 있는 날짜 표시
```

## 5. API 통합

### 5.1 Google Cloud Vision API

**인증 방식**: API Key 또는 Service Account

**주요 기능:**
- Label Detection: 이미지 내 객체/장면 인식
- Object Localization: 객체 위치 감지
- Face Detection: 얼굴 및 감정 분석

**요청 예시:**
```kotlin
val request = AnnotateImageRequest(
    image = Image(content = base64Image),
    features = listOf(
        Feature(type = "LABEL_DETECTION", maxResults = 10),
        Feature(type = "OBJECT_LOCALIZATION", maxResults = 10),
        Feature(type = "FACE_DETECTION", maxResults = 10)
    )
)
```

### 5.2 Google Cloud Speech-to-Text API

**인증 방식**: API Key 또는 Service Account

**주요 설정:**
- 언어: 한국어 (ko-KR)
- 인코딩: LINEAR16 또는 FLAC
- 샘플링 레이트: 16000 Hz

**요청 예시:**
```kotlin
val request = RecognizeRequest(
    config = RecognitionConfig(
        encoding = "LINEAR16",
        sampleRateHertz = 16000,
        languageCode = "ko-KR"
    ),
    audio = RecognitionAudio(content = base64Audio)
)
```

### 5.3 Google Gemini API

**인증 방식**: API Key

**프롬프트 구조:**
```
당신은 부모가 아이와의 소중한 순간을 기록하는 것을 돕는 AI 작가입니다.

[이미지 분석 결과]
- 감지된 객체: {objects}
- 장면: {scene}
- 감정: {emotion}

[부모의 음성 메모]
{voice_transcript}

위 정보를 바탕으로 따뜻하고 감성적인 일기를 작성해주세요.
```

## 6. 상태 관리

### 6.1 MVI 패턴 구현

**UI State 정의:**
```kotlin
data class DiaryWriteUiState(
    val isLoading: Boolean = false,
    val selectedImageUri: Uri? = null,
    val voiceTranscript: String = "",
    val generatedContent: String = "",
    val error: String? = null
)
```

**Intent 정의:**
```kotlin
sealed interface DiaryWriteIntent {
    data class SelectImage(val uri: Uri) : DiaryWriteIntent
    data class RecordVoice(val audioUri: Uri) : DiaryWriteIntent
    object GenerateDiary : DiaryWriteIntent
    data class UpdateContent(val content: String) : DiaryWriteIntent
    object SaveDiary : DiaryWriteIntent
}
```

**ViewModel에서 Intent 처리:**
```kotlin
fun handleIntent(intent: DiaryWriteIntent) {
    when (intent) {
        is DiaryWriteIntent.SelectImage -> {
            _uiState.update { it.copy(selectedImageUri = intent.uri) }
        }
        is DiaryWriteIntent.GenerateDiary -> {
            generateDiary()
        }
        // ...
    }
}
```

### 6.2 Kotlin Coroutines & Flow

**비동기 처리:**
- `viewModelScope`: ViewModel 생명주기에 맞춘 코루틴 스코프
- `Flow`: 데이터 스트림 처리 (DB 변경사항 실시간 반영)
- `StateFlow`: UI 상태 관리

**예시:**
```kotlin
fun loadDiaries() {
    viewModelScope.launch {
        diaryRepository.getDiaries()
            .catch { e -> 
                _uiState.update { it.copy(error = e.message) }
            }
            .collect { diaries ->
                _uiState.update { it.copy(diaries = diaries, isLoading = false) }
            }
    }
}
```

## 7. 의존성 주입 (Hilt)

### 7.1 모듈 구성

**AppModule:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}
```

**DatabaseModule:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "childmomentsnap.db"
        ).build()
    }
    
    @Provides
    fun provideDiaryDao(database: AppDatabase): DiaryDao {
        return database.diaryDao()
    }
}
```

**NetworkModule:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideVisionApiService(): VisionApiService {
        return Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }
}
```

**RepositoryModule:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        impl: DiaryRepositoryImpl
    ): DiaryRepository
}
```

## 8. 테스팅 전략

### 8.1 단위 테스트 (JUnit)

**ViewModel 테스트:**
```kotlin
@Test
fun `일기 생성 시 로딩 상태가 올바르게 변경되는지 테스트`() = runTest {
    // Given
    val viewModel = DiaryViewModel(mockRepository)
    
    // When
    viewModel.handleIntent(DiaryWriteIntent.GenerateDiary)
    
    // Then
    assertEquals(true, viewModel.uiState.value.isLoading)
}
```

**Repository 테스트:**
```kotlin
@Test
fun `일기 저장 후 조회 시 동일한 데이터가 반환되는지 테스트`() = runTest {
    // Given
    val diary = Diary(...)
    repository.insertDiary(diary)
    
    // When
    val result = repository.getDiaryById(diary.id).first()
    
    // Then
    assertEquals(diary, result)
}
```

### 8.2 UI 테스트 (Espresso + Compose Testing)

**Compose UI 테스트:**
```kotlin
@Test
fun `일기 작성 화면에서 제목 입력 시 UI가 업데이트되는지 테스트`() {
    composeTestRule.setContent {
        DiaryWriteScreen(viewModel = viewModel)
    }
    
    composeTestRule.onNodeWithTag("title_input")
        .performTextInput("테스트 제목")
    
    composeTestRule.onNodeWithTag("title_input")
        .assertTextEquals("테스트 제목")
}
```

### 8.3 테스트 커버리지 목표

- **ViewModel**: 80% 이상
- **Repository**: 90% 이상
- **Use Case**: 85% 이상
- **UI 컴포넌트**: 70% 이상

## 9. 빌드 구성

### 9.1 Gradle 설정

**프로젝트 레벨 build.gradle.kts:**
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

**앱 레벨 build.gradle.kts:**
```kotlin
android {
    namespace = "com.example.childmomentsnap"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.example.childmomentsnap"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}
```

### 9.2 빌드 변형

**Debug:**
- 디버깅 가능
- 로그 출력 활성화
- API 키는 개발용 키 사용

**Release:**
- ProGuard/R8 난독화 적용
- 로그 출력 비활성화
- API 키는 프로덕션 키 사용

### 9.3 ProGuard/R8 규칙

```proguard
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
```

## 10. 보안 고려사항

### 10.1 API 키 관리

- **local.properties에 저장**: Git에 커밋되지 않도록 .gitignore에 추가
- **BuildConfig를 통한 접근**: 코드에 하드코딩하지 않음

```kotlin
// local.properties
GOOGLE_CLOUD_API_KEY=your_api_key_here

// build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "API_KEY", "\"${properties["GOOGLE_CLOUD_API_KEY"]}\"")
    }
}
```

### 10.2 데이터 암호화

- **Room 데이터베이스 암호화**: SQLCipher 사용 고려
- **민감 정보 저장**: DataStore에 저장 시 EncryptedSharedPreferences 사용

### 10.3 네트워크 보안

- **HTTPS 사용**: 모든 API 통신은 HTTPS로만 허용
- **Certificate Pinning**: 중요 API의 경우 인증서 피닝 적용 고려

## 11. 성능 최적화

### 11.1 이미지 처리

- **이미지 압축**: Vision API 전송 전 적절한 크기로 리사이징
- **캐싱**: Coil 라이브러리를 활용한 이미지 캐싱

### 11.2 데이터베이스 최적화

- **인덱싱**: 자주 조회되는 컬럼에 인덱스 추가
- **페이징**: Paging 3 라이브러리를 활용한 대량 데이터 처리

### 11.3 메모리 관리

- **ViewModel 생명주기 관리**: viewModelScope 활용으로 메모리 누수 방지
- **Bitmap 재활용**: 사용 후 즉시 recycle() 호출

---

**문서 버전**: 1.0  
**최종 수정일**: 2026-01-13  
**작성자**: Development Team