package com.cornershop.counterstest.utils

import org.mockito.internal.util.reflection.InstanceField

fun <T> getProperty(target: Any, fieldName: String): T {
    val field = target::class.java.getDeclaredField(fieldName)
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(target) as T
}

fun <V, T : Any> setFieldHelper(
    fieldName: String,
    value: V,
    target: T
) {
    InstanceField(target.javaClass.getDeclaredField(fieldName), target).set(value)
}