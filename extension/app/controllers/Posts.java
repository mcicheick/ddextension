/**
 *
 */
package controllers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;
import models.Attachment;
import models.User;
import models.Post;
import play.mvc.Router;
import models.Page;
import models.PostAttachment;
import models.PostComment;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.db.Model;
import play.db.jpa.GenericModel;
import play.db.jpa.GenericModel.JPAQuery;
import play.exceptions.TemplateNotFoundException;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 22:22:37
 */
public class Posts extends Admin {

    public static int RECENTS_MAX_SIZE = 5;
    public static int MAX_POSTS_PAGES = 5;

    public static void index(int page, String search, String searchFields,
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
        Page<Model> pages = new Page<Model>(objects, totalCount, page, 10);
        try {
            render(type, pages, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/index.html", type, pages, count, totalCount, page,
                    orderBy, order);

        }
   
    }
    
}
