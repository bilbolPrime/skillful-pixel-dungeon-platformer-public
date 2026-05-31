package com.bilboldev.skillfulpixeldungeonplatformer.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Languages;
import com.bilboldev.skillfulpixeldungeonplatformer.messages.Messages;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.HashMap;

public class FontHelper {

    public static class FittedTextBlock {
        public final String text;
        public final float size;
        public final float width;
        public final float height;
        public final float englishWidth;

        private FittedTextBlock(String text, float size, float width, float height, float englishWidth) {
            this.text = text;
            this.size = size;
            this.width = width;
            this.height = height;
            this.englishWidth = englishWidth;
        }
    }

    private enum FontType {
        DEFAULT,
        UNICODE
    }

    private static final int OUTLINE_OFFSET = 1;
    private static final float MIN_FIT_SIZE = 1f;
    private static final float FIT_SIZE_STEP = 0.25f;
    private static final float OVERLAY_SHRINK_FACTOR = 0.95f;
    private static final String UNICODE_FONT_PATH = "fonts/droid_sans.ttf";
    private static final String[] MESSAGE_BUNDLE_BASES = new String[]{
            "actors/actors",
            "custom",
            "items/items",
            "journal/journal",
            "levels/levels",
            "misc/misc",
            "plants/plants",
            "scenes/scenes",
            "ui/ui",
            "windows/windows"
    };
    private static final String DEFAULT_GLYPHS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
            + "\u00A0¡¢£¤¥¦§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿"
            + "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞß"
            + "àáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ"
            + "ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚě"
            + "ĞğĢģĨĩĪīĮįİıĶķĹĺĻļĽľŁłŃńŅņŇňŌōŎŏŐő"
            + "ŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲų"
            + "ŴŵŶŷŸŹźŻżŽžȘșȚțΆΈΉΊΌΎΏΐΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩΪΫ"
            + "άέήίΰαβγδεζηθικλμνξοπρσςτυφχψωϊϋόύώ"
            + "ЁЂЃЄЅІЇЈЉЊЋЌЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
            + "абвгдежзийклмнопрстуфхцчшщъыьэюяёђѓєѕіїјљњћќўџ"
            + "日本語简体中文繁體中文한국어";

    private final HashMap<String, BitmapFont> fonts;
    private final HashMap<String, FittedTextBlock> overlayTextFitCache;
    private String localizedGlyphs;

    private static FontHelper m_instance;

    public static FontHelper getSingleton(){
        if(m_instance == null){
            m_instance = new FontHelper();
        }

        return m_instance;
    }

    public static void reset(){
        if (m_instance != null) {
            m_instance.dispose();
        }
        m_instance = null;
    }

    private FontHelper()
    {
        fonts = new HashMap<String, BitmapFont>();
        overlayTextFitCache = new HashMap<String, FittedTextBlock>();
    }

    public BitmapFont getFont(Color color, float size){
        return getFont(color, size, null);
    }

    public BitmapFont getFont(Color color, float size, String text){
        return getFont(color, size, text, false);
    }

    public BitmapFont getEnglishFont(Color color, float size, String text){
        return getFont(color, size, text, true);
    }

    private BitmapFont getFont(Color color, float size, String text, boolean forceEnglishFont){

        String key = getKey(size, text, forceEnglishFont);

        if(!fonts.containsKey(key)){
            fonts.put(key, makeFont(size, text, forceEnglishFont));
        }

        BitmapFont font = fonts.get(key);
        font.setColor(color);
        return font;
    }

    private BitmapFont makeFont(float size, String text){
        return makeFont(size, text, false);
    }

    private BitmapFont makeFont(float size, String text, boolean forceEnglishFont){
        if (getFontType(text, forceEnglishFont) == FontType.UNICODE) {
            return makeUnicodeFont(size);
        }

        BitmapFont font = new BitmapFont();
        font.getData().setScale(size);
        return font;
    }

    private BitmapFont makeUnicodeFont(float size) {
        if (Gdx.files == null || !Gdx.files.internal(UNICODE_FONT_PATH).exists()) {
            BitmapFont fallbackFont = new BitmapFont();
            fallbackFont.getData().setScale(size);
            return fallbackFont;
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(UNICODE_FONT_PATH));
        PixmapPacker packer = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false);
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = Math.max(12, Math.round(size * 15f));
            parameter.characters = getLocalizedGlyphs();
            parameter.kerning = true;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.packer = packer;
            BitmapFont font = generator.generateFont(parameter);
            font.setUseIntegerPositions(false);
            return font;
        } finally {
            generator.dispose();
            packer.dispose();
        }
    }

    private String getKey(float size, String text){
        return getKey(size, text, false);
    }

    private String getKey(float size, String text, boolean forceEnglishFont){
        return getFontType(text, forceEnglishFont).name().toLowerCase() + "_" + size;
    }

    private FontType getFontType(String text) {
        return getFontType(text, false);
    }

    private FontType getFontType(String text, boolean forceEnglishFont) {
        if (!forceEnglishFont && Messages.lang() != Languages.ENGLISH) {
            return FontType.UNICODE;
        }

        if (text == null || text.isEmpty()) {
            return FontType.DEFAULT;
        }

        return requiresUnicodeFont(text) ? FontType.UNICODE : FontType.DEFAULT;
    }

    private boolean requiresUnicodeFont(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            char glyph = text.charAt(i);
            if (glyph == '\n' || glyph == '\r' || glyph == '\t') {
                continue;
            }

            if (glyph < 32 || glyph > 126) {
                return true;
            }
        }

        return false;
    }

    private String getLocalizedGlyphs() {
        if (localizedGlyphs != null) {
            return localizedGlyphs;
        }

        HashSet<Character> seen = new HashSet<Character>();
        StringBuilder builder = new StringBuilder();
        appendUnique(builder, seen, DEFAULT_GLYPHS);

        for (Languages language : Languages.values()) {
            appendUnique(builder, seen, language.nativeName());
            appendUnique(builder, seen, language.englishName());
        }

        if (Gdx.files != null) {
            collectKnownMessageGlyphs(builder, seen);
            collectMessageGlyphs(Gdx.files.internal("messages"), builder, seen);
        }

        localizedGlyphs = builder.toString();
        return localizedGlyphs;
    }

    private void collectKnownMessageGlyphs(StringBuilder builder, HashSet<Character> seen) {
        collectMessageGlyphs(Gdx.files.internal("messages/exact-match.tsv"), builder, seen);

        String languageCode = Messages.lang().code();
        for (String bundleBase : MESSAGE_BUNDLE_BASES) {
            collectMessageGlyphs(Gdx.files.internal("messages/" + bundleBase + ".properties"), builder, seen);
            if (!"en".equals(languageCode)) {
                collectMessageGlyphs(Gdx.files.internal("messages/" + bundleBase + "_" + languageCode + ".properties"), builder, seen);
            }
        }
    }

    private void collectMessageGlyphs(FileHandle handle, StringBuilder builder, HashSet<Character> seen) {
        if (handle == null || !handle.exists()) {
            return;
        }

        if (handle.isDirectory()) {
            for (FileHandle child : handle.list()) {
                collectMessageGlyphs(child, builder, seen);
            }
            return;
        }

        if (!isRelevantMessageFile(handle.name())) {
            return;
        }

        appendUnique(builder, seen, handle.readString(StandardCharsets.UTF_8.name()));
    }

    private boolean isRelevantMessageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        if (fileName.endsWith(".tsv")) {
            return true;
        }

        if (!fileName.endsWith(".properties")) {
            return false;
        }

        int separator = fileName.lastIndexOf('_');
        int extension = fileName.lastIndexOf('.');
        if (separator < 0 || extension < 0 || separator > extension) {
            return true;
        }

        String code = fileName.substring(separator + 1, extension);
        String normalized = code.replace('_', '-').toLowerCase();
        if ("id".equals(normalized)) {
            normalized = "in";
        }
        return normalized.equals(Messages.lang().code());
    }

    private void appendUnique(StringBuilder builder, HashSet<Character> seen, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        for (int i = 0; i < value.length(); i++) {
            char glyph = value.charAt(i);
            if (glyph == '\r' || glyph == '\n' || seen.contains(glyph)) {
                continue;
            }
            seen.add(glyph);
            builder.append(glyph);
        }
    }

    private void dispose() {
        for (BitmapFont font : fonts.values()) {
            if (font != null) {
                font.dispose();
            }
        }
        fonts.clear();
        overlayTextFitCache.clear();
    }

    public void writeWhite(Batch batch, float size, float x, float y, String text){
        writeOutlined(Color.WHITE, batch, size, x, y, text);
    }

    public void write(Color color, Batch batch, float size, float x, float y, String text){
        writeOutlined(color, batch, size, x, y, text);
    }

    public void writeWhiteRaw(Batch batch, float size, float x, float y, String text){
        writeOutlined(Color.WHITE, batch, size, x, y, text, false);
    }

    public void writeRaw(Color color, Batch batch, float size, float x, float y, String text){
        writeOutlined(color, batch, size, x, y, text, false);
    }

    public GlyphLayout measure(Color color, float size, String text) {
        return measure(color, size, text, false);
    }

    public GlyphLayout measureEnglish(Color color, float size, String text) {
        return measure(color, size, text, true);
    }

    public float fitSize(String text, float maxSize, float maxWidth, float maxHeight) {
        return fitSize(text, maxSize, maxWidth, maxHeight, false);
    }

    public float fitSizeToEnglishFootprint(String englishText,
                                           String localizedText,
                                           float maxSize,
                                           float maxWidth) {
        String normalizedEnglish = UtilsHelper.normalizeLineBreaks(englishText == null ? "" : englishText);
        String normalizedLocalized = UtilsHelper.normalizeLineBreaks(localizedText == null ? "" : localizedText);
        float englishSize = fitSize(normalizedEnglish, maxSize, maxWidth, Float.POSITIVE_INFINITY, true);
        if (Messages.lang() == Languages.ENGLISH) {
            return englishSize;
        }

        GlyphLayout englishLayout = measureEnglish(Color.WHITE, englishSize, normalizedEnglish);
        float footprintWidth = boundedFootprint(maxWidth, englishLayout.width);
        return fitSize(normalizedLocalized, englishSize, footprintWidth, Float.POSITIVE_INFINITY, false);
    }

    public FittedTextBlock fitMultilineToEnglishFootprint(String englishSource,
                                                          String localizedText,
                                                          float maxSize,
                                                          float wrapWidth) {
        String englishText = UtilsHelper.multiLineEnglish(englishSource == null ? "" : englishSource, maxSize, wrapWidth);
        GlyphLayout englishLayout = measureEnglish(Color.WHITE, maxSize, englishText);
        float localizedWrapWidth = Math.max(100f, Math.min(wrapWidth, englishLayout.width));
        String fittedText = UtilsHelper.multiLineRaw(localizedText == null ? "" : localizedText, maxSize, localizedWrapWidth);
        float fittedSize = fitSizeToEnglishFootprint(
                englishText,
                fittedText,
                maxSize,
                englishLayout.width);

        if (fittedSize < maxSize) {
            fittedText = UtilsHelper.multiLineRaw(localizedText == null ? "" : localizedText, fittedSize, localizedWrapWidth);
            fittedSize = fitSizeToEnglishFootprint(
                    englishText,
                    fittedText,
                    maxSize,
                    englishLayout.width);
        }

        GlyphLayout fittedLayout = measure(Color.WHITE, fittedSize, fittedText);
        return new FittedTextBlock(fittedText, fittedSize, fittedLayout.width, fittedLayout.height, englishLayout.width);
    }

    public FittedTextBlock fitOverlayText(String overlayKey,
                                          String englishSource,
                                          String localizedText,
                                          float maxSize,
                                          float wrapWidth,
                                          float maxHeight) {
        String normalizedEnglish = UtilsHelper.normalizeLineBreaks(englishSource == null ? "" : englishSource);
        String normalizedLocalized = UtilsHelper.normalizeLineBreaks(localizedText == null ? "" : localizedText);
        String cacheKey = overlayKey
                + "|" + Messages.lang().code()
                + "|" + maxSize
                + "|" + wrapWidth
                + "|" + maxHeight
                + "|" + normalizedEnglish
                + "|" + normalizedLocalized;

        FittedTextBlock cached = overlayTextFitCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        String englishText = UtilsHelper.multiLineEnglish(normalizedEnglish, maxSize, wrapWidth);
        GlyphLayout englishLayout = measureEnglish(Color.WHITE, maxSize, englishText);
        float localizedWrapWidth = Math.max(100f, Math.min(wrapWidth, englishLayout.width));
        String fittedText = UtilsHelper.multiLineRaw(normalizedLocalized, maxSize, localizedWrapWidth);
        float fittedSize = fitSizeToEnglishFootprint(englishText, fittedText, maxSize, englishLayout.width);
        GlyphLayout fittedLayout = measure(Color.WHITE, fittedSize, fittedText);

        while (maxHeight > 0f && fittedSize > MIN_FIT_SIZE && fittedLayout.height > maxHeight + 0.5f) {
            fittedSize = Math.max(MIN_FIT_SIZE, fittedSize * OVERLAY_SHRINK_FACTOR);
            fittedText = UtilsHelper.multiLineRaw(normalizedLocalized, fittedSize, localizedWrapWidth);
            fittedSize = fitSizeToEnglishFootprint(englishText, fittedText, fittedSize, englishLayout.width);
            fittedLayout = measure(Color.WHITE, fittedSize, fittedText);
        }

        FittedTextBlock fittedBlock = new FittedTextBlock(
                fittedText,
                fittedSize,
                fittedLayout.width,
                fittedLayout.height,
                englishLayout.width);
        overlayTextFitCache.put(cacheKey, fittedBlock);
        return fittedBlock;
    }

    private GlyphLayout measure(Color color, float size, String text, boolean forceEnglishFont) {
        String normalizedText = UtilsHelper.normalizeLineBreaks(text == null ? "" : text);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(getFont(color, size, normalizedText, forceEnglishFont), normalizedText);
        return glyphLayout;
    }

    private float fitSize(String text, float maxSize, float maxWidth, float maxHeight, boolean forceEnglishFont) {
        String normalizedText = UtilsHelper.normalizeLineBreaks(text == null ? "" : text);
        float fittedSize = Math.max(MIN_FIT_SIZE, maxSize);
        while (fittedSize > MIN_FIT_SIZE && exceedsBounds(normalizedText, fittedSize, maxWidth, maxHeight, forceEnglishFont)) {
            fittedSize = Math.max(MIN_FIT_SIZE, fittedSize - FIT_SIZE_STEP);
        }
        return fittedSize;
    }

    private boolean exceedsBounds(String text, float size, float maxWidth, float maxHeight, boolean forceEnglishFont) {
        GlyphLayout glyphLayout = measure(Color.WHITE, size, text, forceEnglishFont);
        return exceeds(maxWidth, glyphLayout.width) || exceeds(maxHeight, glyphLayout.height);
    }

    private boolean exceeds(float limit, float value) {
        return limit > 0f && !Float.isInfinite(limit) && value > limit + 0.5f;
    }

    private float boundedFootprint(float limit, float footprint) {
        if (limit > 0f && !Float.isInfinite(limit)) {
            return Math.min(limit, footprint);
        }
        return footprint;
    }

    private void writeOutlined(Color color, Batch batch, float size, float x, float y, String text) {
        writeOutlined(color, batch, size, x, y, text, true);
    }

    private void writeOutlined(Color color, Batch batch, float size, float x, float y, String text, boolean translate) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String sourceText = text;
        if (translate) {
            text = Messages.maybeTranslate(text);
            size = fitSizeToEnglishFootprint(sourceText, text, size, Float.POSITIVE_INFINITY);
        }
        text = UtilsHelper.normalizeLineBreaks(text);

        float snappedX = Math.round(x);
        float snappedY = Math.round(y);
        Color outlineColor = new Color(0f, 0f, 0f, color.a);
        BitmapFont outlineFont = getFont(outlineColor, size, text);
        for (int offsetX = -OUTLINE_OFFSET; offsetX <= OUTLINE_OFFSET; offsetX++) {
            for (int offsetY = -OUTLINE_OFFSET; offsetY <= OUTLINE_OFFSET; offsetY++) {
                if (offsetX == 0 && offsetY == 0) {
                    continue;
                }

                outlineFont.draw(batch, text, snappedX + offsetX, snappedY + offsetY);
            }
        }

        getFont(color, size, text).draw(batch, text, snappedX, snappedY);
    }
}

