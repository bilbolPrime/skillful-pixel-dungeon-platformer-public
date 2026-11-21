package com.bilboldev.skillfulpixeldungeonplatformer.messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageFormats {

    public static final Pattern TOKEN = Pattern.compile(
            "%(?:(\\d+)\\$)?([-#+ 0,(<]*)(\\d*)(?:\\.(\\d+))?([tT][a-zA-Z]|[bBhHsScCdoxXeEfgGaA%n])");

    private MessageFormats() { }

    public static boolean isToken(Matcher token) {
        String flags = token.group(2);
        return token.group(3).length() > 0 || (flags.indexOf('-') < 0 && flags.indexOf(' ') < 0);
    }

    public static boolean consumesArgument(Matcher token) {
        return isToken(token) && !token.group(5).equals("%") && !token.group(5).equals("n");
    }

    public static int argumentIndex(Matcher token, int next, int previous) {
        if (token.group(2).indexOf('<') >= 0) return previous;
        return token.group(1) == null ? next : Integer.parseInt(token.group(1)) - 1;
    }

    public static boolean isImplicit(Matcher token) {
        return token.group(1) == null && token.group(2).indexOf('<') < 0;
    }

    public static Map<Integer, Set<String>> signature(String text) {
        Map<Integer, Set<String>> result = new HashMap<>();
        Matcher tokens = TOKEN.matcher(text);
        int next = 0, previous = -1;
        while (tokens.find()) {
            if (!consumesArgument(tokens)) continue;
            int index = argumentIndex(tokens, next, previous);
            if (isImplicit(tokens)) next++;
            previous = index;
            Set<String> conversions = result.get(index);
            if (conversions == null) result.put(index, conversions = new HashSet<>());
            conversions.add(tokens.group(5).toLowerCase(java.util.Locale.ROOT));
        }
        return result;
    }

    public static boolean compatible(String source, String translated) {
        return signature(source).equals(signature(translated));
    }

    public static String literals(String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '%' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == '%' || next == 'n') {
                    result.append(next == '%' ? '%' : '\n');
                    i++;
                    continue;
                }
            }
            result.append(current);
        }
        return result.toString();
    }
}
