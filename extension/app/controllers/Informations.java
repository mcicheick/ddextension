/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import controllers.CRUD.ObjectType;
import models.Attachment;
import models.Data;
import models.Information;
import models.Information.InformationType;
import models.Page;
import play.data.binding.Binder;
import play.db.Model;
import play.db.jpa.GenericModel;
import play.exceptions.TemplateNotFoundException;
import play.libs.MimeTypes;
import play.mvc.With;

/**
 * 
 * @author Cheick Mahady Sissoko
 * @date 4 oct. 2014 14:46:02
 */
@With(Secure.class)
@CRUD.For(Information.class)
public class Informations extends Admin {
	static int pageMaxSize = 10;

	@Check("admin")
	public static void create() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
		constructor.setAccessible(true);
		Information object = (Information) constructor.newInstance();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		object.save();
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
		redirect(request.controller + ".show", object._key());
	}

	/**
	 * 
	 * @param id
	 */
	public static void information(Long id) {
		notFoundIfNull(id);
		Information information = Information.findById(id);
		notFoundIfNull(information);
		Page<Information> informations = Information.informations(1,
				pageMaxSize, information.informationType);
		render("@" + information.informationType, informations, information);
	}

	/**
	 * 
	 * @param page
	 */
	public static void informations(int page) {
		Page<Information> informations = Information.informations(page,
				pageMaxSize, InformationType.INFORMATION);
		render(informations);
	}

}
