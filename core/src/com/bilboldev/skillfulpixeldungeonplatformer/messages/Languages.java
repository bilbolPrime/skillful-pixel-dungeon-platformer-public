package com.bilboldev.skillfulpixeldungeonplatformer.messages;

import java.util.Locale;

public enum Languages {
    ENGLISH("en", "English", "English", Locale.ENGLISH),
    BELARUSIAN("be", "Belarusian", "Беларуская", new Locale("be")),
    CZECH("cs", "Czech", "Čeština", new Locale("cs")),
    GERMAN("de", "German", "Deutsch", Locale.GERMAN),
    GREEK("el", "Greek", "Ελληνικά", new Locale("el")),
    ESPERANTO("eo", "Esperanto", "Esperanto", new Locale("eo")),
    SPANISH("es", "Spanish", "Español", new Locale("es")),
    FRENCH("fr", "French", "Français", Locale.FRENCH),
    HUNGARIAN("hu", "Hungarian", "Magyar", new Locale("hu")),
    INDONESIAN("in", "Indonesian", "Bahasa Indonesia", new Locale("id")),
    ITALIAN("it", "Italian", "Italiano", Locale.ITALIAN),
    JAPANESE("ja", "Japanese", "日本語", Locale.JAPANESE),
    KOREAN("ko", "Korean", "한국어", Locale.KOREAN),
    DUTCH("nl", "Dutch", "Nederlands", new Locale("nl")),
    POLISH("pl", "Polish", "Polski", new Locale("pl")),
    PORTUGUESE_BRAZIL("pt", "Portuguese (Brazil)", "Português (Brasil)", new Locale("pt", "BR")),
    PORTUGUESE_PORTUGAL("pt-pt", "Portuguese (Portugal)", "Português (Portugal)", new Locale("pt", "PT")),
    RUSSIAN("ru", "Russian", "Русский", new Locale("ru")),
    SWEDISH("sv", "Swedish", "Svenska", new Locale("sv")),
    TURKISH("tr", "Turkish", "Türkçe", new Locale("tr")),
    UKRAINIAN("uk", "Ukrainian", "Українська", new Locale("uk")),
    VIETNAMESE("vi", "Vietnamese", "Tiếng Việt", new Locale("vi")),
    CHINESE("zh", "Chinese", "简体中文", Locale.SIMPLIFIED_CHINESE),
    CHINESE_TRADITIONAL("zh-hant", "Chinese (Traditional)", "繁體中文", Locale.TRADITIONAL_CHINESE);

    private final String code;
    private final String englishName;
    private final String nativeName;
    private final Locale locale;

    Languages(String code, String englishName, String nativeName, Locale locale) {
        this.code = code;
        this.englishName = englishName;
        this.nativeName = nativeName;
        this.locale = locale;
    }

    public String code() {
        return code;
    }

    public String englishName() {
        return englishName;
    }

    public String nativeName() {
        return nativeName;
    }

    public Locale locale() {
        return locale;
    }

    public Languages next() {
        Languages[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static Languages fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return ENGLISH;
        }

        String normalized = normalizeCode(code);
        if ("pt".equals(normalized) || "pt-br".equals(normalized)) {
            return PORTUGUESE_BRAZIL;
        }
        if ("pt-pt".equals(normalized)) {
            return PORTUGUESE_PORTUGAL;
        }
        if ("zh-hant".equals(normalized)) {
            return CHINESE_TRADITIONAL;
        }
        if (normalized.startsWith("zh")) {
            return CHINESE;
        }

        for (Languages language : values()) {
            if (language.code.equals(normalized)) {
                return language;
            }
        }

        return ENGLISH;
    }

    public static Languages matchLocale(Locale locale) {
        if (locale == null) {
            return ENGLISH;
        }

        String language = normalizeCode(locale.getLanguage());
        if ("pt".equals(language)) {
            String country = locale.getCountry();
            if ("PT".equalsIgnoreCase(country)) {
                return PORTUGUESE_PORTUGAL;
            }
            return PORTUGUESE_BRAZIL;
        }
        if ("zh".equals(language) && isTraditionalChinese(locale)) {
            return CHINESE_TRADITIONAL;
        }

        Languages matched = fromCode(language);
        return matched == ENGLISH && !"en".equals(language) ? ENGLISH : matched;
    }

    private static boolean isTraditionalChinese(Locale locale) {
        String script = locale.getScript();
        if ("Hant".equalsIgnoreCase(script)) {
            return true;
        }

        String country = locale.getCountry();
        return "TW".equalsIgnoreCase(country)
                || "HK".equalsIgnoreCase(country)
                || "MO".equalsIgnoreCase(country);
    }

    private static String normalizeCode(String code) {
        String normalized = code.trim().toLowerCase(Locale.ENGLISH).replace('_', '-');
        if ("id".equals(normalized)) {
            return "in";
        }
        return normalized;
    }
}
