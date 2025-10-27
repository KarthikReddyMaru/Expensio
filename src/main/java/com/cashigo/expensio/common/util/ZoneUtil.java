package com.cashigo.expensio.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Component
public class ZoneUtil {

    @Value("${zone.id}")
    private static String zoneId;

    public static ZoneId getZoneId() {
        return ZoneId.of(zoneId);
    }

    public static LocalTime toLocalTime(Instant instant) {
        return instant.atZone(getZoneId()).toLocalTime();
    }

    public static LocalDate toLocalDate(Instant instant) {
        return instant.atZone(getZoneId()).toLocalDate();
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(getZoneId()).toInstant();
    }

    public static Instant getStartOfMonthInstant(Instant instant) {
        return instant
                .atZone(getZoneId())
                .toLocalDate()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay(getZoneId())
                .truncatedTo(ChronoUnit.SECONDS)
                .toInstant();
    }

    public static Instant getEndOfMonthInstant(Instant instant) {
        return instant
                .atZone(getZoneId())
                .toLocalDate()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(getZoneId())
                .truncatedTo(ChronoUnit.SECONDS)
                .toInstant();
    }

    public static Instant getCurrentMonthStartInstant() {
        return getStartOfMonthInstant(Instant.now());
    }

    public static Instant getCurrentMonthEndInstant() {
        return getEndOfMonthInstant(Instant.now());
    }

}
