package co.ifwe.versus.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EnumUtils {
    private EnumUtils() {}

    /**
     * <p>Gets the enum for the class, returning defaultValue if not found.</p>
     *
     * @param <E>          the type of the enumeration
     * @param enumClass    the class of the enum to query, not null
     * @param enumName     the enum name, null returns defaultValue
     * @param defaultValue the enum instance to return if enumName is not found
     * @return the enum, defaultValue if not found
     */
    public static <E extends Enum<E>> E getEnum(@NonNull final Class<E> enumClass,
                                                final String enumName, final E defaultValue) {
        if (enumName == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (final IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    /**
     * <p>Gets the enum for the class, returning {@code null} if not found.</p>
     * <p>
     * <p>This method differs from {@link Enum#valueOf} in that it does not throw an exception
     * for an invalid enum name.</p>
     *
     * @param <E>       the type of the enumeration
     * @param enumClass the class of the enum to query, not null
     * @param enumName  the enum name, null returns null
     * @return the enum, null if not found
     */
    public static <E extends Enum<E>> E getEnum(@NonNull final Class<E> enumClass,
                                                final String enumName) {
        return getEnum(enumClass, enumName, null);
    }

    /**
     * <p>Gets the enum constants for the given enum names</p>
     *
     * @param <E>       the type of the enumeration
     * @param enumClass the class of the enum to query, not null
     * @param enumNames the enum names, null returns empty enum set
     * @return set of enums, empty set if no enums found
     */
    public static <E extends Enum<E>> EnumSet<E> getEnums(@NonNull final Class<E> enumClass,
                                                          @Nullable final String[] enumNames) {
        if (enumNames == null || enumNames.length == 0) {
            return EnumSet.noneOf(enumClass);
        }

        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        for (String name : enumNames) {
            E enumInstance = getEnum(enumClass, name);
            if (enumInstance != null) {
                enumSet.add(enumInstance);
            }
        }
        return enumSet;
    }

    /**
     * <p>Gets the names for corresponding given enum constants</p>
     *
     * @param enums the enum constants
     * @param <E>   the type of enumeration
     * @return array of enum names
     */
    public static <E extends Enum<E>> String[] getNames(@Nullable final Collection<E> enums) {
        if (enums == null) {
            return new String[]{};
        }

        Set<String> nameSet = new HashSet<>(enums.size());
        for (E enumInstance : enums) {
            if (enumInstance != null) {
                nameSet.add(enumInstance.name());
            }
        }
        return nameSet.toArray(new String[nameSet.size()]);
    }

    public static <T> T from(String stringValue, T[] values, T defaultValue) {
        if (TextUtils.isEmpty(stringValue)) return defaultValue;
        for (T value : values) {
            if (value.toString().equalsIgnoreCase(stringValue)) return value;
        }

        return defaultValue;
    }

    public static <E extends Enum> List<E> getEnumList(@NonNull final Class<E> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }

    public static <E extends Enum> E getEnumValue(@NonNull final Class<E> enumClass, int ordinal) {
        if (ordinal < enumClass.getEnumConstants().length && ordinal >= 0) {
            return enumClass.getEnumConstants()[ordinal];
        }
        return null;
    }
}
