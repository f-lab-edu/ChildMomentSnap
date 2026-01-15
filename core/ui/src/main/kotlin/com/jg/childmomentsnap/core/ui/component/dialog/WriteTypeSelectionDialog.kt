package com.jg.childmomentsnap.core.ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.LocalDate

@Composable
fun WriteTypeSelectionDialog(
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
