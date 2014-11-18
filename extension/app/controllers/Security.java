/**
 * 
 */
package controllers;

import java.util.logging.Logger;

import models.User;
import services.BCrypt;

/**
 * @author Cheick Mahady Sissoko
 * @date 7 sept. 2014 22:52:59
 */
public class Security extends Secure.Security {

	static final Logger log = Logger.getGlobal();
	static final BCrypt bcrypt = new BCrypt();

	static User getSessionUser() {
		String username = connected();
		if(username == null){
			return null;
		}
		User user = User.findByUsername(username);
		return user;
	}

	/**
	 * Authenticate with username or email.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	static boolean authenticate(String username, String password) {
		User user = User.findByUsername(username);
		if (user == null) {
			return false;
		}
		return bcrypt.checkpw(password, user.password);
	}

	/**
	 * This method checks that a profile is allowed to view this page/method.
	 * This method is called prior to the method's controller annotated with the @Check
	 * method.
	 *
	 * @param profile
	 * @return true if you are allowed to execute this controller method.
	 */
	static boolean check(String profile) {
		User user = getSessionUser();
		if (user == null) {
			return false;
		}
		if(profile != null) {
			return profile.equalsIgnoreCase(user.profile);
		}
		return false;
	}

	static void onAuthenticated() {
		User user = getSessionUser();
		if (user != null) {
			user.connexion();
			log.info("User[" + user.id + "][" + user + "]: Authentification");
		}
	}

	/**
	 * This method is called before a user tries to sign off. You need to
	 * override this method if you wish to perform specific actions (eg. Record
	 * the name of the user who signed off)
	 */
	static void onDisconnect() {
		User user = getSessionUser();
		if (user != null)
			log.info("User[" + user.id + "][" + user + "]: Disconnect");
	}
}
