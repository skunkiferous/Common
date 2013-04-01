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

    /** Constructor takes the enum type. */
    public EnumByteConverter(final Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException(enumType + " is null");
        }
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException(enumType + " is not an Enum");
        }
        constants = enumType.getEnumConstants();
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
}
