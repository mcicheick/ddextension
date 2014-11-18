/**
 * 
 */
package controllers;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Message;
import models.Page;
import models.User;
import notifiers.Mails;
import play.Play;
import play.data.binding.Binder;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import services.BCrypt;
import utils.Utils;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 22:22:37
 */
public class Users extends Admin {

	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		User object = (User) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		String confirmPassword = params.get("confirmPassword");
		validation.equals(object.password, confirmPassword)
				.key("confirmPassword").message("match.user.confirmPassword");
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				if ("_signup".equals(params.get("_method"))) {
					render("@signup", type, object);
				}
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		BCrypt bcrypt = new BCrypt();
		object.password = bcrypt.hashpw(object.password, BCrypt.gensalt());
		object.confirmToken = Codec.UUID();
		object.active = false;
		object.profile = "user";
		object.save();
		// ------------- Sending Mail -------------
		HashMap<String, Object> args = new HashMap<String, Object>();
		Message message = new Message();
		message.subject = Messages.get("user.welcome", object.fullName());
		message.from = User.userSender("noreplay");
		message.to = object;
		message.token = object.confirmToken;
		args.put("template", "welcome");
		args.put("message", message);
		args.put("user", object);
		try {
			Utils.loadContent(args);
		} catch (Exception e) {
			e.fillInStackTrace();
			log.info(e.getMessage());
			renderArgs.put("error", play.i18n.Messages.get("email.error"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException ee) {
				render("CRUD/blank.html", type, object);
			}
		}
		try {
			Mails.sendMail(args);
			message.save(); // Keep email trace.
		} catch (MalformedURLException e) {
			e.fillInStackTrace();
			log.info(e.getMessage());
			renderArgs.put("error", play.i18n.Messages.get("email.error"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException ee) {
				render("CRUD/blank.html", type, object);
			}
		}
		// ------------- End Sending Mail -------------
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		User object = (User) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		try {
			render(type, object);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
	}

	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		User object = (User) type.findById(id);
		notFoundIfNull(object);
		checkAccess(object);
		User connected = Security.getSessionUser();
		boolean isAdmin = connected.isAdmin();
		String oldProfile = object.profile;
		String oldPassword = object.password;
		Binder.bindBean(params.getRootParamNode(), "object", object);
		if (!isAdmin) {
			object.profile = oldProfile;
		}
		validation.valid(object);
		boolean updatePassword = !oldPassword.equals(object.password);
		String confirmPassword = null;
		if (updatePassword) {
			confirmPassword = params.get("confirmPassword");
			validation.equals(object.password, confirmPassword)
					.key("confirmPassword")
					.message("match.user.confirmPassword");
		}
		if (Validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		if (updatePassword) {
			BCrypt bcrypt = new BCrypt();
			object.password = bcrypt.hashpw(object.password, BCrypt.gensalt());
		}
		object.save();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	/**
	 * Confirmation du compte de l'utilisateur.
	 * 
	 * @param confirmToken
	 * @throws Exception
	 */
	public static void confirm(@Required String confirmToken) throws Exception {
		if (Validation.hasErrors()) {
			notFound();
		}
		User user = User.find("byConfirmToken", confirmToken).first();
		notFoundIfNull(user);
		user.confirmToken = null;
		user.active = true;
		user.save();
		flash.success(Messages.get("confirm.user", user.firstName));
		show(user.id + "");
	}

	/**
	 * Get
	 */
	public static void forgotPassword() {
		render();
	}

	/**
	 * Post
	 * 
	 * @param pseaudo
	 */
	public static void lostPassword(
			@Required(message = "required.user.pseaudo") String username) {
		if (Validation.hasErrors()) {
			render("@forgotPassword", username);
		}
		User user = User.findByUsername(username);
		if (user == null) {
			flash.error(Messages.get("user.notfound", username));
			render("@forgotPassword", username);
		}
		Message message;
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("template", "lostPassword");
		args.put("user", user);
		user.passwordToken = Codec.UUID();
		message = new Message();
		message.subject = Messages.get("password.reset", user.fullName());
		message.from = User.userSender("noreplay");
		message.to = user;
		args.put("message", message);
		try {
			Utils.loadContent(args);
		} catch (Exception e) {
			log.info(e.getMessage());
			flash.error(Messages.get("email.error"));
			render("@forgotPassword", username);
		}

		try {
			Mails.sendMail(args);
			flash.success(Messages.get("email.success"));
			message.save(); // On garde une trace du mail envoyé.
			user.save(); // Tout s'est bien passé donc on enregistre le token du
							// mot de passe.
			Secure.login();
		} catch (MalformedURLException e) {
			log.info(e.getMessage());
			flash.error(Messages.get("email.error"));
			render("@forgotPassword", username);
		} catch (Throwable e) {
			log.info(e.getMessage());
			flash.error(Messages.get("email.error"));
			render("@forgotPassword", username);
		}
	}

	/**
	 * Il se charge juste de récupérer l'utilisateur et l'envoyer à la vue pour
	 * pouvoir mettre à jour le mot de passe. Get
	 * 
	 * @param passwordToken
	 */
	public static void changePassword(String passwordToken) {
		User user = User.find("byPasswordToken", passwordToken).first();
		if (user == null) {
			flash.error(Messages.get("passwordToken.incorrect"));
			Application.index();
		}
		render(user);
	}

	/**
	 * Met à jour le mot de passe de l'utilisateur. Post
	 * 
	 * @param user
	 * @param verifyPassword
	 */
	public static void updatePassword(
			@Required Long id,
			@Required(message = "required.user.password") String password,
			@Required(message = "required.user.confirmPassword") String confirmPassword) {
		validation.equals(confirmPassword, password).key("confirmPassword").message(
				Messages.get("user.confirmPassword"));
		if (Validation.hasErrors()) {
			flash.error(Messages.get("crud.hasErrors"));
			render("@changePassword", id);
		}
		User user = User.findById(id);
		notFoundIfNull(user);
		BCrypt bcrypt = new BCrypt();
		user.password = bcrypt.hashpw(password, BCrypt.gensalt());
		user.passwordToken = null;
		user.save();
		flash.success(Messages.get("update.user.password", user.firstName));
		String username = user.email;
		render("Secure/login.html", username);
	}

	public static void signup() {
		render();
	}
}
