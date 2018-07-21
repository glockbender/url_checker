package com.prvz.url_checker.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UrlListValidator::class])
annotation class UrlsList(
        val message: String = "{invalid.urls}",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)