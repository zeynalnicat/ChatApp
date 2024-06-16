package com.example.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun RoundedCornerCard(
    cornerRadius: Dp = 16.dp,
    innerPadding : Dp = 10.dp,
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp,
    content : @Composable () -> Unit
){

    Card(
        shape = RoundedCornerShape(cornerRadius),
        elevation = elevation,
        backgroundColor = Color.White,
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(innerPadding)){
            content()
        }
    }
}
