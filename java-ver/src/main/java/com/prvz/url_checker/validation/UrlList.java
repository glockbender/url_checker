package com.prvz.url_checker.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UrlListValidator.class)
public @interface UrlList {

    /**
     * Сообщение
     *
     * @return сообщение
     */
    String message() default "Некорректные URL %s";

    /**
     * Группы валидации
     *
     * @return группы валидации
     */
    Class<?>[] groups() default {};

    /**
     * Дополнительная нагрузка
     *
     * @return дополнительная нагрузка
     */
    Class<? extends Payload>[] payload() default {};

}
