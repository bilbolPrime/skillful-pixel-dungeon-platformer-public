package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;
import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;

import java.util.ArrayList;

public class UtilsHelper {
    public static String platformKey(int tileX, int tileY){
        return tileX + "_" + tileY;
    }

    public static double distance(Unit unita, Unit unitb){
        return Math.sqrt(Math.pow(unita.x - unitb.x, 2) + Math.pow(unita.y - unitb.y, 2));
    }

    public static String multiLine(String fullLine, int size, float width){
        return multiLineInternal(fullLine, size, width, true, false);
    }

    public static String multiLineRaw(String fullLine, float size, float width) {
        return multiLineInternal(fullLine, size, width, false, false);
    }

    public static String multiLineEnglish(String fullLine, float size, float width) {
        return multiLineInternal(fullLine, size, width, false, true);
    }

    private static String multiLineInternal(String fullLine, float size, float width, boolean translate, boolean englishFontMetrics){
        if (fullLine == null || fullLine.isEmpty()) {
            return "";
        }

        if (translate) {
            fullLine = Messages.maybeTranslate(fullLine);
        }
        fullLine = normalizeLineBreaks(fullLine);
        if (width <= 0f) {
            return fullLine;
        }

        if (!englishFontMetrics && requiresCharacterWrapping(fullLine)) {
            return wrapByCharacter(fullLine, size, width, englishFontMetrics);
        }

        return wrapByWord(fullLine, size, width, englishFontMetrics);
    }

    private static boolean requiresCharacterWrapping(String text) {
        Languages language = Messages.lang();
        return (language == Languages.JAPANESE
                || language == Languages.CHINESE
                || language == Languages.CHINESE_TRADITIONAL)
                && containsCharacterWrappedGlyphs(text);
    }

    private static boolean containsCharacterWrappedGlyphs(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            if (isCharacterWrappedGlyph(text.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isCharacterWrappedGlyph(char glyph) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(glyph);
        return block == Character.UnicodeBlock.HIRAGANA
                || block == Character.UnicodeBlock.KATAKANA
                || block == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || block == Character.UnicodeBlock.HANGUL_JAMO
                || block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
                || block == Character.UnicodeBlock.HANGUL_SYLLABLES;
    }

    private static String wrapByWord(String fullLine, float size, float width, boolean englishFontMetrics) {
        GlyphLayout glyphLayout = new GlyphLayout();
        ArrayList<String> wrappedLines = new ArrayList<String>();
        String[] paragraphs = fullLine.split("\n", -1);

        for (String paragraph : paragraphs) {
            wrapParagraphByWord(paragraph, size, width, glyphLayout, wrappedLines, englishFontMetrics);
        }

        return String.join("\n", wrappedLines);
    }

    private static void wrapParagraphByWord(String paragraph,
                                            float size,
                                            float width,
                                            GlyphLayout glyphLayout,
                                            ArrayList<String> wrappedLines,
                                            boolean englishFontMetrics) {
        String trimmed = paragraph == null ? "" : paragraph.trim();
        if (trimmed.isEmpty()) {
            wrappedLines.add("");
            return;
        }

        String[] words = trimmed.split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }

            if (currentLine.length() == 0) {
                appendWrappedWord(word, size, width, glyphLayout, currentLine, wrappedLines, englishFontMetrics);
                continue;
            }

            String candidate = currentLine + " " + word;
            if (measureTextWidth(candidate, size, glyphLayout, englishFontMetrics) <= width) {
                currentLine.append(' ').append(word);
                continue;
            }

            wrappedLines.add(currentLine.toString());
            currentLine.setLength(0);
            appendWrappedWord(word, size, width, glyphLayout, currentLine, wrappedLines, englishFontMetrics);
        }

        wrappedLines.add(currentLine.toString());
    }

    private static void appendWrappedWord(String word,
                                          float size,
                                          float width,
                                          GlyphLayout glyphLayout,
                                          StringBuilder currentLine,
                                          ArrayList<String> wrappedLines,
                                          boolean englishFontMetrics) {
        if (measureTextWidth(word, size, glyphLayout, englishFontMetrics) <= width) {
            currentLine.append(word);
            return;
        }

        ArrayList<String> segments = wrapTokenByCharacter(word, size, width, glyphLayout, englishFontMetrics);
        for (int i = 0; i < segments.size() - 1; i++) {
            wrappedLines.add(segments.get(i));
        }
        currentLine.append(segments.get(segments.size() - 1));
    }

    private static String wrapByCharacter(String fullLine, float size, float width, boolean englishFontMetrics) {
        GlyphLayout glyphLayout = new GlyphLayout();
        ArrayList<String> wrappedLines = new ArrayList<String>();
        String[] paragraphs = fullLine.split("\n", -1);

        for (String paragraph : paragraphs) {
            if (paragraph == null || paragraph.isEmpty()) {
                wrappedLines.add("");
                continue;
            }

            wrappedLines.addAll(wrapTokenByCharacter(paragraph, size, width, glyphLayout, englishFontMetrics));
        }

        return String.join("\n", wrappedLines);
    }

    private static ArrayList<String> wrapTokenByCharacter(String text,
                                                          float size,
                                                          float width,
                                                          GlyphLayout glyphLayout,
                                                          boolean englishFontMetrics) {
        ArrayList<String> segments = new ArrayList<String>();
        StringBuilder segment = new StringBuilder();
        float segmentWidth = 0f;

        for (int i = 0; i < text.length(); i++) {
            char glyph = text.charAt(i);
            String token = String.valueOf(glyph);
            float tokenWidth = measureTextWidth(token, size, glyphLayout, englishFontMetrics);
            if (segment.length() > 0 && segmentWidth + tokenWidth > width) {
                segments.add(segment.toString());
                segment.setLength(0);
                segmentWidth = 0f;
            }

            segment.append(glyph);
            segmentWidth += tokenWidth;
        }

        if (segment.length() > 0 || segments.isEmpty()) {
            segments.add(segment.toString());
        }

        return segments;
    }

    private static float measureTextWidth(String text, float size, GlyphLayout glyphLayout, boolean englishFontMetrics) {
        if (englishFontMetrics) {
            glyphLayout.setText(FontHelper.getSingleton().getEnglishFont(Color.WHITE, size, text), text);
            return glyphLayout.width;
        }

        glyphLayout.setText(FontHelper.getSingleton().getFont(Color.WHITE, size, text), text);
        return glyphLayout.width;
    }

    public static String normalizeLineBreaks(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\\n", "\n");
    }
}

