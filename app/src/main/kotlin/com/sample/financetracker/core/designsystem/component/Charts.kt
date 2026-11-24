package com.sample.financetracker.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Sparkline(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 96.dp, height = 28.dp)) {
        val sx = size.width / 100f
        val sy = size.height / 28f
        val path = Path().apply {
            moveTo(0f, 22f * sy)
            quadraticTo(25f * sx, 25f * sy, 40f * sx, 16f * sy)
            quadraticTo(55f * sx, 7f * sy, 70f * sx, 12f * sy)
            quadraticTo(85f * sx, 17f * sy, 100f * sx, 4f * sy)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

data class DonutSegment(val fraction: Float, val color: Color)

@Composable
fun DonutChart(
    segments: List<DonutSegment>,
    trackColor: Color,
    modifier: Modifier = Modifier,
    diameter: Dp = 144.dp,
    center: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val unit = size.width / 120f
            val strokeWidth = 14f * unit
            val inset = (60f - 46f) * unit
            val arcTopLeft = Offset(inset, inset)
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth),
            )
            val gapDegrees = 4f
            var start = -90f
            segments.forEach { segment ->
                val sweep = segment.fraction * 360f
                drawArc(
                    color = segment.color,
                    startAngle = start,
                    sweepAngle = sweep.coerceAtLeast(0.1f),
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                start += sweep + gapDegrees
            }
        }
        center()
    }
}
