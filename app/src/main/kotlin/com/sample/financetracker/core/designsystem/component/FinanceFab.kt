package com.sample.financetracker.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

private val FabShape = RoundedCornerShape(16.dp)

@Composable
fun FinanceFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primaryContainer
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.shadow(
            elevation = 12.dp,
            shape = FabShape,
            ambientColor = primary.copy(alpha = 0.35f),
            spotColor = primary.copy(alpha = 0.35f),
        ),
        shape = FabShape,
        containerColor = primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add transaction")
    }
}
