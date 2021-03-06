/*******************************************************************************
 * Copyright 2013 Sebastien Diot
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.blockwithme.prim;

/**
 * <code>EnumByteConverter</code> implements a ByteConverter for some enum type.
 * It is assumed that there are no more then 256 values for this enum.
 *
 * @author monster
 *
 * @param <E>
 */
public class EnumByteConverter<E extends Enum<E>> extends
        ClassConfiguredConverter<E, E> implements ByteConverter<E> {

    /** The Enum constants. */
    private final E[] constants;

    /** Initialize */
    private E[] init() {
        if (!type.isEnum()) {
            throw new IllegalArgumentException(type + " is not an Enum");
        }
        final E[] result = type.getEnumConstants();
        if (result.length > 256) {
            throw new IllegalArgumentException(type + " has too many constants");
        }
        return result;
    }

    /** Constructor takes the enum type. */
    public EnumByteConverter(final Class<E> theEnumType) {
        super(theEnumType);
        constants = init();
    }

    /** Constructor takes the enum type name. */
    public EnumByteConverter(final String theEnumType) {
        super(theEnumType);
        constants = init();
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

    /* (non-Javadoc)
     * @see com.blockwithme.prim.Converter#bits()
     */
    @Override
    public int bits() {
        return 8;
    }
}
