package com.blockwithme.prim;

/**
 * <code>EnumByteConverter</code> implements a ByteConverter for some enum type.
 * It is assumed that there are no more then 256 values for this enum.
 *
 * @author monster
 *
 * @param <E>
 */
public class EnumByteConverter<E extends Enum<E>> implements ByteConverter<E> {

    /** The Enum constants. */
    private final E[] constants;

    /** The enum type. */
    private final Class<E> enumType;

    /** Constructor takes the enum type. */
    public EnumByteConverter(final Class<E> theEnumType) {
        if (theEnumType == null) {
            throw new IllegalArgumentException(theEnumType + " is null");
        }
        if (!theEnumType.isEnum()) {
            throw new IllegalArgumentException(theEnumType + " is not an Enum");
        }
        constants = theEnumType.getEnumConstants();
        enumType = theEnumType;
        if (constants.length > 256) {
            throw new IllegalArgumentException(enumType
                    + " has too many constants");
        }
    }

    @Override
    public byte fromObject(final E obj) {
        return (byte) obj.ordinal();
    }

    @Override
    public E toObject(final byte value) {
        final int ordinal = value & 0xFF;
        return constants[ordinal];
    }

    /** {@inheritDoc} */
    @Override
    public Class<E> type() {
        return enumType;
    }
}
