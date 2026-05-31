package com.bilboldev.skillfulpixeldungeonplatformer.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Messages {

    private static final String ROOT_PACKAGE = "com.bilboldev.skillfulpixeldungeonplatformer.";
    private static final String EXACT_MATCH_FILE = "messages/exact-match.tsv";
    private static final Pattern FORMAT_PLACEHOLDER_PATTERN = Pattern.compile("%\\d*\\$?[sdifouxXeEgGcfbBhHtn%]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern ARG_INDEX_PATTERN = Pattern.compile("%(\\d+)\\$");
    private static final Pattern TRAILING_LEVEL_SUFFIX_PATTERN = Pattern.compile("^(.*?)(\\s\\+\\d+)$");
    private static final String[] BUNDLE_FILES = new String[] {
            "messages/actors/actors",
            "messages/items/items",
            "messages/journal/journal",
            "messages/levels/levels",
            "messages/misc/misc",
            "messages/plants/plants",
            "messages/scenes/scenes",
            "messages/ui/ui",
            "messages/windows/windows",
            "messages/custom"
    };

    public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";

    private static final Properties properties = new Properties();
    private static final HashMap<String, String> classKeyAliases = new HashMap<>();
    private static final HashMap<String, String> exactTextKeys = new HashMap<>();
    private static final HashMap<Character, ArrayList<ExactPatternEntry>> exactPatternKeys = new HashMap<>();
    private static final ArrayList<ExactPatternEntry> leadingPatternKeys = new ArrayList<>();
    private static final HashMap<String, String> bundleTextKeys = new HashMap<>();
    private static final HashMap<Character, ArrayList<ExactPatternEntry>> bundlePatternKeys = new HashMap<>();
    private static final ArrayList<ExactPatternEntry> leadingBundlePatternKeys = new ArrayList<>();
    private static final HashMap<String, String> translationCache = new HashMap<>();
    private static final Object[] NO_ARGS = new Object[0];

    private static Languages language = Languages.ENGLISH;
    private static Locale locale = Locale.ENGLISH;

    static {
        registerClassKeyAlias("actors.mobs.acidicscorpio", "actors.mobs.acidic");
        registerClassKeyAlias("actors.mobs.albinorat", "actors.mobs.albino");
        registerClassKeyAlias("actors.mobs.burningfist", "actors.mobs.yogfist$burningfist");
        registerClassKeyAlias("actors.mobs.dwarfwarlock", "actors.mobs.warlock");
        registerClassKeyAlias("actors.mobs.elemental", "actors.mobs.elemental$fireelemental");
        registerClassKeyAlias("actors.mobs.evileye", "actors.mobs.eye");
        registerClassKeyAlias("actors.mobs.larva", "actors.mobs.yogdzewa$larva");
        registerClassKeyAlias("actors.mobs.rottingfist", "actors.mobs.yogfist$rottingfist");
        registerClassKeyAlias("items.armor.cloth", "items.armor.clotharmor");
        registerClassKeyAlias("items.wands.magicmissilewand", "items.wands.wandofmagicmissile");
    }

    private Messages() {
    }

    public static Languages lang() {
        return language;
    }

    public static Locale locale() {
        return locale;
    }

    public static void setup(Languages nextLanguage) {
        language = nextLanguage == null ? Languages.ENGLISH : nextLanguage;
        locale = language.locale();
        properties.clear();
        exactTextKeys.clear();
        exactPatternKeys.clear();
        leadingPatternKeys.clear();
        bundleTextKeys.clear();
        bundlePatternKeys.clear();
        leadingBundlePatternKeys.clear();
        translationCache.clear();

        for (String bundleFile : BUNDLE_FILES) {
            loadBundle(bundleFile + ".properties", true);
            if (language != Languages.ENGLISH) {
                loadBundle(bundleFile + "_" + language.code() + ".properties", false);
                if (language == Languages.INDONESIAN) {
                    loadBundle(bundleFile + "_id.properties", false);
                }
            }
        }

        loadExactMatchIndex(EXACT_MATCH_FILE);
    }

    public static String get(String key, Object... args) {
        String value = getValue(normalizeKey(key));
        return args != null && args.length > 0 ? format(value, args) : value;
    }

    public static String get(Object object, String key, Object... args) {
        return object == null ? NO_TEXT_FOUND : get(object.getClass(), key, args);
    }

    public static String get(Class<?> type, String key, Object... args) {
        Class<?> current = type;
        while (current != null) {
            String fullKey = normalizeKey(classKey(current, key));
            String value = properties.getProperty(fullKey);
            if (value == null) {
                String aliasKey = aliasKey(fullKey);
                if (aliasKey != null) {
                    value = properties.getProperty(aliasKey);
                }
            }
            if (value != null) {
                return args != null && args.length > 0 ? format(value, args) : value;
            }
            current = current.getSuperclass();
        }

        return NO_TEXT_FOUND;
    }

    public static String getDirect(Class<?> type, String key, Object... args) {
        if (type == null) {
            return NO_TEXT_FOUND;
        }

        String fullKey = normalizeKey(classKey(type, key));
        String value = properties.getProperty(fullKey);
        if (value == null) {
            String aliasKey = aliasKey(fullKey);
            if (aliasKey != null) {
                value = properties.getProperty(aliasKey);
            }
        }

        if (value == null) {
            return NO_TEXT_FOUND;
        }

        return args != null && args.length > 0 ? format(value, args) : value;
    }

    public static String getOrNull(Class<?> type, String key, Object... args) {
        String value = get(type, key, args);
        return NO_TEXT_FOUND.equals(value) ? null : value;
    }

    public static String getDirectOrNull(Class<?> type, String key, Object... args) {
        String value = getDirect(type, key, args);
        return NO_TEXT_FOUND.equals(value) ? null : value;
    }

    public static String getOrNull(String key, Object... args) {
        String value = get(key, args);
        return NO_TEXT_FOUND.equals(value) ? null : value;
    }

    public static String format(String format, Object... args) {
        return maybeTranslateInternal(format, args == null ? NO_ARGS : args);
    }

    public static String maybeTranslate(String value) {
        return maybeTranslateInternal(value, NO_ARGS);
    }

    public static String maybeTranslate(String value, Object... args) {
        return maybeTranslateInternal(value, args == null ? NO_ARGS : args);
    }

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(locale) + value.substring(1);
    }

    public static String upperCase(String value) {
        return value == null ? null : value.toUpperCase(locale);
    }

    public static String lowerCase(String value) {
        return value == null ? null : value.toLowerCase(locale);
    }

    public static String capitalizeForDisplay(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (language != Languages.ENGLISH && language != Languages.FRENCH) {
            return value;
        }

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (!Character.isLetter(current)) {
                continue;
            }

            String original = value.substring(i, i + 1);
            String upper = original.toUpperCase(locale);
            if (original.equals(upper)) {
                return value;
            }

            return value.substring(0, i) + upper + value.substring(i + 1);
        }

        return value;
    }

    private static void loadBundle(String assetPath, boolean indexBundleValues) {
        InputStream inputStream = null;
        try {
            inputStream = openAsset(assetPath);
            if (inputStream == null) {
                return;
            }

            Properties loaded = new Properties();
            loaded.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            for (String key : loaded.stringPropertyNames()) {
                String normalizedKey = normalizeKey(key);
                String propertyValue = decodeEscapedText(loaded.getProperty(key));
                properties.setProperty(normalizedKey, propertyValue);
                if (indexBundleValues) {
                    indexBundleValue(normalizedKey, propertyValue);
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void loadExactMatchIndex(String assetPath) {
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = openAsset(assetPath);
            if (inputStream == null) {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\t", 3);
                if (parts.length < 2) {
                    continue;
                }

                String normalized = normalizeText(decodeEscapedText(parts[0]));
                String key = parts[1];
                exactTextKeys.put(normalized, key);

                if (parts.length < 3 || parts[2].isEmpty()) {
                    continue;
                }

                String template = decodeEscapedText(URLDecoder.decode(parts[2], "UTF-8"));
                if (!FORMAT_PLACEHOLDER_PATTERN.matcher(template).find()) {
                    continue;
                }

                ExactPatternEntry entry = new ExactPatternEntry(key, template);
                if (normalized.startsWith("%ARG%")) {
                    leadingPatternKeys.add(entry);
                } else {
                    char firstChar = normalized.charAt(0);
                    ArrayList<ExactPatternEntry> entries = exactPatternKeys.get(firstChar);
                    if (entries == null) {
                        entries = new ArrayList<>();
                        exactPatternKeys.put(firstChar, entries);
                    }
                    entries.add(entry);
                }
            }
        } catch (IOException ignored) {
        } catch (IllegalArgumentException ignored) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void indexBundleValue(String key, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        String normalized = normalizeText(value);
        if (normalized.isEmpty()) {
            return;
        }

        if (!FORMAT_PLACEHOLDER_PATTERN.matcher(value).find()) {
            if (!bundleTextKeys.containsKey(normalized)) {
                bundleTextKeys.put(normalized, key);
            }
            return;
        }

        ExactPatternEntry entry = new ExactPatternEntry(key, value);
        if (normalized.startsWith("%ARG%")) {
            leadingBundlePatternKeys.add(entry);
            return;
        }

        char firstChar = normalized.charAt(0);
        ArrayList<ExactPatternEntry> entries = bundlePatternKeys.get(firstChar);
        if (entries == null) {
            entries = new ArrayList<>();
            bundlePatternKeys.put(firstChar, entries);
        }
        entries.add(entry);
    }

    private static InputStream openAsset(String assetPath) throws IOException {
        if (Gdx.files != null) {
            FileHandle handle = Gdx.files.internal(assetPath);
            if (handle.exists()) {
                return handle.read();
            }
        }

        ClassLoader classLoader = Messages.class.getClassLoader();
        return classLoader == null ? null : classLoader.getResourceAsStream(assetPath);
    }

    private static String getValue(String key) {
        String value = properties.getProperty(key);
        return value == null ? NO_TEXT_FOUND : value;
    }

    private static String maybeTranslateInternal(String value, Object[] args) {
        if (value == null) {
            return null;
        }

        if (args.length == 0 && translationCache.containsKey(value)) {
            return translationCache.get(value);
        }

        TranslationMatch match = findTranslationMatch(value);
        String translated;
        if (match == null) {
            String suffixAdjusted = args.length == 0 ? maybeTranslateTrailingLevelSuffix(value) : null;
            translated = suffixAdjusted != null ? suffixAdjusted : (args.length > 0 ? formatRaw(value, args) : value);
        } else {
            String localized = properties.getProperty(normalizeKey(match.key));
            if (localized == null) {
                translated = args.length > 0 ? formatRaw(value, args) : value;
            } else if (args.length > 0) {
                translated = formatRaw(localized, args);
            } else if (match.capturedArgs != null) {
                translated = substituteRawFormat(localized, match.capturedArgs);
            } else {
                translated = localized;
            }
        }

        if (args.length == 0) {
            translationCache.put(value, translated);
        }
        return translated;
    }

    private static String maybeTranslateTrailingLevelSuffix(String value) {
        Matcher matcher = TRAILING_LEVEL_SUFFIX_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return null;
        }

        String baseValue = matcher.group(1);
        String suffix = matcher.group(2);
        if (baseValue == null || baseValue.isEmpty() || suffix == null || suffix.isEmpty()) {
            return null;
        }

        return maybeTranslateInternal(baseValue, NO_ARGS) + suffix;
    }

    private static TranslationMatch findTranslationMatch(String value) {
        String normalized = normalizeText(value);
        if (normalized.isEmpty()) {
            return null;
        }

        String directKey = exactTextKeys.get(normalized);
        if (directKey != null) {
            return new TranslationMatch(directKey, null);
        }

        String bundleDirectKey = bundleTextKeys.get(normalized);
        if (bundleDirectKey != null) {
            return new TranslationMatch(bundleDirectKey, null);
        }

        if (FORMAT_PLACEHOLDER_PATTERN.matcher(value).find()) {
            return null;
        }

        ArrayList<ExactPatternEntry> candidates = exactPatternKeys.get(normalized.charAt(0));
        TranslationMatch match = findPatternMatch(candidates, value);
        if (match != null) {
            return match;
        }

        match = findPatternMatch(bundlePatternKeys.get(normalized.charAt(0)), value);
        if (match != null) {
            return match;
        }

        match = findPatternMatch(leadingPatternKeys, value);
        if (match != null) {
            return match;
        }

        return findPatternMatch(leadingBundlePatternKeys, value);
    }

    private static TranslationMatch findPatternMatch(ArrayList<ExactPatternEntry> entries, String value) {
        if (entries == null) {
            return null;
        }

        for (ExactPatternEntry entry : entries) {
            TranslationMatch match = entry.match(value);
            if (match != null) {
                return match;
            }
        }

        return null;
    }

    private static String substituteRawFormat(String format, String[] args) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = FORMAT_PLACEHOLDER_PATTERN.matcher(format);
        int last = 0;
        int nextArg = 0;

        while (matcher.find()) {
            builder.append(format, last, matcher.start());
            String token = matcher.group();

            if ("%%".equals(token)) {
                builder.append('%');
            } else if ("%n".equals(token)) {
                builder.append('\n');
            } else {
                int argIndex = argumentIndex(token, nextArg);
                if (argIndex >= 0 && argIndex < args.length) {
                    builder.append(args[argIndex]);
                } else {
                    builder.append(token);
                }

                if (token.indexOf('$') < 0) {
                    nextArg++;
                }
            }

            last = matcher.end();
        }

        builder.append(format.substring(last));
        return builder.toString();
    }

    private static int argumentIndex(String token, int defaultIndex) {
        Matcher matcher = ARG_INDEX_PATTERN.matcher(token);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) - 1;
        }
        return defaultIndex;
    }

    private static void registerClassKeyAlias(String sourcePrefix, String targetPrefix) {
        classKeyAliases.put(normalizeKey(sourcePrefix), normalizeKey(targetPrefix));
    }

    private static String formatRaw(String format, Object[] args) {
        try {
            return String.format(locale, format, args);
        } catch (IllegalFormatException e) {
            return format;
        }
    }

    private static String decodeEscapedText(String value) {
        if (value == null || value.indexOf('\\') < 0) {
            return value;
        }

        StringBuilder builder = new StringBuilder(value.length());
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (!escaping) {
                if (current == '\\') {
                    escaping = true;
                } else {
                    builder.append(current);
                }
                continue;
            }

            switch (current) {
                case 'n':
                    builder.append('\n');
                    break;
                case 'r':
                    builder.append('\r');
                    break;
                case 't':
                    builder.append('\t');
                    break;
                case '\\':
                    builder.append('\\');
                    break;
                default:
                    builder.append('\\').append(current);
                    break;
            }
            escaping = false;
        }

        if (escaping) {
            builder.append('\\');
        }

        return builder.toString();
    }

    private static String classKey(Class<?> type, String key) {
        String className = type.getName();
        if (className.startsWith(ROOT_PACKAGE)) {
            className = className.substring(ROOT_PACKAGE.length());
        }
        className = remapClassKey(className);
        return className + "." + key;
    }

    private static String remapClassKey(String className) {
        if (className.startsWith("units.mobs.")) {
            return "actors.mobs." + simpleClassSegment(className);
        }

        if (className.startsWith("items.weapons.melee.wands.")) {
            return "items.wands." + simpleClassSegment(className);
        }

        if (className.startsWith("items.weapons.melee.")) {
            return "items.weapon.melee." + simpleClassSegment(className);
        }

        return className;
    }

    private static String simpleClassSegment(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot >= 0 ? className.substring(lastDot + 1) : className;
    }

    private static String aliasKey(String fullKey) {
        for (String sourcePrefix : classKeyAliases.keySet()) {
            if (fullKey.startsWith(sourcePrefix + ".")) {
                return classKeyAliases.get(sourcePrefix) + fullKey.substring(sourcePrefix.length());
            }
        }
        return null;
    }

    private static String normalizeKey(String key) {
        return key.toLowerCase(Locale.ENGLISH);
    }

    private static String normalizeText(String value) {
        String normalized = value.trim();
        normalized = normalized.replace('\u2019', '\'').replace('\u201C', '"').replace('\u201D', '"');
        normalized = FORMAT_PLACEHOLDER_PATTERN.matcher(normalized).replaceAll("%ARG%");
        normalized = WHITESPACE_PATTERN.matcher(normalized).replaceAll(" ");
        return normalized.toLowerCase(Locale.ENGLISH);
    }

    private static class TranslationMatch {
        private final String key;
        private final String[] capturedArgs;

        private TranslationMatch(String key, String[] capturedArgs) {
            this.key = key;
            this.capturedArgs = capturedArgs;
        }
    }

    private static class ExactPatternEntry {
        private final String key;
        private final Pattern pattern;

        private ExactPatternEntry(String key, String template) {
            this.key = key;
            this.pattern = Pattern.compile(templatePattern(template), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
        }

        private TranslationMatch match(String value) {
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                return null;
            }

            String[] args = new String[matcher.groupCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = matcher.group(i + 1);
            }
            return new TranslationMatch(key, args);
        }

        private static String templatePattern(String template) {
            StringBuilder regex = new StringBuilder("^");
            Matcher matcher = FORMAT_PLACEHOLDER_PATTERN.matcher(template);
            int last = 0;

            while (matcher.find()) {
                regex.append(Pattern.quote(template.substring(last, matcher.start())));
                String token = matcher.group();

                if ("%%".equals(token)) {
                    regex.append('%');
                } else if ("%n".equals(token)) {
                    regex.append("\\\\R");
                } else {
                    regex.append("(.+?)");
                }

                last = matcher.end();
            }

            regex.append(Pattern.quote(template.substring(last)));
            regex.append('$');
            return regex.toString();
        }
    }
}
