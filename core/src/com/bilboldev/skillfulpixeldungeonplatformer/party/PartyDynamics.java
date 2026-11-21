package com.bilboldev.skillfulpixeldungeonplatformer.party;

import com.bilboldev.skillfulpixeldungeonplatformer.units.Unit;
import com.bilboldev.skillfulpixeldungeonplatformer.units.buffs.Buff;
import java.lang.reflect.*;
import java.util.*;
import java.io.IOException;

public final class PartyDynamics {
    private static final Set<String> LOCAL = new HashSet<>(Arrays.asList(
            "partyPreviousState", "partyPhysicsRemainder", "x", "y", "floorY", "frameAt", "frameAlpha", "frameSpawning", "stepSound", "soundLevel",
            "lastWaterSplashTileX", "lastWaterSplashTileY",
            "particleAt", "eatIconTimer", "cursedParticleTimer", "lastX", "lastY", "lastTileX", "lastTileY", "rotation", "fluctuation", "yFloat", "floatingUp", "lastObservedIdle", "alertIconTimer", "sleepIconTimer", "championAuraTime"));
    private static final Map<Class<?>, List<Field>> FIELDS = new HashMap<>();
    private static final Map<Class<?>, List<Field>> REFERENCES = new HashMap<>();

    private static List<Field> fields(Class<?> type) {
        List<Field> result = FIELDS.get(type);
        if (result != null) return result;
        result = new ArrayList<>();
        for (Class<?> current = type; current != null && (Unit.class.isAssignableFrom(current) || Buff.class.isAssignableFrom(current) || com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI.class.isAssignableFrom(current)); current = current.getSuperclass()) {
            Field[] declared = current.getDeclaredFields();
            Arrays.sort(declared, (left, right) -> left.getName().compareTo(right.getName()));
            for (Field field : declared) {
                if (Modifier.isStatic(field.getModifiers()) || LOCAL.contains(field.getName())) continue;
                Class<?> fieldType = field.getType();
                if (fieldType == boolean.class || fieldType == int.class || fieldType == long.class
                        || fieldType == float.class || fieldType == double.class || fieldType.isEnum()) {
                    field.setAccessible(true); result.add(field);
                }
            }
        }
        FIELDS.put(type, result);
        return result;
    }

    public static long[] capture(Object object) {
        List<Field> fields = fields(object.getClass());
        long[] values = new long[fields.size()];
        try {
            for (int i = 0; i < values.length; i++) {
                Field field = fields.get(i); Class<?> type = field.getType();
                if (type == boolean.class) values[i] = field.getBoolean(object) ? 1 : 0;
                else if (type == float.class) values[i] = Float.floatToIntBits(field.getFloat(object));
                else if (type == double.class) values[i] = Double.doubleToLongBits(field.getDouble(object));
                else if (type.isEnum()) values[i] = field.get(object) == null ? -1 : ((Enum<?>)field.get(object)).ordinal();
                else values[i] = field.getLong(object);
            }
        } catch (IllegalAccessException invalid) { throw new IllegalStateException(invalid); }
        return values;
    }

    public static void apply(Object object, long[] values) throws IOException {
        List<Field> fields = fields(object.getClass());
        if (values == null || values.length != fields.size()) throw new IOException("state_schema " + object.getClass().getName()
                + " received=" + (values == null ? -1 : values.length) + " expected=" + fields.size());
        try {

            for (int i = 0; i < values.length; i++) {
                Class<?> type = fields.get(i).getType(); long value = values[i];
                if (type == float.class && !finite(Float.intBitsToFloat((int)value))
                        && !(Float.isNaN(Float.intBitsToFloat((int)value)) && Arrays.asList("committedDropEdgeX", "committedDropStartY", "ascentX").contains(fields.get(i).getName()))
                        || type == double.class && !finite(Double.longBitsToDouble(value))
                        || type == int.class && (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE)
                        || type == boolean.class && value != 0 && value != 1
                        || type.isEnum() && (value < -1 || value >= type.getEnumConstants().length)) throw new IOException("state_value");
            }
            for (int i = 0; i < values.length; i++) {
                Field field = fields.get(i); Class<?> type = field.getType(); long value = values[i];
                if (type == boolean.class) field.setBoolean(object, value == 1);
                else if (type == float.class) field.setFloat(object, Float.intBitsToFloat((int)value));
                else if (type == double.class) field.setDouble(object, Double.longBitsToDouble(value));
                else if (type == int.class) field.setInt(object, (int)value);
                else if (type.isEnum()) field.set(object, value == -1 ? null : type.getEnumConstants()[(int)value]);
                else field.setLong(object, value);
            }
        } catch (IllegalAccessException invalid) { throw new IOException("state_field", invalid); }
    }

    public static String[] references(Object object) {
        List<String> values = new ArrayList<>();
        try {
            for (Field field : referenceFields(object.getClass())) {
                Object value = field.get(object);
                values.add(value instanceof Unit ? ((Unit)value).getPersistentId() : (String)value);
            }
        } catch (IllegalAccessException failure) { throw new IllegalStateException(failure); }
        return values.toArray(new String[0]);
    }
    public static void references(Object object, String[] values, Map<String, Unit> units) throws IOException {
        List<Field> fields = referenceFields(object.getClass());
        if (values == null || values.length != fields.size()) throw new IOException("reference_schema");
        try {
            for (int i = 0; i < values.length; i++) {
                Field field = fields.get(i);
                if (values[i] != null && values[i].length() > 160) throw new IOException("reference_size");
                Object value = Unit.class.isAssignableFrom(field.getType()) ? units.get(values[i]) : values[i];
                if (value != null && !field.getType().isInstance(value)) throw new IOException("reference_type");
                field.set(object, value);
            }
        } catch (IllegalAccessException failure) { throw new IOException("reference", failure); }
    }
    private static List<Field> referenceFields(Class<?> type) {
        List<Field> cached = REFERENCES.get(type);
        if (cached != null) return cached;
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && (Unit.class.isAssignableFrom(current) || Buff.class.isAssignableFrom(current) || com.bilboldev.skillfulpixeldungeonplatformer.units.ai.AI.class.isAssignableFrom(current)); current = current.getSuperclass()) {
            Field[] declared = current.getDeclaredFields(); Arrays.sort(declared, (left, right) -> left.getName().compareTo(right.getName()));
            for (Field field : declared) if (!Modifier.isStatic(field.getModifiers())
                    && (Unit.class.isAssignableFrom(field.getType()) || field.getType() == String.class
                    && (field.getName().equals("room") || field.getName().equals("originRoom") || field.getName().equals("pendingMeleeRoom") || field.getName().equals("queuedCombatRoom")))) {
                field.setAccessible(true); fields.add(field);
            }
        }
        REFERENCES.put(type, fields);
        return fields;
    }
    private static boolean finite(double value) { return !Double.isNaN(value) && !Double.isInfinite(value); }
    private PartyDynamics() { }
}
