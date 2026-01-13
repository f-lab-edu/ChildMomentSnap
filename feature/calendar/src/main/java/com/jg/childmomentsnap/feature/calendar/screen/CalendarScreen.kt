package com.jg.childmomentsnap.feature.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarSideEffect
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarUiState
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
internal fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToWrite: (LocalDate) -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is CalendarSideEffect.NavigateToDetail -> onNavigateToDetail(effect.diaryId)
                is CalendarSideEffect.NavigateToCamera -> onNavigateToCamera()
                is CalendarSideEffect.NavigateToWrite -> onNavigateToWrite(effect.date)
                is CalendarSideEffect.ShowWriteSelectionDialog -> showDialog = effect.date
            }
        }
    }

    if (showDialog != null) {
        val dialogDate = showDialog!!
        WriteTypeSelectionDialog(
            date = dialogDate,
            onDismiss = { showDialog = null },
            onTypeSelected = { isPhoto ->
                showDialog = null
                viewModel.onWriteTypeSelected(isPhoto, dialogDate)
            }
        )
    }

    CalendarScreen(
        uiState = uiState,
        onDateClick = viewModel::onDateClick,
        onMonthChange = viewModel::loadDiariesForMonth,
        onDismissBottomSheet = viewModel::dismissBottomSheet,
        onBottomSheetItemClick = { diaryId ->
            viewModel.dismissBottomSheet()
            onNavigateToDetail(diaryId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScreen(
    uiState: CalendarUiState,
    onDateClick: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onDismissBottomSheet: () -> Unit,
    onBottomSheetItemClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            CalendarTopBar(
                currentMonth = uiState.currentMonth,
                onMonthChange = onMonthChange
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CalendarGrid(
                currentMonth = uiState.currentMonth,
                diaries = uiState.diaries,
                onDateClick = onDateClick
            )
        }

        if (uiState.isBottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = onDismissBottomSheet,
                sheetState = rememberModalBottomSheetState()
            ) {
                DailyPostBottomSheetContent(
                    diaries = uiState.bottomSheetDiaries,
                    onItemClick = onBottomSheetItemClick
                )
            }
        }
    }
}

@Composable
private fun CalendarTopBar(
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 MM월")),
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    diaries: Map<LocalDate, List<com.jg.childmomentsnap.core.model.Diary>>,
    onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
    val totalCells = daysInMonth + firstDayOfMonth

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize()
    ) {
        // Day headers
        val days = listOf(
            com.jg.childmomentsnap.feature.calendar.R.string.day_sunday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_monday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_tuesday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_wednesday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_thursday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_friday,
            com.jg.childmomentsnap.feature.calendar.R.string.day_saturday
        )
        items(days) { dayResId ->
            Text(
                text = stringResource(id = dayResId),
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Empty cells for offset
        items(firstDayOfMonth) {
            Box(modifier = Modifier.aspectRatio(1f))
        }

        // Days
        items(daysInMonth) { day ->
            val date = currentMonth.atDay(day + 1)
            val dayDiaries = diaries[date] ?: emptyList()

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
                    .clickable { onDateClick(date) }
                    .background(
                        color = if (dayDiaries.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${day + 1}")
                    if (dayDiaries.isNotEmpty()) {
                        Text(
                            text = "${dayDiaries.size}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WriteTypeSelectionDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onTypeSelected: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "작성 유형 선택") },
        text = { Text(text = "${date}에 어떤 일기를 작성하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = { onTypeSelected(false) }) {
                Text("글쓰기")
            }
        },
        dismissButton = {
            TextButton(onClick = { onTypeSelected(true) }) {
                Text("사진찍기")
            }
        }
    )
}

@Composable
private fun DailyPostBottomSheetContent(
    diaries: List<com.jg.childmomentsnap.core.model.Diary>,
    onItemClick: (Long) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "작성된 일기 목록",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn {
            items(diaries) { diary ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(diary.id) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = diary.time)
                    Text(text = diary.content.take(20) + "...")
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
