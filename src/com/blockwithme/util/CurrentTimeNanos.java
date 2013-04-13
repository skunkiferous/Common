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

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Helper class, returns the *current time* at nano precision, but the nanos
 * are estimated.
 *
 * This class tries to contact some public NTP time servers, so that it can
 * return a value as close as possible to the real time, even if the local
 * clock is skewed. This happens only once on first access, such that long
 * running JVM will eventually drift away, even with the correction.
 *
 * TODO: This class cannot currently handle the summer/winter time changes
 * done by the OS, *while the JVM is running*.
 *
 * @author monster
 */
public class CurrentTimeNanos {

    /** The NTP time server pool. */
    private static final String[] NTP_POOL = new String[] { "0.pool.ntp.org",
            "1.pool.ntp.org", "2.pool.ntp.org" };

    /** The difference between the local system clock, and the time-servers clock, in MS. */
    private static final long TS_DIFF = computeTSDiff();

    /** The difference between System.currentTimeMillis() and System.nanoTime(). */
    private static final long DIFF = diff() + TS_DIFF * 1000000L;

    /** Time in nanoseconds, at last call. */
    private static final AtomicLong LAST_NANO_TIME = new AtomicLong(
            currentTimeNanos());

    /** Computes the difference between the local system clock, and the time-servers clock, in MS. */
    private static long computeTSDiff() {
        final NTPUDPClient client = new NTPUDPClient();
        // We want to timeout if a response takes longer than 10 seconds
        client.setDefaultTimeout(3000);
        long offsetSum = 0L;
        int offsetCount = 0;
        long bestDelay = Long.MAX_VALUE;
        long bestOffset = Long.MAX_VALUE;
        try {
            client.open();
            for (int i = 0; i < NTP_POOL.length; i++) {
                try {
                    final InetAddress hostAddr = InetAddress
                            .getByName(NTP_POOL[i]);
                    final TimeInfo info = client.getTime(hostAddr);
                    info.computeDetails();
                    final Long offsetValue = info.getOffset();
                    final Long delayValue = info.getDelay();
                    if ((delayValue != null) && (offsetValue != null)) {
                        if (delayValue <= 100L) {
                            offsetSum += offsetValue;
                            offsetCount++;
                        }
                        if (delayValue < bestDelay) {
                            bestDelay = delayValue;
                            bestOffset = offsetValue;
                        }
                    }
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (final SocketException e) {
            e.printStackTrace();
            // NTPUDPClient can't even open at all!?!
        } finally {
            client.close();
        }
        if (offsetCount > 0) {
            return offsetSum / offsetCount;
        }
        // OK, not good result. Any result at all?
        if (bestDelay != Long.MAX_VALUE) {
            return bestOffset;
        }
        // FAIL!
        return 0;
    }

    /** Computes the difference between System.currentTimeMillis() and System.nanoTime(). */
    private static long diff() {
        long sumDiffNS = 0;
        for (int i = 0; i < 10; i++) {
            long bestDurationNS = Long.MAX_VALUE;
            long bestDiffNS = Long.MAX_VALUE;
            int loops = 0;
            while (loops < 100) {
                final long startMS = System.currentTimeMillis();
                final long startNS = System.nanoTime();
                long prevNS = startNS;
                long nextMS = System.currentTimeMillis();
                long nextNS = System.nanoTime();
                while (startMS == nextMS) {
                    prevNS = nextNS;
                    nextMS = System.currentTimeMillis();
                    nextNS = System.nanoTime();
                }
                final long durationNS = (nextNS - prevNS);
                if (durationNS < bestDurationNS) {
                    bestDiffNS = nextMS - nextNS / 1000000L;
                    bestDurationNS = durationNS;
                    if (durationNS < 100) {
                        break;
                    }
                }
                loops++;
            }
            sumDiffNS += bestDiffNS;
        }
        // Times NS to MS conversion factor, divided by number of loops ...
        return sumDiffNS * (1000000L / 10L);
    }

    /**
     * Returns an approximation of the *current time* at nano-seconds scale.
     */
    public static long currentTimeNanos() {
        return System.nanoTime() + DIFF;
    }

    /**
     * Returns an approximation of the *current time* at nano-seconds scale.
     *
     * This methods never go back in time, but might stall for a while.
     * Also, it takes longer to run, reducing the quality of the result.
     *
     * Why would System.nanoTime() go backward, *despite what the Javadoc says*?
     * Because on some multi-core systems, System.nanoTime() values are core-
     * specific, and so some core might drift away from each other, in particular
     * when only some of the core are put in low-power mode.
     */
    public static long safeCurrentTimeNanos() {
        while (true) {
            // TODO Make sure to catch large jumps, and adjust appropriately.
            final long last = LAST_NANO_TIME.get();
            final long now = currentTimeNanos();
            if (now >= last) {
                if (LAST_NANO_TIME.compareAndSet(last, now)) {
                    return now;
                }
            } else {
                // Ouch! System.nanoTime() went backward!
                return last;
            }
        }
    }

    private CurrentTimeNanos() {
        // NOP
    }

    public static void main(final String[] args) {
        final long timeMS = System.currentTimeMillis();
        final long timeNS = timeMS * 1000000L;
        final long nanoTime = currentTimeNanos();
        System.out.println("timeMS:    " + timeMS);
        System.out.println("timeNS:    " + timeNS);
        System.out.println("nanoTime:  " + nanoTime);
        System.out.println("MAX_VALUE: " + Long.MAX_VALUE);
    }
}
