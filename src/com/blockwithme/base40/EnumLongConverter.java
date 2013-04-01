package com.blockwithme.base40;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.blockwithme.prim.LongConverter;

/**
 * <code>EnumLongConverter</code> implements a LongConverter for some enum type.
 * It is assumed that all enum constant names can be converted to unique Base40 long.
 *
 * @author monster
 *
 * @param <E>
 */
public class EnumLongConverter<E extends Enum<E>> implements LongConverter<E> {

    /** The Long to Enum map. */
    private final Map<Long, E> mapLongToEnum;

    /** The Enum to Long map. */
    private final Map<E, Long> mapEnumToLong;

    /** The character set. */
    private final CharacterSet characterSet = Enum40.getDefaultCharacterSet();

    /** Constructor takes the enum type. */
    public EnumLongConverter(final Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException(enumType + " is null");
        }
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException(enumType + " is not an Enum");
        }
        final E[] constants = enumType.getEnumConstants();
        final long[] longs = new long[constants.length];
        mapLongToEnum = new HashMap<>();
        mapEnumToLong = new HashMap<>();
        for (int i = 0; i < constants.length; i++) {
            final E e = constants[i];
            final Long l = longs[i] = characterSet.toLong(e.name());
            mapLongToEnum.put(l, e);
            mapEnumToLong.put(e, l);
        }
        Arrays.sort(longs);
        long last = longs[0];
        for (int i = 1; i < longs.length; i++) {
            final long l = longs[i];
            if (l == last) {
                throw new IllegalArgumentException(
                        enumType
                                + " has multiple constants that map to the same base40 values");
            }
            last = l;
        }
    }

    @Override
    public long fromObject(final E obj) {
        return mapEnumToLong.get(obj);
    }

    @Override
    public E toObject(final long value) {
        return mapLongToEnum.get(value);
    }
}
