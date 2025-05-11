package com.kamilbaranowski.taskmanager.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

public class BusinessDayCalculator {
    private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    /**
     * Dodaje określoną liczbę dni roboczych do podanej daty
     * @param startDate data początkowa
     * @param businessDays liczba dni roboczych do dodania
     * @return data po dodaniu dni roboczych
     */
    public static LocalDateTime addBusinessDays(LocalDateTime startDate, int businessDays) {
        if (businessDays < 0) {
            throw new IllegalArgumentException("Number of business days cannot be negative");
        }

        LocalDateTime result = startDate;
        int addedDays = 0;

        while (addedDays < businessDays) {
            result = result.plusDays(1);
            if (!isWeekend(result)) {
                addedDays++;
            }
        }

        return result;
    }

    /**
     * Sprawdza czy data przypada na weekend
     */
    private static boolean isWeekend(LocalDateTime date) {
        return WEEKEND.contains(date.getDayOfWeek());
    }

    /**
     * Dodaje dni robocze z uwzględnieniem niestandardowych dni wolnych
     * @param holidays zbiór niestandardowych dni wolnych (np. święta)
     */
    public static LocalDateTime addBusinessDays(LocalDateTime startDate, int businessDays, Set<LocalDateTime> holidays) {
        if (businessDays < 0) {
            throw new IllegalArgumentException("Number of business days cannot be negative");
        }

        LocalDateTime result = startDate;
        int addedDays = 0;

        while (addedDays < businessDays) {
            result = result.plusDays(1);
            if (isBusinessDay(result, holidays)) {
                addedDays++;
            }
        }

        return result;
    }

    /**
     * Sprawdza czy dzień jest dniem roboczym (nie weekend i nie święto)
     */
    private static boolean isBusinessDay(LocalDateTime date, Set<LocalDateTime> holidays) {
        return !isWeekend(date) && !holidays.contains(date.toLocalDate().atStartOfDay());
    }
}
