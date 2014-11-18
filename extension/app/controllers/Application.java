package controllers;

import play.*;
import play.i18n.Lang;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

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

    public static void index() {
        render();
    }

}