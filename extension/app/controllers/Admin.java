/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;

import models.Attachment;
import models.Comment;
import models.Data;
import models.Page;
import models.Standard;
import models.User;
import play.Play;
import play.data.binding.Binder;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.GenericModel;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Lang;
import play.libs.MimeTypes;
import play.mvc.Before;
import play.mvc.With;

/**
 * @author Cheick Mahady Sissoko
 * @date 20 août 2014 08:52:45
 */
public class Admin extends CRUD {

	// Global Variables for all Controllers
	public static int PHOTO_LIMIT_SIZE = Integer.parseInt(Play.configuration
			.getProperty("photo.limit.size", (3 * 1024) + ""));

	static final Logger log = Logger.getGlobal();

	@Before
	public static void setup() {
		flash.put("url", "GET".equals(request.method) ? request.url
				: Play.ctxPath + "/"); // seems a good default
		String locale = params.get("locale");
		if (locale != null) {
			Lang.change(locale);
		}
		if (renderArgs.get("connected") == null) {
			User user = Security.getSessionUser();
			if (user != null) {
				renderArgs.put("connected", user);
			}
		}
	}

	/**
	 * check and find the connected user.
	 *
	 * @return
	 */
	public static User connected() {
		if (renderArgs.get("user") != null) {
			return renderArgs.get("user", User.class);
		}
		return Security.getSessionUser();
	}

	// CRUD ZONE

	@Check("admin")
	public static void list(int page, String search, String searchFields,
			String orderBy, String order) {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		if (page < 1) {
			page = 1;
		}
		List<Model> objects = type.findPage(page, search, searchFields,
				orderBy, order, (String) request.args.get("where"));
		Long count = type.count(search, searchFields,
				(String) request.args.get("where"));
		Long totalCount = type.count(null, null,
				(String) request.args.get("where"));
		try {
			render(type, objects, count, totalCount, page, orderBy, order);
		} catch (TemplateNotFoundException e) {
			render("CRUD/list.html", type, objects, count, totalCount, page,
					orderBy, order);
		}
	}

	@Check("admin")
	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
	}

	@Check("admin")
	@SuppressWarnings("deprecation")
	public static void attachment(String id, String field) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		Object att = object.getClass().getField(field).get(object);
		if (att instanceof Model.BinaryField) {
			Model.BinaryField attachment = (Model.BinaryField) att;
			if (attachment == null || !attachment.exists()) {
				notFound();
			}
			response.contentType = attachment.type();
			renderBinary(attachment.get(), attachment.length());
		}
		// DEPRECATED
		if (att instanceof play.db.jpa.FileAttachment) {
			play.db.jpa.FileAttachment attachment = (play.db.jpa.FileAttachment) att;
			if (attachment == null || !attachment.exists()) {
				notFound();
			}
			renderBinary(attachment.get(), attachment.filename);
		}
		notFound();
	}

	@Check("admin")
	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		object.save();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	@Check("admin")
	public static void blank() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("CRUD/blank.html", type, object);
		}
	}

	@Check("admin")
	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Model object = (Model) constructor.newInstance();
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
		object._save();
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	@Check("admin")
	public static void delete(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		try {
			object.delete();
		} catch (Exception e) {
			flash.error(play.i18n.Messages.get("crud.delete.error",
					type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(play.i18n.Messages.get("crud.deleted", type.modelName));
		redirect(request.controller + ".list");
	}

	static void checkAccess(Standard object) {
		User connected = Security.getSessionUser();
		if (connected == null) {
			try {
				Secure.login();
			} catch (Throwable e) {
				forbidden();
			}
		}
		if (!connected.isAdmin() && !connected.canEdit(object)) {
			forbidden();
		}
	}

	/**
	 * 
	 * @param id
	 * @param attachments
	 * @throws Exception
	 */
	public static void uploadFile(@Required String id,
			@Required File attachments[]) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		validation.valid(object);
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			render("@show", object, attachments);
		}
		String errors = "Not created : ";
		int ecount = 0;
		for (File file : attachments) {
			Attachment attachment = new Attachment();
			attachment.attachmentType = request.controller.toUpperCase();
			attachment.title = file.getName();
			attachment.author = Security.getSessionUser();
			attachment.set(new FileInputStream(file), MimeTypes
					.getContentType(file.getName()), Boolean
					.valueOf(Play.configuration.getProperty(
							"data.remote.store", "false")),
					attachment.attachmentType + "/" + id + "/");
			attachment.save();
			if (object.createAssociation(attachment) == null) {
				attachment.delete();
				errors += attachment.title + ", ";
				ecount++;
			}
		}
		if (ecount > 0) {
			flash.error(play.i18n.Messages.get("errors", errors));
		}
		render("@show", object, attachments);
	}

	/**
	 * 
	 * @param id
	 * @param attachments
	 * @throws Exception
	 */
	public static void postComment(@Required String id,
			@Required(message = "required.comment.content") String comment)
			throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Standard object = (Standard) type.findById(id);
		notFoundIfNull(object);
		validation.valid(object);
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			render("@show", object, comment);
		}
		Comment cmt = new Comment();
		cmt.author = Security.getSessionUser();
		cmt.content = comment;
		cmt.save();
		if (object.createAssociation(cmt) == null) {
			cmt.delete();
			flash.error(play.i18n.Messages.get("errors"));
		}
		render("@show", object, comment);
	}
}
