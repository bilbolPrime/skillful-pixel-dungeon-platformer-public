package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.bilboldev.skillfulpixeldungeonplatformer.helpers.SaveHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public final class PartyCodec {
    private static final ThreadLocal<Json> JSON = new ThreadLocal<Json>() { @Override protected Json initialValue() { return createJson(); } };
    private static Json json() { return JSON.get(); }
    private static Json createJson() {
        Json json = new Json(JsonWriter.OutputType.json);
        json.setTypeName(null);
        json.setIgnoreUnknownFields(false);
        json.setUsePrototypes(false);
        return json;
    }
    public static String encode(Object value) { return json().toJson(value); }
    public static <T> T decode(Class<T> type, String text) throws IOException {
        if (text == null || text.length() > 65535) throw new IOException("metadata_size");
        boolean quoted = false, escaped = false;
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quoted) {
                if (escaped) escaped = false;
                else if (c == '\\') escaped = true;
                else if (c == '"') quoted = false;
            } else if (c == '"') quoted = true;
            else if ((c == '{' || c == '[') && ++depth > 24) throw new IOException("metadata_depth");
            else if (c == '}' || c == ']') depth--;
        }
        if (depth != 0 || quoted) throw new IOException("metadata_syntax");
        try {
            JsonValue value = new JsonReader().parse(text);
            validate(value, new int[]{0});
            T decoded = json().readValue(type, value);

            if (decoded instanceof PartyReplica.World) ((PartyReplica.World)decoded).keys = integerCounts(value.get("keys"));
            if (decoded instanceof SaveHelper.HeroSaveData) {
                SaveHelper.HeroSaveData hero = (SaveHelper.HeroSaveData)decoded;
                hero.fletchedArrowsPerDepth = integerCounts(value.get("fletchedArrowsPerDepth"));
                hero.activeSkillUsageCounts = integerCounts(value.get("activeSkillUsageCounts"));
            }
            return decoded;
        } catch (RuntimeException invalid) { throw new IOException("metadata", invalid); }
    }
    private static HashMap<Integer, Integer> integerCounts(JsonValue value) throws IOException {
        if (value == null || value.isNull()) return null;
        if (!value.isObject() || value.size > 4096) throw new IOException("metadata_counts");
        HashMap<Integer, Integer> result = new HashMap<>();
        for (JsonValue child = value.child; child != null; child = child.next) {
            int key = Integer.parseInt(child.name);
            if (key < 0 || !Integer.toString(key).equals(child.name) || !child.isLong()
                    || child.asLong() < 0 || child.asLong() > Integer.MAX_VALUE) throw new IOException("metadata_count");
            result.put(key, child.asInt());
        }
        return result;
    }
    private static void validate(JsonValue value, int[] count) throws IOException {
        if (++count[0] > 12000) throw new IOException("metadata_count");
        if (value.isNumber() && (Double.isNaN(value.asDouble()) || Double.isInfinite(value.asDouble()))) throw new IOException("metadata_number");
        if (value.isString() && value.asString().length() > 8192) throw new IOException("metadata_string");
        HashSet<String> names = value.isObject() ? new HashSet<>() : null;
        for (JsonValue child = value.child; child != null; child = child.next) {
            if (names != null && (!names.add(child.name) || child.name.equals("class"))) throw new IOException("metadata_field");
            validate(child, count);
        }
    }
    private PartyCodec() { }
}
