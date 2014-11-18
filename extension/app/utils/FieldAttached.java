/**
 * 
 */
package utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.oval.configuration.annotation.Constraint;

/**
 * @author Cheick Mahady Sissoko
 * @date 26 sept. 2014 20:35:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Constraint(checkWith = FieldAttachedCheck.class)
public @interface FieldAttached {
	String message() default FieldAttachedCheck.mes;

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD, ElementType.PARAMETER })
	public @interface For {
		String message() default "required.for";

		String value();
	}
}
