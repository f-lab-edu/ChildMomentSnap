package com.jg.database.diary

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jg.childmomentsnap.core.model.EmotionKey
import com.jg.childmomentsnap.database.dao.DiaryDao
import com.jg.childmomentsnap.database.entity.DiaryEntity
import com.jg.childmomentsnap.database.room.DiaryDatabase
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import org.robolectric.annotation.Config
import kotlin.test.DefaultAsserter.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class DiaryDaoTest {

    private lateinit var database: DiaryDatabase
    private lateinit var diaryDao: DiaryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DiaryDatabase::class.java)
            .allowMainThreadQueries() // 테스트 환경이므로 메인 스레드 쿼리 허용
            .build()

        diaryDao = database.diaryDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `선택한_날짜_일기가_있으면_리스트를_반환한다`() = runTest {
        val targetDate = "2026-02-24"
        val todayDiary = DiaryEntity(
            id = 1,
            date = "$targetDate 14:30:00",
            content = "오늘 놀이터에 다녀옴",
            imagePath = "",
            bgType = "COLOR",
            bgValue = "놀이터",
            isFavorite = true,
            emotion = EmotionKey.JOY.name
        )
        val yesterdayDiary = DiaryEntity(
            id = 2,
            date = "2026-02-23 10:00:00",
            content = "어제 낮잠을 잠",
            imagePath = "",
            bgType = "COLOR",
            bgValue = "집",
            isFavorite = false,
            emotion = EmotionKey.CALM.name
        )

        diaryDao.insertDiary(todayDiary)
        diaryDao.insertDiary(yesterdayDiary)

        // When (실행)
        val startDate = "$targetDate 00:00:00"
        val endDate = "$targetDate 23:59:59"
        val result = diaryDao.getDiaryListByDate(startDate, endDate)

        // Then (검증)
        // 조회된 리스트의 크기가 1개여야 한다 (오늘 날짜 1개만)
        assertEquals(1, result.size)
    }

    @Test
    fun `선택한_날짜가_일기가 없으면_빈_리스트를_반환한다`() = runTest {
        val existingDiary = DiaryEntity(
            id = 1,
            date = "2026-02-24 14:30:00",
            content = "오늘 일기",
            imagePath = "",
            bgType = "",
            bgValue = "",
            isFavorite = false,
            emotion = EmotionKey.JOY.name
        )
        diaryDao.insertDiary(diary = existingDiary)

        // 데이터가 없는 내일 날짜로 조회 시도
        val targetDate = "2026-02-25"
        val startDate = "$targetDate 00:00:00"
        val endDate = "$targetDate 23:59:59"
        val emptyResult = diaryDao.getDiaryListByDate(startDate, endDate)
        assertTrue("해당 날짜에 데이터가 없으므로 리스트는 비어있어야 합니다.", emptyResult.isEmpty())

    }

    @Test
    fun `선택한_피드에_좋아요_클릭하여_상태_확인한다` () = runTest {
        val existingDiary = DiaryEntity(
            id = 1,
            date = "2026-02-24 14:30:00",
            content = "오늘 일기",
            imagePath = "",
            bgType = "",
            bgValue = "",
            isFavorite = false,
            emotion = EmotionKey.JOY.name
        )
        diaryDao.insertDiary(diary = existingDiary)

        diaryDao.updateFavoriteStatus(1, true)
        val updatedDiary = diaryDao.getFavoriteDiaryList()
        assertEquals(true, updatedDiary.size == 1)
    }
}