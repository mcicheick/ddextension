/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import play.db.jpa.JPABase;
import play.db.jpa.Model;
import play.i18n.Messages;

/**
 * This class is the parent of all models. It has all common fields
 * 
 * @author Cheick Mahady SISSOKO
 * @date 30 oct. 2014 12:27:00
 * 
 * 
 * @since djammadev v1
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Standard extends Model {

	@Column(name = "CREATE_DATE")
	public Date createDate;
	@Column(name = "MODIFY_DATE")
	public Date modifyDate;
	/**
	 * {@link Standard#active} indicates the state of models.
	 */
	@Column(name = "ACTIVE")
	public Boolean active;

	public Standard() {
		createDate = new Date();
		modifyDate = new Date();
		active = true;
	}
		
	public <T extends Association> T createAssociation(Attachable attachable) {
		throw new UnsupportedOperationException(Messages.get("createAssociation.message"));
	}

	@Override
	public <T extends JPABase> T save() {
		modifyDate = new Date();
		return super.save();
	}

	public <T extends JPABase> T simpleSave() {
		return super.save();
	}
}
