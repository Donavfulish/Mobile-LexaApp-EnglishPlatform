package com.home.lexa.ui.theme

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

@Composable
fun colorRes(@ColorRes id: Int): Color = colorResource(id = id)