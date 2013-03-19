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

    /** The enum type. */
    private final Class<E> enumType;

    /** The Enum to Long map. */
    private final Map<E, Long> mapEnumToLong;

    /** The Long to Enum map. */
    private final Map<Long, E> mapLongToEnum;

    /** Constructor takes the enum type. */
    public EnumLongConverter(final Class<E> theEnumType) {
        if (theEnumType == null) {
            throw new IllegalArgumentException(theEnumType + " is null");
        }
        if (!theEnumType.isEnum()) {
            throw new IllegalArgumentException(theEnumType + " is not an Enum");
        }
        enumType = theEnumType;
        final E[] constants = theEnumType.getEnumConstants();
        final long[] longs = new long[constants.length];
        mapLongToEnum = new HashMap<>();
        mapEnumToLong = new HashMap<>();
        for (int i = 0; i < constants.length; i++) {
            final E e = constants[i];
            @SuppressWarnings("boxing")
            final Long l = longs[i] = Base40.toLong(e.name());
            mapLongToEnum.put(l, e);
            mapEnumToLong.put(e, l);
        }
        Arrays.sort(longs);
        long last = longs[0];
        for (int i = 1; i < longs.length; i++) {
            final long l = longs[i];
            if (l == last) {
                throw new IllegalArgumentException(
                        theEnumType
                                + " has multiple constants that map to the same base40 values");
            }
            last = l;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("boxing")
    @Override
    public long fromObject(final E obj) {
        return mapEnumToLong.get(obj);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("boxing")
    @Override
    public E toObject(final long value) {
        return mapLongToEnum.get(value);
    }

    /** {@inheritDoc} */
    @Override
    public Class<E> type() {
        return enumType;
    }
}
