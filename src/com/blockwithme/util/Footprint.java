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

package com.blockwithme.util;

/** Helper class to compute the approximate memory footprint of an object. */
public class Footprint {

    /** Are we in 64 bits? */
    public static final boolean JVM_64_BITS = System.getProperty("os.arch").contains("64");

    /** The architecture "word" size. */
	private static final int WORD = JVM_64_BITS ? 8 : 4;

    /** How big is an object without data (approx)? */
    public static final int OBJECT_SIZE = JVM_64_BITS ? 16 : 8;

    /** How big is an empty array (approx)? */
    public static final int ARRAY_SIZE = JVM_64_BITS ? 24 : 12;

    /** How big is an object reference? (We assume compressed pointers in 64 bit) */
    public static final int REFERENCE = 4;

    /** Rounds the footprint to an appropriate multiple of the architecture "word" size. */
    public static int round(final int footprint) {
    	final int rest = footprint % WORD;
    	return (rest == 0) ? footprint : (footprint - rest + WORD);
    }
}
