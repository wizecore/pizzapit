package com.wizecore.pizzapit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark as value available to be setup.
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Setup {
	/**
	 * ENV_VAR:default value
	 * @return
	 */
	String value() default "";
}
