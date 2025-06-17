package first.backtest.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AriaStringConverter implements AttributeConverter<String, String> {

    // 저장 시: 평문 → 암호문
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return AriaEncryptor.encrypt(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("암호화 실패", e);
        }
    }

    // 조회 시: 암호문 → 평문
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return AriaEncryptor.decrypt(dbData);
        } catch (Exception e) {
            throw new IllegalStateException("복호화 실패", e);
        }
    }
}