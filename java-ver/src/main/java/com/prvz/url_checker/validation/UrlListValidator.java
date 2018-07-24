package com.prvz.url_checker.validation;

import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

import static com.prvz.url_checker.util.Utils.isUrl;
import static java.lang.String.format;

public class UrlListValidator implements ConstraintValidator<UrlList, List<?>> {

    @Override
    public boolean isValid(List<?> value, ConstraintValidatorContext context) {

        if (CollectionUtils.isEmpty(value)) {
            return false;
        }
        List<String> incorrect = value.stream()
                .map(Object::toString)
                .filter(url -> !isUrl(url))
                .collect(Collectors.toList());
        if (incorrect.isEmpty()) {
            return true;
        }
        context.buildConstraintViolationWithTemplate(
                format(context.getDefaultConstraintMessageTemplate(),
                        String.join(",\n", incorrect)))
                .addConstraintViolation();
        return false;
    }
}
