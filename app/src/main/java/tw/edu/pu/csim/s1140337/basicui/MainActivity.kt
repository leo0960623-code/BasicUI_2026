package tw.edu.pu.csim.s1140337.basicui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import tw.edu.pu.csim.s1140337.basicui.ui.theme.BasicUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicUITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
                Toast.makeText(
                    this,
                    "作者：賴鈺霖",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        VideoSection()       // 頂部影片與切換按鈕
        LazyRowExample()      // 中間動物滑動卡片（縮小並下移）
        TextFieldExample()    // 底部文字輸入框
    }
}

@Composable
fun VideoSection(modifier: Modifier = Modifier) {
    var currentVideoRes by remember { mutableIntStateOf(R.raw.love) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleVideoPlayer(videoRes = currentVideoRes)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                currentVideoRes = if (currentVideoRes == R.raw.love) {
                    R.raw.handpan
                } else {
                    R.raw.love
                }
            }
        ) {
            Text(
                text = if (currentVideoRes == R.raw.love) "切換至手碟影片" else "切換至Love影片"
            )
        }
    }
}

@Composable
fun TextFieldExample(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("請輸入文字") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (inputText.isBlank()) "尚未輸入內容" else "您輸入的是：$inputText"
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class Animal(
    val name: String,
    @DrawableRes val imageRes: Int
)

@Composable
fun LazyRowExample(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val animals = listOf(
        Animal(name = "鴨子", imageRes = R.drawable.animal0),
        Animal(name = "企鵝", imageRes = R.drawable.animal1),
        Animal(name = "青蛙", imageRes = R.drawable.animal2),
        Animal(name = "貓頭鷹", imageRes = R.drawable.animal3),
        Animal(name = "海豚", imageRes = R.drawable.animal4),
        Animal(name = "牛", imageRes = R.drawable.animal5),
        Animal(name = "無尾熊", imageRes = R.drawable.animal6),
        Animal(name = "獅子", imageRes = R.drawable.animal7),
        Animal(name = "狐狸", imageRes = R.drawable.animal8),
        Animal(name = "小雞", imageRes = R.drawable.animal9)
    )

    // 設定 top padding 將卡片往下方移開，避免擋到上方的切換按鈕
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 260.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(animals) { index, animal ->
            Card(
                modifier = Modifier
                    .fillParentMaxWidth(0.65f) // 卡片寬度縮小
                    .height(200.dp)            // 卡片高度由 280dp 縮至 200dp
                    .padding(horizontal = 8.dp)
                    .clickable {
                        Toast.makeText(
                            context,
                            "您點選的是 ${animal.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD0BCFF)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = animal.imageRes),
                        contentDescription = animal.name,
                        modifier = Modifier.size(90.dp) // 動物圖示縮小至 90dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$index ${animal.name}"
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleVideoPlayer(
    @RawRes videoRes: Int
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // 當 videoRes 改變（按下按鈕切換）時，立即更新播放來源並重新播放
    LaunchedEffect(videoRes) {
        val uri = "android.resource://${context.packageName}/$videoRes"
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}