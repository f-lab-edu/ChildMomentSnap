package com.jg.childmomentsnap.feature.diary.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
internal fun DiaryRoute(

) {
    DiaryScreen()
}

@Composable
private fun DiaryScreen(

) {
    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Diary Screen",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}