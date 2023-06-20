package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link TimeHelper}.
 */
public class TimeHelperTest extends BaseTestCase {

        private static final String DATETIME_DISPLAY_FORMAT = "EEE, dd MMM yyyy, hh:mm a z";

        @Test
        public void testEndOfYearDates() {
                LocalDateTime date = LocalDateTime.of(2015, Month.DECEMBER, 30, 12, 0);
                assertEquals("Wed, 30 Dec 2015, 12:00 NOON UTC", TimeHelper.formatInstant(
                                date.atZone(ZoneId.of("UTC")).toInstant(), "UTC", DATETIME_DISPLAY_FORMAT));
        }

        @Test
        public void testFormatDateTimeForDisplay() {
                String zoneId = "UTC";
                Instant instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 12, 0).atZone(ZoneId.of(zoneId))
                                .toInstant();
                assertEquals("Mon, 30 Nov 2015, 12:00 NOON UTC",
                                TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

                zoneId = "Asia/Singapore";
                instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 16, 0).atZone(ZoneId.of(zoneId)).toInstant();
                assertEquals("Mon, 30 Nov 2015, 04:00 PM SGT",
                                TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));

                instant = LocalDateTime.of(2015, Month.NOVEMBER, 30, 4, 0).atZone(ZoneId.of(zoneId)).toInstant();
                assertEquals("Mon, 30 Nov 2015, 04:00 AM SGT",
                                TimeHelper.formatInstant(instant, zoneId, DATETIME_DISPLAY_FORMAT));
        }

        @Test
        public void testCustomCode() {
                String customZoneId = "PST";
                Instant customInstantAt0000 = LocalDateTime.of(2022, Month.JANUARY, 15, 0, 0)
                                .atZone(ZoneId.of(customZoneId)).toInstant();

                Instant backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt0000,
                                customZoneId, false);
                assertEquals("Fri, 14 Jan 2022, 11:59 PM PST",
                                TimeHelper.formatInstant(backwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                Instant forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt0000,
                                customZoneId, true);
                assertEquals("Sat, 15 Jan 2022, 12:00 AM PST",
                                TimeHelper.formatInstant(forwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                Instant customInstantAt2359 = LocalDateTime.of(2022, Month.JANUARY, 14, 23, 59)
                                .atZone(ZoneId.of(customZoneId))
                                .toInstant();

                backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt2359, customZoneId,
                                false);
                assertEquals("Fri, 14 Jan 2022, 11:59 PM PST",
                                TimeHelper.formatInstant(backwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt2359, customZoneId,
                                true);
                assertEquals("Sat, 15 Jan 2022, 12:00 AM PST",
                                TimeHelper.formatInstant(forwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                String wrongCustomTimeZone = "EST";

                backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt0000,
                                wrongCustomTimeZone, false);
                assertEquals("Sat, 15 Jan 2022, 12:00 AM PST",
                                TimeHelper.formatInstant(backwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt2359,
                                wrongCustomTimeZone, true);
                assertEquals("Fri, 14 Jan 2022, 11:59 PM PST",
                                TimeHelper.formatInstant(forwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                // Teste de entrada invalida pra customZoneId
                backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(customInstantAt0000,
                                "InvalidCustomZoneId", false);
                assertEquals("Fri, 14 Jan 2022, 11:59 PM PST",
                                TimeHelper.formatInstant(backwardAdjusted, customZoneId, DATETIME_DISPLAY_FORMAT));

                // Teste para um timezone e data diferente
                String differentZoneId = "CET";
                Instant differentInstantAt0000 = LocalDateTime.of(2022, Month.JANUARY, 15, 0, 0)
                                .atZone(ZoneId.of(differentZoneId)).toInstant();

                backwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(differentInstantAt0000,
                                differentZoneId, false);
                assertEquals("Fri, 14 Jan 2022, 11:59 PM CET",
                                TimeHelper.formatInstant(backwardAdjusted, differentZoneId, DATETIME_DISPLAY_FORMAT));

                forwardAdjusted = TimeHelper.getMidnightAdjustedInstantBasedOnZone(differentInstantAt0000,
                                differentZoneId, true);
                assertEquals("Sat, 15 Jan 2022, 12:00 AM CET",
                                TimeHelper.formatInstant(forwardAdjusted, differentZoneId, DATETIME_DISPLAY_FORMAT));
        }

        @Test
        public void testGetInstantNearestHourBefore() {
                Instant expected = Instant.parse("2020-12-31T16:00:00Z");
                Instant actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:00:00Z"));

                assertEquals(expected, actual);

                actual = TimeHelper.getInstantNearestHourBefore(Instant.parse("2020-12-31T16:10:00Z"));

                assertEquals(expected, actual);

                actual = TimeHelper.getInstantNearestHourBefore(
                                OffsetDateTime.parse("2021-01-01T00:30:00+08:00").toInstant());

                assertEquals(expected, actual);

                actual = TimeHelper.getInstantNearestHourBefore(
                                OffsetDateTime.parse("2020-12-31T12:59:00-04:00").toInstant());

                assertEquals(expected, actual);
        }

        @Test
        public void testGetInstantDaysOffsetFromNow() {
                // Comparison using second precision is sufficient
                Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Instant actual = TimeHelper.getInstantDaysOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);

                expected = Instant.now().plus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
                actual = TimeHelper.getInstantDaysOffsetFromNow(365).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);
        }

        @Test
        public void testGetInstantDaysOffsetBeforeNow() {
                // Comparison using second precision is sufficient
                Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Instant actual = TimeHelper.getInstantDaysOffsetBeforeNow(0).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);

                expected = Instant.now().minus(Duration.ofDays(365)).truncatedTo(ChronoUnit.SECONDS);
                actual = TimeHelper.getInstantDaysOffsetBeforeNow(365).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);
        }

        @Test
        public void testGetInstantHoursOffsetFromNow() {
                // Comparison using second precision is sufficient
                Instant expected = Instant.now().truncatedTo(ChronoUnit.SECONDS);
                Instant actual = TimeHelper.getInstantHoursOffsetFromNow(0).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);

                expected = Instant.now().plus(Duration.ofHours(60)).truncatedTo(ChronoUnit.SECONDS);
                actual = TimeHelper.getInstantHoursOffsetFromNow(60).truncatedTo(ChronoUnit.SECONDS);
                assertEquals(expected, actual);
        }

        @Test
        public void testFormatInstant() {
                // Test null inputs
                assertEquals("", TimeHelper.formatInstant(null, null, null));

                // Test DateTime with NOON
                LocalDateTime dateTimeWithNoon = LocalDateTime.of(2017, Month.NOVEMBER, 30, 12, 0);
                Instant instantWithNoon = dateTimeWithNoon.atZone(ZoneId.of("GMT")).toInstant();
                assertEquals("Thu, 30 Nov 2017, 12:00 NOON GMT",
                                TimeHelper.formatInstant(instantWithNoon, "GMT", "hh:mm a"));

                // Test DateTime without NOON
                LocalDateTime dateTimeWithoutNoon = LocalDateTime.of(2017, Month.NOVEMBER, 30, 11, 59);
                Instant instantWithoutNoon = dateTimeWithoutNoon.atZone(ZoneId.of("GMT")).toInstant();
                assertEquals("Thu, 30 Nov 2017, 11:59 AM GMT",
                                TimeHelper.formatInstant(instantWithoutNoon, "GMT", "hh:mm a"));
        }
}
