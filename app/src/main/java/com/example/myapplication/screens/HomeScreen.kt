package com.example.myapplication.screens

import android.graphics.Typeface
import android.widget.TextView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.example.myapplication.AppUiState
import com.example.myapplication.AppViewModel
import com.example.myapplication.ReaderTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onFavoritesClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateString by viewModel.dateString.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val textSizeSp = viewModel.textSizeSp
    val currentTheme = viewModel.currentTheme

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.bg)
    ) {
        when (val uiState = state.uiState) {
            is AppUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = currentTheme.icon)
            }

            is AppUiState.Success -> {
                val story = uiState.story

                Row(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onFavoritesClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "My Favorites",
                                    tint = currentTheme.icon,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            val isFav = story.isFavorite
                            IconButton(onClick = { viewModel.toggleFavorite(story) }) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Toggle Favorite",
                                    tint = if (isFav) Color.Red else currentTheme.icon,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { viewModel.previousDay() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Prev", tint = currentTheme.icon)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Daily Talmud Story",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = currentTheme.text
                                )
                                Text(
                                    text = dateString,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = currentTheme.icon
                                )
                            }
                            IconButton(onClick = { viewModel.nextDay() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next", tint = currentTheme.icon)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(story.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = currentTheme.text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(story.description, style = MaterialTheme.typography.bodyMedium, fontStyle = FontStyle.Italic, color = currentTheme.icon, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Source: ${story.ref}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2196F3),
                            textDecoration = TextDecoration.Underline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    uriHandler.openUri("https://www.sefaria.org/${story.ref}")
                                }
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.shareStory(context, story) }) {
                                Icon(Icons.Default.Share, "Share", tint = currentTheme.icon)
                            }

                            IconButton(onClick = {
                                viewModel.currentTheme = when (viewModel.currentTheme) {
                                    ReaderTheme.Day -> ReaderTheme.Cream
                                    ReaderTheme.Cream -> ReaderTheme.Night
                                    ReaderTheme.Night -> ReaderTheme.Day
                                }
                            }) {
                                SunIcon(color = currentTheme.icon)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { if (viewModel.textSizeSp > 14f) viewModel.textSizeSp -= 2f }) {
                                    Text("A-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = currentTheme.icon)
                                }
                                TextButton(onClick = { if (viewModel.textSizeSp < 40f) viewModel.textSizeSp += 2f }) {
                                    Text("A+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = currentTheme.icon)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = currentTheme.icon.copy(alpha = 0.3f))

                        val contentPairs = story.hebrewText.zip(story.englishText)
                        if (contentPairs.isEmpty()) {
                            Text("No text available.", color = currentTheme.text)
                        } else {
                            contentPairs.forEach { (hebrew, english) ->
                                Text(
                                    text = viewModel.parseHtml(hebrew),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = (textSizeSp + 4).sp,
                                    textAlign = TextAlign.Right,
                                    lineHeight = (textSizeSp * 1.5).sp,
                                    color = currentTheme.text,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HtmlText(html = english, textSizeSp = textSizeSp, color = currentTheme.text)
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Translation: ${story.englishSource} • Powered by Sefaria",
                            style = MaterialTheme.typography.labelSmall,
                            color = currentTheme.icon,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    val progress = if (scrollState.maxValue > 0) {
                        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                    } else {
                        0f
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(currentTheme.icon.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progress)
                                .background(currentTheme.icon.copy(alpha = 0.5f))
                                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        )
                    }
                }
            }

            is AppUiState.Error -> {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error loading wisdom", color = Color.Red)
                    Button(onClick = { viewModel.loadDailyWisdom() }) { Text("Retry") }
                }
            }
            is AppUiState.Empty -> Text("No Data", modifier = Modifier.align(Alignment.Center), color = currentTheme.text)
        }
    }
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier, textSizeSp: Float, color: Color) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            TextView(context).apply {
                typeface = Typeface.SERIF
            }
        },
        update = { textView ->
            textView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textView.textSize = textSizeSp
            textView.setTextColor(color.toArgb())
        }
    )
}

@Composable
fun SunIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val radius = size.minDimension / 6
        val stroke = 2.dp.toPx()
        val center = this.center

        drawCircle(color = color, radius = radius, style = Stroke(width = stroke))

        for (i in 0 until 8) {
            val angle = i * 45.0 * (Math.PI / 180)
            val startRadius = radius * 1.5f
            val endRadius = radius * 2.2f

            val start = Offset(
                x = (center.x + startRadius * cos(angle)).toFloat(),
                y = (center.y + startRadius * sin(angle)).toFloat()
            )
            val end = Offset(
                x = (center.x + endRadius * cos(angle)).toFloat(),
                y = (center.y + endRadius * sin(angle)).toFloat()
            )
            drawLine(color, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
        }
    }
}