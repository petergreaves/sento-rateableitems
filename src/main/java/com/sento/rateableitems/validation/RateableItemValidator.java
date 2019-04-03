package com.sento.rateableitems.validation;

import com.sento.rateableitems.model.RateableItem;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

@Component
public class RateableItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(RateableItem.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        RateableItem ri = (RateableItem) target;
        Date endDate = ri.getEndDate();
        Date startDate = ri.getStartDate();
        if (null!=endDate & (startDate.compareTo(endDate) > 1)){

            errors.rejectValue("end date", "endDate must be after start date");
        }

    }
}
