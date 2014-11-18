/**
 * 
 */
package utils;

import java.lang.reflect.Field;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Cheick Mahady Sissoko
 * @date 9 sept. 2014 23:22:21
 */
@SuppressWarnings("serial")
public class FieldGroupCheck extends AbstractAnnotationCheck<FieldGroup> {

	public final static String mes = "required.group";

	@Override
	public boolean isSatisfied(Object validatedObject, Object value,
			OValContext context, Validator validator) throws OValException {
		int count = 0;
		Field[] fields = validatedObject.getClass().getFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(FieldGroup.class)) {
				try {
					Object v = f.get(validatedObject);
					if (v != null) {
						count++;
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return count == 1;
	}

}
