package com.watabou.pixeldungeon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watabou.pixeldungeon.model.LogMessage
import com.watabou.pixeldungeon.ui.theme.DungeonBorder
import com.watabou.pixeldungeon.ui.theme.DungeonDark

@Composable
fun MessageLogView(
    messages: List<LogMessage>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(DungeonDark.copy(alpha = 0.85f))
            .border(1.dp, DungeonBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("message_log")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            reverseLayout = false
        ) {
            items(messages.take(15), key = { it.id }) { msg ->
                Text(
                    text = msg.text,
                    color = Color(msg.colorHex),
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}
