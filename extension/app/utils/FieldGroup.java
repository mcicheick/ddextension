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
 * @date 9 sept. 2014 23:21:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(checkWith = FieldGroupCheck.class)
public @interface FieldGroup {
	String message() default FieldGroupCheck.mes;
}
