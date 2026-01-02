package com.jg.childmomentsnap.feature.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jg.childmomentsnap.feature.calendar.R
import com.jg.childmomentsnap.feature.calendar.screen.component.YearMonthPickerDialog
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarDay
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarUiState
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CalendarScreen(
        uiState = uiState,
        onPreviousMonth = viewModel::onPreviousMonthClick,
        onNextMonth = viewModel::onNextMonthClick,
        onDateClick = viewModel::onDateClick,
        onYearMonthSelected = viewModel::updateYearMonth
    )
}

@Composable
private fun CalendarScreen(
    uiState: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit,
    onYearMonthSelected: (YearMonth) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        YearMonthPickerDialog(
            initialYearMonth = uiState.yearMonth,
            onDismissRequest = { showDatePicker = false },
            onYearMonthSelected = { yearMonth ->
                onYearMonthSelected(yearMonth)
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            CalendarHeader(
                yearMonth = uiState.yearMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onYearMonthSearchClick = { showDatePicker = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DaysOfWeekHeader()

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = uiState.calendarDays,
                    key = { day -> day.date }
                ) { day ->
                    CalendarDayItem(
                        day = day,
                        isSelected = day.date == uiState.selectedDate,
                        onClick = { onDateClick(day.date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onYearMonthSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.desc_prev_month)
            )
        }
        
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onYearMonthSearchClick)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.date_year_month_format, yearMonth.year, yearMonth.monthValue),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.size(4.dp))
            
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.desc_select_year_month)
            )
        }
        
        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.desc_next_month)
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf(
            R.string.day_sunday, R.string.day_monday, R.string.day_tuesday,
            R.string.day_wednesday, R.string.day_thursday, R.string.day_friday, R.string.day_saturday
        )

        daysOfWeek.forEach { dayResId ->
            Text(
                text = stringResource(dayResId),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    day.isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }
            )
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Date Text
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when {
                    !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Content Text (if exists)
            if (day.content != null) {
                Text(
                    text = day.content,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                           else MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 10.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    val today = LocalDate.now()
    val days = (1..42).map { 
        CalendarDay(
            date = today.plusDays(it.toLong()),
            isCurrentMonth = true,
            isToday = it == 1,
            content = if (it % 3 == 0) "일기 내용이 길어지면 어떻게 될까요? 말줄임표가 나와야 합니다." else null
        )
    }

    MaterialTheme {
        CalendarScreen(
            uiState = CalendarUiState(
                yearMonth = YearMonth.now(),
                calendarDays = days,
                selectedDate = today
            ),
            onPreviousMonth = {},
            onNextMonth = {},
            onDateClick = {},
            onYearMonthSelected = {}
        )
    }
}