package br.com.oldtown.pharma.shared.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public static LocalDateTime parseToLocalDateTime(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(date, DEFAULT_FORMATTER);
    }

    public static boolean isEndDateAfterStartDate(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate != null && endDate != null && endDate.isAfter(startDate);
    }

}
