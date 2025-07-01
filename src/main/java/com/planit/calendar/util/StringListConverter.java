package com.planit.calendar.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * counties, types 등의 List를 String으로 변환하여 저장하기 위한 Converter
 * , 문자를 기준으로 구분한다.
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CRITERIA = ",";

    @Override
    public String convertToDatabaseColumn(List<String> value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return String.join(SPLIT_CRITERIA, value);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(dbData.split(SPLIT_CRITERIA))
            .map(String::trim)
            .collect(Collectors.toList());
    }
}
