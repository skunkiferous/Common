/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blockwithme.base40;

import com.blockwithme.prim.ConverterRegistry;
import com.blockwithme.prim.LongConverter;

/**
 * <code>Base40Converter</code> converts Base40 to and from Java long.
 */
public class Base40Converter implements LongConverter<Base40> {

    /** The character set. */
    private final CharacterSet characterSet;

    /** Creates a Base40Converter with the given Base40 CharacterSet. */
    public Base40Converter(final CharacterSet theCharacterSet) {
        characterSet = theCharacterSet;
    }

    /** Creates a Base40Converter with the Base40 default CharacterSet. */
    public Base40Converter() {
        this(Base40.getDefaultCharacterSet());
    }

    /** Registers this converter globally. */
    public static void register() {
        ConverterRegistry.instance().register(new Base40Converter(),
                Base40.class);
    }

    @Override
    public long fromObject(final Base40 obj) {
        return (obj == null) ? 0L : obj.asLong();
    }

    @Override
    public Base40 toObject(final long value) {
        return new Base40(characterSet, value);
    }
}
