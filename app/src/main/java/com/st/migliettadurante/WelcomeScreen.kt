package com.example.humanactivityrecognition.screen


import android.app.ActionBar
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.har.migliettadurante.R
import com.st.migliettadurante.authentication.SecureStorageManager


@Composable
fun WelcomeScreen(navController: NavController) {

    val secureStorageManager = SecureStorageManager(LocalContext.current)
    LaunchedEffect(Unit) {
        secureStorageManager.clearJwt()
        secureStorageManager.clearUser()
        secureStorageManager.clearDeviceId()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VideoPlayer(videoUri = Uri.parse("asset:///prova.mp4"))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                BasicText(
                    text = "Benvenuti nell'app di Human Activity Recognition",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color(0xFF3A7BD5),
                            offset = Offset(10f, 10f),
                            blurRadius = 20f
                        )
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp).width(270.dp)
                )

                Button(
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F4)),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        navController.navigate("info")
                    },
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_information),
                        contentDescription = "Icona Informazioni",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
//                Button(
//                    onClick = {
//                        navController.navigate("archive")
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F3F4)),
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .weight(1f)
//                        .height(50.dp)
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.archive),
//                        contentDescription = "Icona Archivio",
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(9.dp))
//                    Text(
//                        text = "Archivio",
//                        color = Color.Black,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }

//                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A7BD5)),
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .weight(1f)
                        .height(45.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "Icona Login",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Accedi",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun VideoPlayer(videoUri: Uri) {
    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(player) {
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        // Imposta il listener per il completamento del video
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    player.seekTo(0)
                    player.playWhenReady = true
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                this.layoutParams =
                    ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT
                    )
                this.setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                this.hideController()
                this.setUseController(false)
                this.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
            }
        }
    )
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(navController = rememberNavController())
}