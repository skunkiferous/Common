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

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

/**
 * NanoClock is a system clock with nano precision.
 *
 * It delegates to CurrentTimeNanos, and so should return good time values,
 * even if the local clock is wrong.
 *
 * @author monster
 */
public class NanoClock extends Clock {
    private final ZoneId zone;

    /** @see org.threeten.bp.Clock#systemUTC() */
    public static Clock systemUTC() {
        return new NanoClock(ZoneOffset.UTC);
    }

    /** @see org.threeten.bp.Clock#systemDefaultZone() */
    public static Clock systemDefaultZone() {
        return new NanoClock(ZoneId.systemDefault());
    }

    private NanoClock(final ZoneId zone) {
        this.zone = zone;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(final ZoneId _zone) {
        if (_zone.equals(zone)) {
            return this;
        }
        return new NanoClock(_zone);
    }

    @Override
    public long millis() {
        return CurrentTimeNanos.safeCurrentTimeNanos() / 1000000L;
    }

    @Override
    public Instant instant() {
        return instant(CurrentTimeNanos.safeCurrentTimeNanos());
    }

    public static Instant instant(final long currentTimeNanos) {
        final long epochSecond = currentTimeNanos / 1000000000L;
        final long nanoAdjustment = currentTimeNanos - epochSecond
                * 1000000000L;
        return Instant.ofEpochSecond(epochSecond, nanoAdjustment);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof NanoClock) {
            return zone.equals(((NanoClock) obj).zone);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return zone.hashCode() + 1;
    }

    @Override
    public String toString() {
        return "NanoClock[" + zone + "]";
    }

    public static void main(final String[] args) {
        // Warmup!
        Clock.systemUTC().instant();
        NanoClock.systemUTC().instant();
        System.out.println("system: " + Clock.systemUTC().instant());
        System.out.println("nano:   " + NanoClock.systemUTC().instant());
    }
}
