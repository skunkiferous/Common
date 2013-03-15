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

import java.math.BigInteger;
import java.util.Arrays;

import com.blockwithme.util.Internalizer;
import com.blockwithme.util.InternalizerImpl;

/**
 * <code>Base40</code> represents a 64bit non-negative base-40 value.
 * The value is expressed as *lower-case*. It covers the whole range of a long,
 * treating it as an unsigned long. Leading zeroes are not part of the String by
 * default, and are ignored when converting a String to a Base40.
 *
 * The character set can be changed, globally, once, by calling
 * <code>Base40CharacterSetLoader.setCharacterSet()</code> BEFORE the
 * first access to Base40.
 *
 * The real benefit comes when the Base40 value is stored internally as a long
 * in your objects, instead of as an instance of Base40. The same applies to
 * serialization.
 *
 * Since all the important functionality is available from public static
 * methods, it is easy to add base-40 functionality to any class.
 *
 * LIMITATION: The textual value can be either a single '0', or a string that
 * does not start with 0. This is because Base40 is not really a String, but
 * rather a number, and like most numbers, the leading zeros are not
 * significant, and therefore omitted.
 *
 * WARNING: Do not change your character set, after you started saving data
 * with it, otherwise the names/IDs you saved with the old character set,
 * will come out different when you read them with the new one. I have no plan
 * to change this in the future, as it would make the serialized footprint
 * much bigger.
 */
public final class Base40 extends AbstractBase40<Base40> {

    /** Lazy character set loader. */
    public static final class Base40CharacterSetLoader {
        /** The lower-case characters */
        public static final String LOWER = "0123456789abcdefghijklmnopqrstuvwxyz";

        /** The upper-case characters */
        public static final String UPPER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        /** The default character set final 4 characters. */
        public static final String LAST_4 = "_-.'";

        /**
         * The "identifier" character set final 4 characters.
         * Note that the second character is 0xAD, not 0x2D (normal minus sign).
         */
        public static final String LAST_4_ID = "_­$¢";

        /** The character set */
        private static char[] CHARACTERS;

        /**
         * Sets the character set; can only be set once!
         * @param lowerCase specifies if lower-case, or upper-case letters will be used.
         * @param last4Characters the last 4 characters, forming the complete base-40 character set.
         * @throws java.lang.NullPointerException if last4Characters is null.
         * @throws java.lang.IllegalArgumentException if we don't like what's in last4Characters ...
         * @throws java.lang.IllegalStateException if the character set was already set.
         * @throws java.lang.IllegalStateException if the character contains Path.SEP.
         */
        public static synchronized void setCharacterSet(
                final boolean lowerCase, final String last4Characters) {
            if (CHARACTERS != null) {
                throw new IllegalStateException(
                        "Base-40 Character Set can only be set once!");
            }
            if (last4Characters == null) {
                throw new NullPointerException("last4Characters");
            }
            if (last4Characters.length() != 4) {
                throw new IllegalArgumentException(
                        "last4Characters.length() must be 4, but is "
                                + last4Characters.length());
            }
            final String chosen = (lowerCase ? LOWER : UPPER);
            for (int i = 0; i < 4; i++) {
                final int c = last4Characters.codePointAt(i);
                if ((c < 0) || (c > 255)) {
                    throw new IllegalArgumentException(
                            "last4Characters characters must be in the range [0,255]");
                }
                if (chosen.indexOf(c) >= 0) {
                    throw new IllegalArgumentException(
                            "last4Characters characters must not be in the selected character set: "
                                    + chosen);
                }
            }
            if (last4Characters.contains(String.valueOf(Path.SEP))) {
                throw new IllegalArgumentException(
                        "last4Characters characters must not contian: "
                                + Path.SEP);
            }
            CHARACTERS = (chosen + last4Characters).toCharArray();
        }

        /** Returns the character set. Defaults to lower-case letters. */
        public static synchronized char[] getCharacterSet() {
            if (CHARACTERS == null) {
                // Using default character set ...
                setCharacterSet(true, LAST_4);
            }
            return CHARACTERS.clone();
        }
    }

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The base-40 radix */
    public static final long RADIX = 40;

    /** The base-40 radix maximal length. */
    public static final int MAX_LEN = 13;

    /** The character set */
    private static final char[] CHARACTERS = Base40CharacterSetLoader
            .getCharacterSet();

    /** Is the character set lower-case? */
    private static final boolean LOWER = (CHARACTERS[10] == 'a');

    /** The character set at 36 */
    private static final char C36 = CHARACTERS[36];

    /** The character set at 37 */
    private static final char C37 = CHARACTERS[37];

    /** The character set at 38 */
    private static final char C38 = CHARACTERS[38];

    /** The character set at 39 */
    private static final char C39 = CHARACTERS[39];

    /** Static cache. */
    private static volatile Internalizer<Base40> INTERN = new InternalizerImpl<>();

    /**
     * Takes a character, and returns the position in CHARACTERS.
     *
     * @param c
     * @return the index, or -1 if not found.
     */
    public static int find(final char c) {
        if (('0' <= c) && (c <= '9')) {
            return (c - '0');
        }
        if (LOWER) {
            if (('a' <= c) && (c <= 'z')) {
                return (c - ('a' - 10));
            }
        } else {
            if (('A' <= c) && (c <= 'Z')) {
                return (c - ('A' - 10));
            }
        }
        if (c == C36) {
            return 36;
        }
        if (c == C37) {
            return 37;
        }
        if (c == C38) {
            return 38;
        }
        if (c == C39) {
            return 39;
        }
        return -1;
    }

    /**
     * Takes a character, and returns the position in CHARACTERS.
     *
     * @param c
     * @return the index.
     * @throws java.lang.IllegalArgumentException if not found
     */
    public static int indexOf(final char c) {
        final int result = find(c);
        if (result == -1) {
            throw new IllegalArgumentException("Invalid character (#"
                    + ((int) c) + ") " + c);
        }
        return result;
    }

    /** Returns the character set */
    public static char[] getCharacterSet() {
        return CHARACTERS.clone();
    }

    /** Returns the Base40 representation of this long. */
    public static Base40 get(final long value) {
        return new Base40(value);
    }

    /** Returns the Base40 representation of this base-40 encoded String
     * (non-case-sensitive). */
    public static Base40 get(final String value) {
        return new Base40(toLong(value));
    }

    /** Returns the long representation of this base-40 encoded String
     * (non-case-sensitive). */
    public static long toLong(final String value) {
        final int len = value.length();
        if (len > MAX_LEN) {
            throw new IllegalArgumentException("Maximum length is: " + MAX_LEN
                    + " value: \"" + value + "\"");
        }
        final String cased = LOWER ? value.toLowerCase() : value.toUpperCase();
        final char[] chars = cased.toCharArray();
        long v = 0;
        for (int i = 0; i < chars.length; i++) {
            v = v * RADIX + indexOf(chars[i]);
        }
        return v;
    }

    /**
     * Returns the long representation of this base-40 encoded String
     * (non-case-sensitive). This function will replace any inappropriate
     * character with "CHARACTERS[36]", and will truncate the input string,
     * if needed.
     */
    public static long toLongLenient(final String value) {
        final String cased = LOWER ? value.toLowerCase() : value.toUpperCase();
        final char[] chars = cased.toCharArray();
        final int len = Math.min(chars.length, MAX_LEN);
        long v = 0;
        for (int i = 0; i < len; i++) {
            int index = find(chars[i]);
            if (index == -1) {
                index = 36;
            }
            v = v * RADIX + index;
        }
        return v;
    }

    /**
     * Returns the base-40 char[] representation of the value, treated as an
     * unsigned long. If fixedSize is true, it will be MAX_LEN character long.
     * If capitalize is true, and the character set is lower-case, then the
     * first character, if it is a letter, and any letter afterward, if it
     * follows a non-letter.
     */
    public static char[] toCharArray(long value, final boolean fixedSize,
            final boolean capitalize) {
        final char[] chars = new char[MAX_LEN];
        int i = MAX_LEN - 1;
        if (value < 0) {
            int index = (int) (value % RADIX);
            if (index < 0) {
                index += RADIX;
            }

            // I don't understand why I need to do this:
            // (Probably I somehow compute the modulo wrong. Any help welcome here!)
            if (index >= 24) {
                index -= 24;
            } else {
                index += 16;
            }

            chars[i--] = CHARACTERS[index];
            // To turn a byte to the integer range, you add 256 (2^8 == 1 << 8),
            // so to do that with longs, you need to add (1 << 64) ...
            // value = (value + (1L << 64))/RADIX;
            // value = ((value + (1L << 64))/4)/(RADIX/4);
            // value = (value/4 + (1L << 64)/4)/(RADIX/4);
            value = ((value >> 2) + (1L << 62L)) / (RADIX / 4L);
        }
        while (value != 0) {
            final int index = (int) (value % RADIX);
            chars[i--] = CHARACTERS[index];
            value /= RADIX;
        }
        int len = MAX_LEN - i - 1;
        if (len == 0) {
            len = 1;
        }
        while (i >= 0) {
            chars[i--] = '0';
        }
        final char[] result;
        if (fixedSize || (MAX_LEN == len)) {
            result = chars;
        } else {
            result = Arrays.copyOfRange(chars, MAX_LEN - len, MAX_LEN);
        }
        if (LOWER && capitalize) {
            boolean up = true;
            for (int j = 0; j < result.length; j++) {
                final char c = result[j];
                if (up && Character.isLowerCase(c)) {
                    result[j] = Character.toUpperCase(c);
                }
                up = !Character.isLetter(c);
            }
        }
        return result;
    }

    /** Returns the base-40 String representation of the value, treated as an
     * unsigned long. If fixedSize is true, it will be MAX_LEN character long. */
    public static String toString(final long value, final boolean fixedSize,
            final boolean capitalize) {
        return new String(toCharArray(value, fixedSize, capitalize));
    }

    /** Returns a BigInteger equivalent to the unsigned version of the long. */
    public static BigInteger toUnsigned(final long value) {
        if (value == 0L) {
            return BigInteger.ZERO;
        }
        if (value > 0L) {
            return new BigInteger(String.valueOf(value));
        }
        return new BigInteger(String.valueOf(value & ~(1L << 63))).setBit(63);
    }

    /** Sets the static cache, if desired. */
    public static void setInternalizer(
            final Internalizer<Base40> theInternalizer) {
        final Internalizer<Base40> old = INTERN;
        INTERN = theInternalizer;
        if ((old != null) && (theInternalizer != null)) {
            for (final Base40 base40 : old) {
                theInternalizer.intern(base40);
            }
        }
    }

    /** Statically cache a Base40 instance. */
    public static Base40 intern(final Base40 base40) {
        final Internalizer<Base40> i = INTERN;
        if (i == null) {
            throw new IllegalStateException("No Internalizer registered");
        }
        return i.intern(base40);
    }

    ///////////////////////
    // Instance methods. //
    ///////////////////////

    /** Constructor. Accepts any value. */
    public Base40(final long value) {
        super(value);
    }

    /** Constructor. Only accepts valid names. */
    public Base40(final String name) {
        super(name);
    }

    /** Statically cache a Base40 instance. */
    public Base40 intern() {
        return intern(this);
    }
}
