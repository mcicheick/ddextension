

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.List;
import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

/**
 *
 * @author Djamma Dev
 */
@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() {
        //Check if the db is empty
        //Fixtures.deleteDatabase();
        if (play.Play.mode == Play.Mode.DEV) {
            if (User.count() == 0) {
                Fixtures.loadModels("initialData.yml");
            }
        }
    }
}
