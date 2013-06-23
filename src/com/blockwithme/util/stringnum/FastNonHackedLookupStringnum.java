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
package com.blockwithme.util.stringnum;

import java.util.HashMap;

/**
 * A Stringnum with fast lookup of non-hacked String.
 * It uses an HashMap, which means it is much "heavier" then a Stringnum.
 *
 * It is NOT thread-safe!
 *
 * @author monster
 */
public class FastNonHackedLookupStringnum extends Stringnum {

    /** Maps normal Strings to hacked Strings. */
    private final HashMap<String, String> mapping = new HashMap<>();

    /** Finds the hacked String matching the non-hacked input String. */
    @Override
    protected String findHacked(final String str) {
        return mapping.get(str);
    }

    /** Called when a new String is indexed. */
    @Override
    protected void onNewString(final String original, final String hacked) {
        super.onNewString(original, hacked);
        mapping.put(original, hacked);
    }
}
