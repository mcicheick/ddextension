/**
 * 
 */
package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Cheick Mahady Sissoko
 * @date 26 sept. 2014 20:36:10
 */
@SuppressWarnings("serial")
public class FieldAttachedCheck extends AbstractAnnotationCheck<FieldAttached> {

	public final static String mes = "required.content";

	@Override
	public boolean isSatisfied(Object validatedObject, Object value,
			OValContext context, Validator validator) throws OValException {
		Field[] fields = validatedObject.getClass().getFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(FieldAttached.class)) {
				try {
					Object v = f.get(validatedObject);
					if (v != null) {
						for (Field ff : fields) {
							if (ff.isAnnotationPresent(FieldAttached.For.class)) {
								FieldAttached.For a = ff
										.getAnnotation(FieldAttached.For.class);
								if (a.value().equals(f.getName())) {
									Object u = ff.get(validatedObject);
									if(u == null) {
										return false;
									}
									return (Boolean) v
											.getClass()
											.getMethod("contains", u.getClass())
											.invoke(v, u);
								}
							}
						}
					}
					return true;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

}
