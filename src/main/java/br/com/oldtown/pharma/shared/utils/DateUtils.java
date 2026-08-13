package br.com.oldtown.pharma.shared.utils;

import br.com.oldtown.pharma.shared.exception.ConflictException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static LocalDateTime parseToLocalDateTime(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(date, DEFAULT_FORMATTER);
    }

    public static boolean isEndDateAfterStartDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return false;
        } else if (endDate.isEqual(startDate)) {
            throw new ConflictException("The ends date cannot be equal start date");
        }

        return endDate.isBefore((startDate));
    }

}
