package com.jg.childmomentsnap.feature.calendar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarDay
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarUiState
import com.jg.childmomentsnap.feature.calendar.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CalendarScreen(
        uiState = uiState,
        onPreviousMonth = viewModel::onPreviousMonthClick,
        onNextMonth = viewModel::onNextMonthClick,
        onDateClick = viewModel::onDateClick
    )
}

@Composable
private fun CalendarScreen(
    uiState: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (LocalDate) -> Unit
) {
    Scaffold(
        topBar = {
            CalendarHeader(
                yearMonth = uiState.yearMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
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
                items(uiState.calendarDays) { day ->
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
    onNextMonth: () -> Unit
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
                contentDescription = "Previous Month"
            )
        }

        Text(
            text = "${yearMonth.year} ${
                yearMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                )
            }",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next Month"
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = listOf(
            DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        )

        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
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
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    day.isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = when {
                !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.bodyLarge
        )
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
            isToday = it == 1
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
            onDateClick = {}
        )
    }
}