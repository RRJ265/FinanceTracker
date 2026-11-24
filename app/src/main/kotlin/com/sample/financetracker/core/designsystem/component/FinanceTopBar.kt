package com.sample.financetracker.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

import com.sample.financetracker.core.designsystem.theme.Fixed

@Composable
fun FinanceTopBar(title: String, userName: String) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding().height(56.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(32.dp).clip(MaterialTheme.shapes.medium).background(scheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = scheme.onPrimary, modifier = Modifier.size(18.dp))
            }
            Column {
                Text("FINANCETRACKER", style = MaterialTheme.typography.labelSmall, color = scheme.outline)
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = scheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Fixed.primaryFixed),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    userName.firstOrNull()?.uppercase().orEmpty(),
                    style = MaterialTheme.typography.labelLarge,
                    color = Fixed.onPrimaryFixed,
                )
            }
        }
    }
}
