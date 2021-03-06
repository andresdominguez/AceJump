package com.johnlindquist.acejump.ui

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.impl.EditorImpl
import com.johnlindquist.acejump.search.getVisibleRange
import java.awt.Font
import java.awt.Font.BOLD
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import javax.swing.JComponent

class AceCanvas(val editor: EditorImpl) : JComponent() {
  var jumpLocations: Collection<JumpInfo> = arrayListOf()
  val scheme = EditorColorsManager.getInstance().globalScheme
  val colors = Pair(scheme.defaultBackground, scheme.defaultForeground)
  val fbm: FontBasedMeasurements
  var existingTags = hashSetOf<Pair<Int, Int>>()

  init {
    font = Font(scheme.editorFontName, BOLD, scheme.editorFontSize)
    fbm = FontBasedMeasurements()
  }

  inner class FontBasedMeasurements() {
    var font = getFont()!!
    val fontWidth = getFontMetrics(font).stringWidth("w")
    val fontHeight = font.size
    val lineHeight = editor.lineHeight
    val lineSpacing = scheme.lineSpacing
    val rectMarginWidth = fontWidth / 2
    val doubleRectMarginWidth = rectMarginWidth * 2
    val fontSpacing = fontHeight * lineSpacing
    val rectHOffset = fontSpacing - fontHeight
    val rectWidth = doubleRectMarginWidth
    val hOffset = fontHeight - fontSpacing
  }

  override fun paint(graphics: Graphics) {
    if (jumpLocations.isEmpty())
      return

    super.paint(graphics)

    val g2d = graphics as Graphics2D
    existingTags = hashSetOf<Pair<Int, Int>>()
    jumpLocations.orEmpty().forEach { it.paintMe(g2d, this@AceCanvas) }
  }

  fun registerTag(point: Pair<Int, Int>, tag: String) {
    (-1..(tag.length + 1)).forEach {
      existingTags.add(Pair(point.first + it * fbm.fontWidth, point.second))
    }
  }

  fun isFree(point: Pair<Int, Int>): Boolean {
    return !existingTags.contains(point)
  }
}