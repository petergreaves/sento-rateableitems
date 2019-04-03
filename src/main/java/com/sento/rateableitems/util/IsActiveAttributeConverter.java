package com.sento.rateableitems.util;

import javax.persistence.AttributeConverter;

public class IsActiveAttributeConverter implements AttributeConverter<Boolean, String> {

    public Boolean convertToEntityAttribute(String str) {

        boolean result = true;

        if (str == null) {
            result = false;
        } else if (str.equalsIgnoreCase("Y")) {

            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public String convertToDatabaseColumn(Boolean val) {
        return (val == true ? "Y" : "N");
    }

}
