/**
 * 
 */
package extensions;

import java.util.Collection;

import play.templates.JavaExtensions;

/**
 * @author Cheick Mahady Sissoko
 * @date 1 juin 2014 16:42:39
 */
public class AppExtension extends JavaExtensions {

	/**
	 * 
	 * @param n
	 * @return
	 */
	public static String pluralize(Number n) {
		long l = n.longValue();
		if (l > 1) {
			return "s";
		}
		return "";
	}
	
	/**
	 * 
	 * @param n
	 * @param plural
	 * @return
	 */
	public static String pluralize(Number n, String plural) {
        long l = n.longValue();
        if (l > 1) {
            return plural;
        }
        return "";
    }
	
	/**
	 * 
	 * @param n
	 * @param forms
	 * @return
	 */
	public static String pluralize(Number n, String[] forms) {
        long l = n.longValue();
        if (l != 1) {
            return forms[1];
        }
        return forms[0];
    }

	/**
	 * Tronquage d'un text par une taille donnée.
	 * 
	 * @param text
	 * @param length
	 * @return tronked text.
	 */
	public static String tronk(String text, int length) {
		String tronked = text;
		if (text == null) {
			return text;
		}
		if (text.length() <= length)
			return text;
		tronked = tronked.substring(0, length);
		return tronked + "...";
	}
}
