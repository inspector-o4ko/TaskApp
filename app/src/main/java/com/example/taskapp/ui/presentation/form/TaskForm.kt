package com.example.taskapp.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.taskapp.domain.model.TaskPriority


@Composable
fun TaskForm(
    state: TaskFormState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 24.dp,
                bottom = 96.dp
            ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.titleMedium,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            placeholder = { Text("Add details (optional)") },
            shape = MaterialTheme.shapes.medium,
            minLines = 6,
            maxLines = 12
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Select priority", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskPriority.entries.forEach { priority ->
                    FilterChip(
                        selected = state.priority == priority,
                        onClick = { onPriorityChange(priority) },
                        label = { Text(stringResource(priority.labelRes)) }
                    )
                }
            }
        }

    }
}