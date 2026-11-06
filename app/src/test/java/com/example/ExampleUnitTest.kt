package com.example

import com.example.ai.LyricsEngine
import com.example.model.LyricsLanguage
import com.example.model.MusicStyle
import com.example.model.SongMood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLyricsGeneration() {
    val lyrics = LyricsEngine.generateLyrics(
      title = "Tere Sang",
      themePrompt = "Romance in the rain",
      language = LyricsLanguage.HINDI,
      mood = SongMood.ROMANTIC
    )
    assertNotNull(lyrics)
    assertEquals("Tere Sang", lyrics.title)
    assertTrue(lyrics.intro.isNotBlank())
    assertTrue(lyrics.verse1.isNotBlank())
    assertTrue(lyrics.chorus.isNotBlank())
    assertTrue(lyrics.outro.isNotBlank())
  }

  @Test
  fun testMusicStylesBpm() {
    assertTrue(MusicStyle.BOLLYWOOD_2026.bpm == 118)
    assertTrue(MusicStyle.BHOJPURI.bpm == 130)
    assertTrue(MusicStyle.HINDI_ROMANTIC.bpm == 82)
  }
}
