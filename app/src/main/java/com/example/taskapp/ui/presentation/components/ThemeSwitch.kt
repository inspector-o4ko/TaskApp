package com.example.taskapp.ui.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import com.example.taskapp.R

@Composable
fun ThemeSwitch(
    isDark: Boolean,
    onToggle: (dark: Boolean, origin: Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    IconToggleSwitch(
        checked = isDark,
        onCheckedChange = onToggle,
        startIcon = painterResource(R.drawable.ic_light_mode),
        endIcon = painterResource(R.drawable.ic_dark_mode),
        modifier = modifier
    )
}