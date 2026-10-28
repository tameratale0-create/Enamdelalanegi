package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VoiceChat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.ListingItem
import com.example.ui.DelalaViewModel
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessagesScreen(
    viewModel: DelalaViewModel,
    modifier: Modifier = Modifier
) {
    val activeListing by viewModel.activeChatListing.collectAsState()
    val chatMessages by viewModel.currentChatMessages.collectAsState()
    val rawListings by viewModel.rawListings.collectAsState()
    val isAmharic by viewModel.isAmharic.collectAsState()

    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val liveAmplitudes by viewModel.liveAmplitudes.collectAsState()
    val playingMessageId by viewModel.playingMessageId.collectAsState()
    val playbackProgress by viewModel.playbackProgress.collectAsState()

    var textInput by remember { mutableStateOf("") }

    if (activeListing == null) {
        // Conversations Overview Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = if (isAmharic) "የመልእክትና የድምጽ መለዋወጫ" else "Messages & Voice Notes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isAmharic) "ከአከራዮች እና ከሻጮች ጋር በጽሁፍ ወይም በድምጽ መልእክት ይነጋገሩ።"
                else "Chat with landlords and sellers via text and direct voice recordings.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isAmharic) "ንቁ ንግግሮች" else "Active Inquiries",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rawListings) { item ->
                    ConversationItemCard(
                        item = item,
                        isAmharic = isAmharic,
                        onClick = { viewModel.openChatForListing(item) }
                    )
                }
            }
        }
    } else {
        // Active Chat Thread Screen
        val currentListing = activeListing!!

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Chat Header
            Surface(
                color = EmeraldDark,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.closeChat() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentListing.posterName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${currentListing.title} (${currentListing.location})",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Escrow Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldSecondary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAmharic) "አስተማማኝ አደራ" else "Escrow Protected",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick reply presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val presets = if (isAmharic) listOf(
                    "ቦታውን ማየት እፈልጋለሁ",
                    "ዋጋው ይቀነሳል?",
                    "የካርታ ኮፒ አሳዩኝ",
                    "በአደራ ክፍያ እንስማማ"
                ) else listOf(
                    "I want to visit",
                    "Is price negotiable?",
                    "Send Carta copy",
                    "Let's use Escrow"
                )

                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { viewModel.sendTextMessage(preset) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Messages LazyColumn
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        isAmharic = isAmharic,
                        isPlaying = playingMessageId == msg.id,
                        playbackProgress = if (playingMessageId == msg.id) playbackProgress else 0f,
                        onPlayClick = {
                            viewModel.playVoiceMessage(msg.id, msg.voiceDurationSeconds.coerceAtLeast(3))
                        }
                    )
                }
            }

            // Voice Recording Banner (When user is recording audio)
            AnimatedVisibility(visible = isRecordingVoice) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ድምጽ በመቅዳት ላይ... 00:${recordingSeconds.toString().padStart(2, '0')}",
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Live Waveform visualizer
                        VoiceWaveformVisualizer(
                            amplitudes = liveAmplitudes,
                            color = Color.Red,
                            modifier = Modifier.width(90.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.cancelVoiceRecording() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                            }

                            Button(
                                onClick = { viewModel.stopAndSendVoiceRecording() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(if (isAmharic) "ላክ" else "Send", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Message Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = if (isAmharic) "መልእክት እዚህ ይጻፉ..." else "Type message here...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_text_input"),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    if (textInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                viewModel.sendTextMessage(textInput)
                                textInput = ""
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                                .testTag("send_text_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    } else {
                        // Voice Record Button (Tap to record or hold)
                        IconButton(
                            onClick = {
                                if (isRecordingVoice) {
                                    viewModel.stopAndSendVoiceRecording()
                                } else {
                                    viewModel.startVoiceRecording()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isRecordingVoice) Color.Red else GoldSecondary)
                                .testTag("voice_record_button")
                        ) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Voice Record",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItemCard(
    item: ListingItem,
    isAmharic: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(EmeraldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VoiceChat,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.posterName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = item.title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.location} • ${item.posterRole.amharicName}",
                    fontSize = 11.sp,
                    color = EmeraldPrimary
                )
            }

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = GoldSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isAmharic: Boolean,
    isPlaying: Boolean,
    playbackProgress: Float,
    onPlayClick: () -> Unit
) {
    val isMe = message.isFromMe
    val alignment = if (isMe) Alignment.End else Alignment.Start
    val bgColor = if (isMe) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface

    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = message.senderName,
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            color = bgColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.isVoiceMessage) {
                    // Voice Note UI
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isMe) GoldSecondary else EmeraldPrimary)
                                .clickable { onPlayClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            VoiceWaveformVisualizer(
                                amplitudes = message.voiceAmplitudes,
                                progress = playbackProgress,
                                isPlaying = isPlaying,
                                color = if (isMe) Color.White else EmeraldPrimary,
                                modifier = Modifier.width(130.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "የድምጽ መልእክት (0:${message.voiceDurationSeconds.toString().padStart(2, '0')})",
                                fontSize = 10.sp,
                                color = if (isMe) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                            )
                        }
                    }
                } else {
                    // Text Message
                    Text(
                        text = message.messageText ?: "",
                        fontSize = 13.sp,
                        color = textColor,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = timeFormatted,
                    fontSize = 9.sp,
                    color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
