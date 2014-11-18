/**
 * 
 */
package controllers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import models.Message;
import notifiers.Mails;
import play.Play;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.With;

/**
 * 
 * @author Cheick Mahady SISSOKO
 * @date 12 nov. 2014 00:08:26
 */

@With(Secure.class)
public class Messages extends Admin {
	
	@Check("admin")
	public static void create(File attachments[]) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Message object = (Message) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		object.save();
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("message", object);
		args.put("attachments", attachments);
		Mails.sendMail(args);
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}
}
