package com.prvz.url_checker.validation

import org.springframework.util.ResourceUtils
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

open class UrlListValidator : ConstraintValidator<UrlsList, List<Any>> {

    private lateinit var urls: List<Any>

    override fun isValid(value: List<Any>?, context: ConstraintValidatorContext?): Boolean {
        return value
                ?.let {
                    if (it.isEmpty()) false
                    else {
                        it.forEach { url ->
                            if (!ResourceUtils.isUrl(url.toString())) {
                                context!!.buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate).apply {
                                    addPropertyNode("urls")
                                    addConstraintViolation()
                                }
                                return false
                            }
                        }
                        true
                    }
                }
                ?: false
    }
}