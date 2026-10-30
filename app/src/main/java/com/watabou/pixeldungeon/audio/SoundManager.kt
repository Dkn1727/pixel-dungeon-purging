package com.watabou.pixeldungeon.audio

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log

class SoundManager private constructor(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()
    private var mediaPlayer: MediaPlayer? = null

    var sfxEnabled: Boolean = true
    var musicEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                stopMusic()
            }
        }

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()

            preloadSounds()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing SoundPool", e)
        }
    }

    private fun preloadSounds() {
        val soundFiles = listOf(
            "snd_hit.mp3",
            "snd_miss.mp3",
            "snd_gold.mp3",
            "snd_eat.mp3",
            "snd_drink.mp3",
            "snd_zap.mp3",
            "snd_levelup.mp3",
            "snd_death.mp3",
            "snd_door_open.mp3",
            "snd_descend.mp3",
            "snd_step.mp3",
            "snd_water.mp3",
            "snd_unlock.mp3",
            "snd_item.mp3",
            "snd_trap.mp3",
            "snd_bones.mp3",
            "snd_rocks.mp3",
            "snd_burning.mp3",
            "snd_puff.mp3",
            "snd_shatter.mp3",
            "snd_secret.mp3",
            "snd_alert.mp3",
            "snd_badge.mp3",
            "snd_boss.mp3",
            "snd_teleport.mp3",
            "snd_click.mp3"
        )

        soundFiles.forEach { filename ->
            try {
                val afd: AssetFileDescriptor = context.assets.openFd(filename)
                val id = soundPool?.load(afd, 1) ?: 0
                soundMap[filename] = id
            } catch (e: Exception) {
                // If not found in assets, skip gracefully
            }
        }
    }

    fun playSfx(name: String, volume: Float = 1.0f) {
        if (!sfxEnabled) return
        val soundId = soundMap[name]
        if (soundId != null && soundId != 0) {
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }

    fun playHit() = playSfx("snd_hit.mp3")
    fun playMiss() = playSfx("snd_miss.mp3")
    fun playGold() = playSfx("snd_gold.mp3")
    fun playEat() = playSfx("snd_eat.mp3")
    fun playDrink() = playSfx("snd_drink.mp3")
    fun playZap() = playSfx("snd_zap.mp3")
    fun playLevelUp() = playSfx("snd_levelup.mp3")
    fun playDeath() = playSfx("snd_death.mp3")
    fun playDoor() = playSfx("snd_door_open.mp3")
    fun playDescend() = playSfx("snd_descend.mp3")
    fun playStep() = playSfx("snd_step.mp3", 0.35f)
    fun playWater() = playSfx("snd_water.mp3")
    fun playUnlock() = playSfx("snd_unlock.mp3")
    fun playItem() = playSfx("snd_item.mp3")
    fun playTrap() = playSfx("snd_trap.mp3")
    fun playRocks() = playSfx("snd_rocks.mp3")
    fun playShatter() = playSfx("snd_shatter.mp3")
    fun playSecret() = playSfx("snd_secret.mp3")
    fun playBadge() = playSfx("snd_badge.mp3")
    fun playTeleport() = playSfx("snd_teleport.mp3")
    fun playClick() = playSfx("snd_click.mp3", 0.5f)

    fun playMusic(trackName: String = "game.mp3") {
        if (!musicEnabled) return
        try {
            stopMusic()
            val afd = context.assets.openFd(trackName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = true
                setVolume(0.4f, 0.4f)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing music $trackName", e)
        }
    }

    fun stopMusic() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
