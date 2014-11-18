/**
 * 
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import models.Message;
import play.Play;
import play.mvc.Util;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

/**
 * @author Cheick Mahady SISSOKO
 * @date 11 nov. 2014 22:47:50
 */
public class Utils {

	public static final String ADMIN_EMAIL_ADDRESS = Play.configuration
			.getProperty("admin.email.address", "admin@surofinkele.com");
	public static final String NOREPLAY_EMAIL_ADDRESS = Play.configuration
			.getProperty("noreplay.email.address", "noreplay@surofinkele.com");
	public static final String APPLICATION_NAME = Play.configuration
			.getProperty("application.name", "Application");

	/**
	 * args doit contenir forcement le Message avec la key=message <br>
	 * et aussi le nom du template à charger.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void loadContent(HashMap<String, Object> args)
			throws Exception {
		String template = (String) args.get("template");
		Message message = (Message) args.get("message");
		VirtualFile vf = VirtualFile.open(new File("app/views/Mails/" + template + ".html"));
		message.content = TemplateLoader.load(vf).render(args);
	}
}
