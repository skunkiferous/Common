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

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class should be used to eliminate all non-primitive static members.
 * It is assumed, that thought some magic, this class will be able to build
 * a quasi-static cache, using whatever mean is enabled by the platform.
 *
 * TODO: Make this somehow compatible with OSGi.
 *
 * @author monster
 */
public class Statics {
    /** The static cache. */
    private static final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

    /** Returns the current value of this key. */
    public static Object get(final String key) {
        if (key == null) {
            return null;
        }
        return cache.get(key);
    }

    /**
     * Compares the currently cached value with the given previous value, and
     * if matching, replace with the new value. Returns the value *after* the
     * operation.
     */
    @SuppressWarnings("unchecked")
    public static <E> E replace(final String key, final E oldValue,
            final E newValue) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (newValue == null) {
            throw new NullPointerException("newValue");
        }
        if (oldValue == null) {
            if (cache.putIfAbsent(key, newValue) == null) {
                return newValue;
            }
        } else {
            if (cache.replace(key, oldValue, newValue)) {
                return newValue;
            }
        }
        return (E) cache.get(key);
    }
}
