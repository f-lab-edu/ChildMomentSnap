package com.jg.childmomentsnap.feature.feed.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import com.jg.childmomentsnap.core.model.Diary
import com.jg.childmomentsnap.core.model.EmotionKey
import com.jg.childmomentsnap.core.ui.component.MomentsTagChip
import com.jg.childmomentsnap.core.ui.theme.Amber50
import com.jg.childmomentsnap.core.ui.theme.Amber100
import com.jg.childmomentsnap.core.ui.theme.Amber800
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.jg.childmomentsnap.core.ui.theme.Amber500
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
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is FeedSideEffect.NavigateToDetail -> onNavigateToDetail(effect.diaryId)
                    is FeedSideEffect.NavigateToCamera -> onNavigateToCamera()
                    is FeedSideEffect.NavigateToWrite -> onNavigateToWrite(effect.date)
                }
            }
        }
    }

    FeedScreen(
        uiState = uiState,
        onDateClick = viewModel::onDateClick,
        onToggleCalendar = viewModel::toggleCalendarExpansion,
        onToggleFavorite = viewModel::toggleFavorite,
        onFeedDeletedClick = viewModel::onDeleteDiary,
        onToggleSearchMode = viewModel::toggleSearchMode,
        onSearchQueryChange = viewModel::searchDiaryContent,
        onNavigateWeek = viewModel::navigateWeek,
        onNavigateMonth = viewModel::navigateMonth,
        onHeaderClick = { viewModel.showYearMonthPicker() },
        onYearMonthSelected = viewModel::onYearMonthSelected,
        onDismissYearMonthPicker = { viewModel.hideYearMonthPicker() }
    )
}

@Composable
private fun FeedScreen(
    uiState: FeedUiState,
    onDateClick: (LocalDate) -> Unit,
    onToggleCalendar: () -> Unit,
    onToggleFavorite: (id: Long, isFavorite: Boolean) -> Unit,
    onFeedDeletedClick: (id: Long) -> Unit,
    onToggleSearchMode: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigateWeek: (Int) -> Unit,
    onNavigateMonth: (Int) -> Unit,
    onHeaderClick: () -> Unit,
    onYearMonthSelected: (YearMonth) -> Unit,
    onDismissYearMonthPicker: () -> Unit
) {
    MomentsTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                MomentsTopAppBar(
                    isSearchMode = uiState.isSearchMode,
                    onSearchModeChange = onToggleSearchMode,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = onSearchQueryChange
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // 1. 캘린더 영역 (검색 모드일 때는 위로 스르륵 사라짐)
                AnimatedVisibility(
                    visible = !uiState.isSearchMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ExpandableCalendar(
                        isExpanded = uiState.isCalendarExpanded,
                        onToggle = onToggleCalendar,
                        currentMonth = uiState.currentMonth,
                        selectedDate = uiState.selectedDate,
                        onDateClick = onDateClick,
                        weeklyDays = uiState.weeklyDays,
                        monthlyDays = uiState.monthlyDays,
                        onNavigateWeek = onNavigateWeek,
                        onNavigateMonth = onNavigateMonth,
                        onHeaderClick = onHeaderClick
                    )
                }

                // 2. 검색 결과 안내 텍스트 (검색어가 있을 때만 등장)
                if (uiState.isSearchMode && uiState.searchQuery.isNotEmpty()) {
                    Text(
                        text = "'${uiState.searchQuery}' 검색 결과 ${uiState.searchResults.size}건",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Amber800,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                // 3. 피드 영역: 검색 모드일 때는 searchResults, 아니면 feedList
                val displayList = if (uiState.isSearchMode && uiState.searchQuery.isNotEmpty()) {
                    uiState.searchResults
                } else {
                    uiState.feedList
                }

                val globalBottomBarHeight = 100.dp
                MomentFeed(
                    moments = displayList,
                    isSearchMode = uiState.isSearchMode,
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        bottom = globalBottomBarHeight + 16.dp
                    ),
                    onLikeClick = onToggleFavorite,
                    onFeedDeletedClick = onFeedDeletedClick
                )
            }

            // 4. 년/월 선택 다이얼로그
            if (uiState.isYearMonthPickerVisible) {
                YearMonthPickerDialog(
                    currentMonth = uiState.currentMonth,
                    onYearMonthSelected = onYearMonthSelected,
                    onDismiss = onDismissYearMonthPicker
                )
            }
        }
    }
}

// --- 1. TopAppBar 영역 ---
@Composable
fun MomentsTopAppBar(
    isSearchMode: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    AnimatedContent(
        targetState = isSearchMode,
        transitionSpec = {
            fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
        },
        label = "TopAppBarSearchAnimation"
    ) { isSearch ->
        if (isSearch) {
            // [검색 모드 UI]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onSearchModeChange(false)
                    onSearchQueryChange("") // 닫을 때 검색어 초기화
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "닫기", tint = Stone800)
                }
                Spacer(modifier = Modifier.width(4.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .background(Stone100, CircleShape)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Stone900),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text(stringResource(FeedR.string.feed_screen_search_hint), color = Stone400, style = MaterialTheme.typography.bodyMedium)
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "지우기", tint = Stone400)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp)) // To keep layout consistent when empty
                }
            }
        } else {
            // [기본 모드 UI]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(FeedR.string.app_name_moments),
                    style = MaterialTheme.typography.displayLarge,
                    color = Stone900
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onSearchModeChange(true) }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = stringResource(FeedR.string.desc_search),
                            tint = Stone300
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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
    monthlyDays: List<LocalDate?> = emptyList(),
    onNavigateWeek: (Int) -> Unit = {},
    onNavigateMonth: (Int) -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.8f))
            .animateContentSize()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ◀ 이전 (주간: 이전 주, 월간: 이전 달)
            IconButton(
                onClick = { if (isExpanded) onNavigateMonth(-1) else onNavigateWeek(-1) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(FeedR.string.desc_prev_month),
                    tint = Stone400
                )
            }

            // 년/월 헤더 (클릭하면 다이얼로그 열기)
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern(stringResource(FeedR.string.calendar_header_format))),
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                color = Stone800,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onHeaderClick() }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            // ▶ 다음 (주간: 다음 주, 월간: 다음 달)
            IconButton(
                onClick = { if (isExpanded) onNavigateMonth(1) else onNavigateWeek(1) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(FeedR.string.desc_next_month),
                    tint = Stone400
                )
            }

            // 확장/축소 토글
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(FeedR.string.desc_toggle_calendar),
                    tint = Stone300
                )
            }
        }

        if (!isExpanded) {
            WeeklyCalendarView(
                days = weeklyDays,
                selectedDate = selectedDate,
                onDateClick = onDateClick
            )
        } else {
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
                    color = if (isSelected) Color.White else if (isToday) Stone900 else Stone400,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
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
                                            fontSize = 14.sp, // 글씨를 깔끔하게 보이도록 살짝 키웠습니다
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else if (isToday) Stone900 else Stone400,
                                        maxLines = 1,
                                        softWrap = false,
                                        textAlign = TextAlign.Center
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
    isSearchMode: Boolean, // 추가됨
    contentPadding: PaddingValues = PaddingValues(top = 16.dp, bottom = 16.dp),
    onFeedDeletedClick: (Long) -> Unit = {},
    onFeedSharedClick: (Long) -> Unit = {},
    onLikeClick: (Long, Boolean) -> Unit
) {
    if (moments.isEmpty()) {
        // 검색 모드 여부에 따라 EmptyView 문구 분기 처리
        EmptyFeedView(
            isSearchMode = isSearchMode,
            modifier = Modifier.padding(contentPadding)
        )
    } else {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = contentPadding
        ) {
            items(moments) { moment ->
                MomentFeedItem(
                    moment = moment,
                    onFeedDeletedClick = onFeedDeletedClick,
                    onFeedSharedClick = { },
                    onLikeClick = onLikeClick
                )
            }
        }
    }
}

@Composable
fun EmptyFeedView(isSearchMode: Boolean = false, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Stone100.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSearchMode) Icons.Default.Search else Icons.Filled.Face, // 아이콘 분기
                contentDescription = null,
                tint = Stone300,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 검색 모드에 따라 Title 텍스트 분기
        Text(
            text = if (isSearchMode) stringResource(FeedR.string.feed_screen_not_search_content) else stringResource(FeedR.string.empty_feed_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            ),
            color = Stone900
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 검색 모드에 따라 Subtitle 텍스트 분기
        Text(
            text = if (isSearchMode) stringResource(FeedR.string.feed_screen_search_other_keyword) else stringResource(FeedR.string.empty_feed_subtitle),
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
    onFeedSharedClick: (id: Long) -> Unit = {},
    onFeedDeletedClick: (id: Long) -> Unit = {},
    onLikeClick: (Long, Boolean) -> Unit,
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
                diaryId = moment.id,
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
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
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

            // 4. 액션 바 영역 (좋아요)
            MomentItemActionBar(
                id = moment.id,
                isFavorite = moment.isFavorite,
                onLikeClick = onLikeClick,
            )
        }
    }
}

@Composable
private fun MomentHeader(
    diaryId: Long,
    role: String,
    babyInfo: String,
    timestamp: String,
    onFeedSharedClick: (id: Long) -> Unit = {},
    onFeedDeletedClick: (id: Long) -> Unit = {}
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
                        onFeedSharedClick(diaryId)
                    },
                    leadingIcon = { Icon(Icons.Default.Share, "공유") }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(FeedR.string.feed_item_delete), color = Color.Red) },
                    onClick = {
                        showMenu = false
                        onFeedDeletedClick(diaryId)
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, "삭제", tint = Color.Red) }
                )
            }
        }
    }
}

@Composable
private fun MomentItemActionBar(
    id: Long,
    isFavorite: Boolean,
    onLikeClick: (Long, Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onLikeClick(id, isFavorite) }) {
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

// --- Year/Month Picker Dialog ---
@Composable
fun YearMonthPickerDialog(
    currentMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit,
    onDismiss: () -> Unit
) {
    // 2단계: Step 0 = 년도 선택, Step 1 = 월 선택
    var step by remember { mutableIntStateOf(0) }
    var selectedYear by remember { mutableIntStateOf(currentMonth.year) }

    val currentYear = YearMonth.now().year
    val yearRange = (currentYear - 5)..(currentYear + 1) // 5년 전 ~ 1년 후

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(FeedR.string.dialog_cancel), color = Stone400)
            }
        },
        title = {
            Text(
                text = if (step == 0)
                    stringResource(FeedR.string.dialog_select_year)
                else
                    stringResource(FeedR.string.dialog_select_month_format, selectedYear),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = Stone900
            )
        },
        text = {
            Column {
                if (step == 0) {
                    // Step 0: 년도 선택 그리드 (3열)
                    val years = yearRange.toList()
                    repeat((years.size + 2) / 3) { rowIdx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) { colIdx ->
                                val idx = rowIdx * 3 + colIdx
                                if (idx < years.size) {
                                    val year = years[idx]
                                    val isCurrentYear = year == currentMonth.year
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                selectedYear = year
                                                step = 1
                                            },
                                        color = if (isCurrentYear) Amber100 else Stone100,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(FeedR.string.date_year_format, year),
                                            modifier = Modifier.padding(vertical = 12.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrentYear) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isCurrentYear) Amber800 else Stone800
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    // Step 1: 월 선택 그리드 (3열)
                    // 뒤로가기 버튼
                    TextButton(onClick = { step = 0 }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(FeedR.string.dialog_back),
                            modifier = Modifier.size(16.dp),
                            tint = Stone400
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(FeedR.string.dialog_back), color = Stone400)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    repeat(4) { rowIdx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) { colIdx ->
                                val month = rowIdx * 3 + colIdx + 1
                                val isCurrentMonth = selectedYear == currentMonth.year && month == currentMonth.monthValue
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            onYearMonthSelected(YearMonth.of(selectedYear, month))
                                        },
                                    color = if (isCurrentMonth) Amber100 else Stone100,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(FeedR.string.date_month_format, month),
                                        modifier = Modifier.padding(vertical = 12.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isCurrentMonth) Amber800 else Stone800
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
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
            onToggleCalendar = {},
            onToggleFavorite = { _, _ -> },
            onFeedDeletedClick = {},
            onToggleSearchMode = {},
            onSearchQueryChange = {},
            onNavigateWeek = {},
            onNavigateMonth = {},
            onHeaderClick = {},
            onYearMonthSelected = {},
            onDismissYearMonthPicker = {}
        )
    }
}

