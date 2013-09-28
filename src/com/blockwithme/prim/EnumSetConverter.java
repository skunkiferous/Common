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

import java.util.EnumSet;

/**
 * <code>EnumSetConverter</code> implements a LongConverter for some enum type.
 * It is assumed that there are no more then 64 values for this enum.
 *
 * @author monster
 *
 * @param <E>
 */
public class EnumSetConverter<E extends Enum<E>> implements
        LongConverter<EnumSet<E>> {

    /** The Enum constants. */
    private final E[] constants;

    /** The enum type. */
    private final Class<E> enumType;

    /** Constructor takes the enum type. */
    public EnumSetConverter(final Class<E> theEnumType) {
        if (theEnumType == null) {
            throw new IllegalArgumentException(theEnumType + " is null");
        }
        if (!theEnumType.isEnum()) {
            throw new IllegalArgumentException(theEnumType + " is not an Enum");
        }
        constants = theEnumType.getEnumConstants();
        enumType = theEnumType;
        if (constants.length > 64) {
            throw new IllegalArgumentException(enumType
                    + " has too many constants");
        }
    }

    @Override
    public long fromObject(final EnumSet<E> theSet) {
        long result = 0;
        if (theSet != null) {
            for (final E e : theSet) {
                // TODO ordinal() is evil, and should never be used for serialization
                result |= 1L << e.ordinal();
            }
        }
        return result;
    }

    @Override
    public final EnumSet<E> toObject(final long theValue) {
        final EnumSet<E> result = EnumSet.noneOf(enumType);
        for (int i = 0; i < constants.length; i++) {
            if ((theValue & 1L << i) != 0) {
                result.add(constants[i]);
            }
        }
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Class<EnumSet<E>> type() {
        return (Class<EnumSet<E>>) EnumSet.noneOf(enumType).getClass();
    }

    /* (non-Javadoc)
     * @see com.blockwithme.prim.Converter#bits()
     */
    @Override
    public int bits() {
        return 64;
    }
}
