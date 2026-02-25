package com.jg.childmomentsnap.feature.feed.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jg.childmomentsnap.core.model.ChildEmotion
import com.jg.childmomentsnap.core.model.Diary
import com.jg.childmomentsnap.core.model.EmotionKey
import com.jg.childmomentsnap.core.ui.component.MomentsTagChip
import com.jg.childmomentsnap.core.ui.theme.Amber50
import com.jg.childmomentsnap.core.ui.theme.Amber100
import com.jg.childmomentsnap.core.ui.theme.Amber500
import com.jg.childmomentsnap.core.ui.theme.Amber800
import com.jg.childmomentsnap.core.ui.theme.Indigo50
import com.jg.childmomentsnap.core.ui.theme.Indigo400
import com.jg.childmomentsnap.core.ui.theme.Indigo800
import com.jg.childmomentsnap.core.ui.theme.Purple50
import com.jg.childmomentsnap.core.ui.theme.Rose500
import com.jg.childmomentsnap.core.ui.theme.MomentsShapes
import com.jg.childmomentsnap.core.ui.theme.MomentsTheme
import com.jg.childmomentsnap.core.ui.theme.Stone100
import com.jg.childmomentsnap.core.ui.theme.Stone200
import com.jg.childmomentsnap.core.ui.theme.Stone300
import com.jg.childmomentsnap.core.ui.theme.Stone400
import com.jg.childmomentsnap.core.ui.theme.Stone800
import com.jg.childmomentsnap.core.ui.theme.Stone900
import com.jg.childmomentsnap.core.ui.util.EmotionChipResources
import com.jg.childmomentsnap.core.ui.util.modifier.scrollbar
import com.jg.childmomentsnap.feature.feed.viewmodel.FeedSideEffect
import com.jg.childmomentsnap.feature.feed.viewmodel.FeedUiState
import com.jg.childmomentsnap.feature.feed.viewmodel.FeedViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jg.childmomentsnap.core.ui.theme.Rose400
import com.jg.childmomentsnap.feature.feed.R as FeedR


@Composable
internal fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWrite: (LocalDate) -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is FeedSideEffect.NavigateToDetail -> onNavigateToDetail(effect.diaryId)
                is FeedSideEffect.NavigateToCamera -> onNavigateToCamera()
                is FeedSideEffect.NavigateToWrite -> onNavigateToWrite(effect.date)
                is FeedSideEffect.ShowWriteSelectionDialog -> {

                }
            }
        }
    }



    FeedScreen(
        uiState = uiState,
        onDateClick = viewModel::onDateClick,
        onToggleCalendar = viewModel::toggleCalendarExpansion
    )
}

@Composable
private fun FeedScreen(
    uiState: FeedUiState,
    onDateClick: (LocalDate) -> Unit,
    onToggleCalendar: () -> Unit
) {

    MomentsTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { MomentsTopAppBar() },
            bottomBar = {
                // Global CmsBottomBar handles this now.
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // 2. 캘린더 영역
                ExpandableCalendar(
                    isExpanded = uiState.isCalendarExpanded,
                    onToggle = onToggleCalendar,
                    currentMonth = uiState.currentMonth,
                    selectedDate = uiState.selectedDate,
                    onDateClick = onDateClick,
                    weeklyDays = uiState.weeklyDays,
                    monthlyDays = uiState.monthlyDays
                )

                // 3. 피드 영역
                // Add extra bottom padding for floating CmsBottomBar (~100.dp)
                val globalBottomBarHeight = 100.dp
                MomentFeed(
                    moments = uiState.feedList,
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = globalBottomBarHeight + 16.dp
                    )
                )
            }
        }
    }
}

// --- 1. TopAppBar 영역 ---
@Composable
fun MomentsTopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 로고 (MomentsDesignSystem의 displayLarge 스타일 적용)
        Text(
            text = stringResource(FeedR.string.app_name_moments),
            style = MaterialTheme.typography.displayLarge,
            color = Stone900
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* 검색 모달 열기 */ }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = stringResource(FeedR.string.desc_search),
                    tint = Stone300
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // 프로필 버튼
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Stone200)
                    .clickable { /* 프로필/설정 이동 */ }
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = stringResource(FeedR.string.desc_profile),
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}

// --- 2. Calendar 영역 ---
@Composable
fun ExpandableCalendar(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    currentMonth: YearMonth = YearMonth.now(),
    selectedDate: LocalDate? = null,
    onDateClick: (LocalDate) -> Unit = {},
    weeklyDays: List<LocalDate> = emptyList(),
    monthlyDays: List<LocalDate?> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f))
            .animateContentSize() // 확장/축소 시 부드러운 애니메이션
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern(stringResource(FeedR.string.calendar_header_format))),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
                color = Stone800
            )
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(FeedR.string.desc_toggle_calendar),
                    tint = Stone300
                )
            }
        }

        if (!isExpanded) {
            // 주간 뷰 (Horizontal)
            WeeklyCalendarView(
                days = weeklyDays,
                selectedDate = selectedDate,
                onDateClick = onDateClick
            )
        } else {
            // 월간 뷰 그리드
            MonthlyCalendarGridView(
                days = monthlyDays,
                selectedDate = selectedDate,
                onDateClick = onDateClick
            )
        }
    }
}

@Composable
fun WeeklyCalendarView(
    days: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {

    val weekDays = listOf(
        stringResource(FeedR.string.day_sunday),
        stringResource(FeedR.string.day_monday),
        stringResource(FeedR.string.day_tuesday),
        stringResource(FeedR.string.day_wednesday),
        stringResource(FeedR.string.day_thursday),
        stringResource(FeedR.string.day_friday),
        stringResource(FeedR.string.day_saturday)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(days.size) { index ->
            val day = days[index]
            val isSelected = (day == selectedDate)
            val isToday = (day == LocalDate.now())

            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isSelected) Stone900
                        else if (isToday) Amber100
                        else Color.Transparent
                    )
                    .padding(vertical = 12.dp, horizontal = 12.dp)
                    .clickable { onDateClick(day) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weekDays[day.dayOfWeek.value % 7], // correct day mapping
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 10.sp,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isSelected) Color.White.copy(alpha = 0.6f) else if (isToday) Stone900 else Stone300
                )
                Text(
                    text = day.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isSelected) Color.White else if (isToday) Stone900 else Stone400
                )
            }
        }
    }
}

@Composable
fun MonthlyCalendarGridView(
    days: List<LocalDate?>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val weekDays = listOf(
        stringResource(FeedR.string.day_sunday),
        stringResource(FeedR.string.day_monday),
        stringResource(FeedR.string.day_tuesday),
        stringResource(FeedR.string.day_wednesday),
        stringResource(FeedR.string.day_thursday),
        stringResource(FeedR.string.day_friday),
        stringResource(FeedR.string.day_saturday)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = Stone300
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 그리드 (7열 구조)
        val rows = (days.size + 6) / 7

        repeat(rows) { rowIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { colIndex ->
                    val cellIndex = rowIndex * 7 + colIndex

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cellIndex < days.size) {
                            val date = days[cellIndex]
                            if (date != null) {
                                val day = date.dayOfMonth
                                val isSelected = (date == selectedDate)
                                val isToday = (date == LocalDate.now())

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Stone900
                                            else if (isToday) Amber100
                                            else Color.Transparent
                                        )
                                        .clickable { onDateClick(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else if (isToday) Stone900 else Stone400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. 피드 영역 ---
@Composable
private fun MomentFeed(
    moments: List<Diary>,
    contentPadding: PaddingValues = PaddingValues(top = 16.dp, bottom = 16.dp)
) {
    if (moments.isEmpty()) {
        EmptyFeedView(modifier = Modifier.padding(contentPadding))
    } else {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .scrollbar(state = listState, color = Stone300),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = contentPadding
        ) {
            items(moments) { moment ->
                MomentFeedItem(
                    moment = moment,
                    onFeedDeletedClick = { },
                    onFeedSharedClick = { },
                    onLikeClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun EmptyFeedView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp) // Large size
                .clip(CircleShape)
                .background(Stone100.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = null,
                tint = Stone300,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(FeedR.string.empty_feed_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            ),
            color = Stone900
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(FeedR.string.empty_feed_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Stone400,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun MomentFeedItem(
    moment: Diary,
    onFeedSharedClick: () -> Unit = {},
    onFeedDeletedClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MomentsShapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // 1. 헤더 영역 (날짜, 장소, 더보기)
            //  TODO UserInfo 데이터 필요 (로그인 사용자 역할, 아이 이름, 생년월일로 개월수 표기)
            MomentHeader(
                role = "",
                babyInfo = "",
                timestamp = moment.date,
                onFeedSharedClick = onFeedSharedClick,
                onFeedDeletedClick = onFeedDeletedClick
            )

            // 2. 이미지 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f) // 감성적인 세로 비율
                    .padding(horizontal = 12.dp)
                    .clip(MomentsShapes.large) // 32.dp 곡률 적용
                    .background(Stone100)
            ) {
                //  - 플레이스홀더 아이콘
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(48.dp),
                    tint = Stone200
                )
            }

            // 3. 콘텐츠 영역 (태그, 일기 본문)
            val emotionResIds = EmotionChipResources.getEmotionChipResIds(moment.emotion)
            MomentItemContent(
                tag = moment.bgValue ?: "",
                emotionResIds = emotionResIds,
                content = moment.content
            )

            // 4. 액션 바 영역 (좋아요, 마일스톤 별)
            MomentItemActionBar(
                isFavorite = moment.isFavorite,
                isMilestone = false,
                onLikeClick = onLikeClick,
            )
        }
    }
}



@Composable
private fun MomentHeader(
    role: String,
    babyInfo: String,
    timestamp: String,
    onFeedSharedClick: () -> Unit = {},
    onFeedDeletedClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지 및 역할 뱃지
        Box(modifier = Modifier.size(48.dp)) {
            // 프로필 이미지 (실제 앱에서는 AsyncImage 사용)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Stone200)
                    .border(2.dp, Amber50, CircleShape)
                    .align(Alignment.TopStart)
            )

            // 역할 뱃지
            if (role.isNotEmpty()) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = Amber100,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(
                        text = role,
                        color = Amber800,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 아기 정보 및 시간
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = babyInfo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = timestamp,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 더보기 메뉴
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "더보기",
                    tint = Color.Gray
                )
            }

            // 드롭다운 메뉴
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(FeedR.string.shared_with_family)) },
                    onClick = {
                        showMenu = false
                        onFeedSharedClick()
                    },
                    leadingIcon = { Icon(Icons.Default.Share, "공유") }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(FeedR.string.feed_item_delete), color = Color.Red) },
                    onClick = {
                        showMenu = false
                        onFeedDeletedClick()
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, "삭제", tint = Color.Red) }
                )
            }
        }
    }
}

@Composable
private fun MomentItemActionBar(
    isFavorite: Boolean,
    isMilestone: Boolean,
    onLikeClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(FeedR.string.desc_like),
                tint = Rose400,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun MomentItemContent(
    tag: String,
    emotionResIds: List<Int>,
    content: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 6.dp)
    ) {
        Text(
            text = "\"$content\"",
            style = MaterialTheme.typography.bodyLarge,
            color = Stone800,
            lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 태그 영역 (감정 칩 + 일반 태그)
        MomentTagArea(tag = tag, emotionResIds = emotionResIds)
    }
}

@Composable
fun MomentTagArea(
    tag: String,
    emotionResIds: List<Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AI 감정 분석 칩들
        emotionResIds.forEach { resId ->
            MomentsTagChip(text = stringResource(resId))
        }

        // 일반 장소/상황 태그
        if (tag.isNotBlank()) {
            MomentsTagChip(text = tag)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenLoadedPreview() {
    val sampleDiaries = listOf(
        Diary(
            id = 1,
            date = "2026-02-24 14:30:00",
            content = "오늘은 놀이터에서 신나게 미끄럼틀을 탔다. 아이가 너무 즐거워했다.",
            imagePath = "",
            bgValue = "놀이터",
            isFavorite = true,
            emotion = listOf(EmotionKey.JOY, EmotionKey.SURPRISE)
        ),
        Diary(
            id = 2,
            date = "2026-02-23 10:00:00",
            content = "아침에 일어나서 밥을 맛있게 먹고 그림책을 읽었다.",
            imagePath = "",
            bgValue = "집",
            isFavorite = false,
            emotion = listOf(EmotionKey.CALM)
        )
    )

    MomentsTheme {
        FeedScreen(
            uiState = FeedUiState(
                currentMonth = YearMonth.now(),
                selectedDate = LocalDate.now(),
                feedList = sampleDiaries,
                weeklyDays = (0..6).map { LocalDate.now().minusDays(3).plusDays(it.toLong()) },
                isCalendarExpanded = false
            ),
            onDateClick = {},
            onToggleCalendar = {}
        )
    }
}

