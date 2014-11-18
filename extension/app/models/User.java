/**
 * 
 */
package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.GenericModel;
import utils.Utils;

/**
 * Un utilisateur est représentation par son username et son email.<br>
 * On ne demanda pas de nom et de prénom afin d'assurer les utilisateurs de
 * l'anonymat.
 * 
 * @author Cheick Mahady SISSOKO
 * @date 30 oct. 2014 21:38:23
 * 
 * @since djammadev v1
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_USERS")
public class User extends Standard {

	/**
	 * The user's first name
	 */
	@Required(message = "required.user.firstName")
	@Column(name = "FIRST_NAME")
	public String firstName;

	/**
	 * The user's last name
	 */
	@Required(message = "Le nom est obligatoire.")
	@Column(name = "LAST_NAME")
	public String lastName;

	/**
	 * The user's username
	 */
	@Unique(message = "unique.user.username")
	@Required(message = "required.user.username")
	@MinSize(value = 6, message = "min.user.username")
	@Column(name = "USER_NAME", unique = true, nullable = false, updatable = true)
	public String username;

	/**
	 * The user's email
	 */
	@Unique(message = "unique.user.email")
	@Required(message = "required.user.email")
	@Email(message = "valid.user.email")
	@Column(name = "EMAIL", unique = true, nullable = false, updatable = true)
	public String email;

	/**
	 * Count the number off user connexion.
	 */
	@Column(name = "CONNEXION")
	public Integer connexion;

	/**
	 * Save the last timestamp connexion.
	 */
	@Column(name = "LAST_CONNEXION")
	public Date lastConnexion;

	@Column(name = "PROFILE")
	public String profile;

	/**
	 * The user's password
	 */
	@Password
	@Required(message = "required.user.password")
	@Column(name = "PASSWORD")
	@MinSize(value = 6, message = "min.user.password")
	public String password;

	/**
	 * passwordToken is generate when a author lost his password or if he wants
	 * to change it. this token is deleted when the author update his password
	 */
	@Column(name = "PWDT")
	public String passwordToken;

	/**
	 * Quand l'utilisateur oublie ou veut changer son mot de passe on génère un
	 * token aléatoire utilisation qu'une seule fois. <br>
	 * Il est remis à <code>null</code> une fois que l'utilisateur met à jour
	 * son mdp
	 */
	@Column(name = "CONFIRM")
	public String confirmToken;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
	public List<Attachment> attachments;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
	public List<Comment> comments;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "from")
	public List<Message> fromMessages;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "to")
	public List<Message> toMessages;
	/**
	 * 
	 */
	public User() {
		super();
		profile = "user";
	}

	/**
	 * Find the user by his username or his email.
	 * 
	 * @param username
	 * @return
	 */
	public static User findByUsername(String username) {
		return User.find("username = ?1 or email = ?1", username).first();
	}

	public String fullName() {
		String name = username;
		if(firstName != null && !firstName.isEmpty()) {
			name = firstName;
		}
		if(lastName != null && !lastName.isEmpty()) {
			if(firstName != null && !firstName.isEmpty()) {
				name += " " + lastName;
			} else {
				name = lastName;
			}
		}
		return name;
	}
	
	@Override
	public String toString() {
		return fullName();
	}

	public void connexion() {
		if(connexion == null) {
			connexion = 0;
		}
		connexion += 1;
		lastConnexion = new Date();
		save();
	}

	public static User userSender(String email) {
		User user = User.findByUsername(email);
		if(user == null) {
			user = new User();
			user.username = email;
			user.firstName = Utils.APPLICATION_NAME;
			if("noreplay".equalsIgnoreCase(email)) {
				user.email = Utils.NOREPLAY_EMAIL_ADDRESS;
			} else {
				user.email = Utils.ADMIN_EMAIL_ADDRESS;
			}
			user.save();
		}
		return user;
	}

	public boolean isAdmin() {
		return "admin".equals(profile);
	}

	public boolean canEdit(GenericModel object) {
		if(object == null) {
			return false;
		}
		if(isAdmin()) {
			return true;
		}
		if(object instanceof User) {
			return equals(object);
		}
		if(object instanceof Message) {
			return equals(((Message) object).from);
		}
		if(object instanceof Post) {
			return equals(((Post) object).author);
		}
		if(object instanceof Attachment) {
			return equals(((Attachment) object).author);
		}
		if(object instanceof Comment) {
			return equals(((Comment) object).author);
		}
		return false;
	}
}
