package com.example

import com.example.ai.LyricsEngine
import com.example.model.LyricsLanguage
import com.example.model.MusicStyle
import com.example.model.SongMood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testLyricsGenerationStructure() {
    val lyrics = LyricsEngine.generateLyrics(
      title = "Pyaar Ki Dhun",
      themePrompt = "Romantic evening under stars",
      language = LyricsLanguage.HINDI,
      mood = SongMood.ROMANTIC
    )
    assertNotNull(lyrics)
    assertEquals("Pyaar Ki Dhun", lyrics.title)
    assertTrue(lyrics.intro.isNotBlank())
    assertTrue(lyrics.verse1.isNotBlank())
    assertTrue(lyrics.chorus.isNotBlank())
    assertTrue(lyrics.outro.isNotBlank())
  }

  @Test
  fun testBhojpuriLyricsGeneration() {
    val lyrics = LyricsEngine.generateLyrics(
      title = "Bhojpuriya Dhamaka",
      themePrompt = "Festive celebration",
      language = LyricsLanguage.BHOJPURI,
      mood = SongMood.HAPPY
    )
    assertNotNull(lyrics)
    assertTrue(lyrics.chorus.contains("kajarawa") || lyrics.chorus.contains("dholak"))
  }

  @Test
  fun testBhaktiLyricsGeneration() {
    val lyrics = LyricsEngine.generateLyrics(
      title = "Shiv Stuti",
      themePrompt = "Lord Shiva worship",
      language = LyricsLanguage.BHAKTI,
      mood = SongMood.SPIRITUAL
    )
    assertNotNull(lyrics)
    assertTrue(lyrics.intro.contains("Shiv") || lyrics.intro.contains("Mahadev"))
  }
}
