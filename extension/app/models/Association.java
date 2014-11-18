/**
 * 
 */
package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import play.db.jpa.GenericModel;
import play.db.jpa.JPABase;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 20:53:23
 * 
 * @since djammadev v1
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Association extends GenericModel {
	@Column(name = "CREATE_DATE")
	public Date createDate;
	@Column(name = "MODIFY_DATE")
	public Date modifyDate;
	/**
	 * {@link Association#active} indicates the state of models.
	 */
	@Column(name = "ACTIVE")
	public Boolean active;

	/**
	 * 
	 */
	public Association() {
		createDate = new Date();
		modifyDate = new Date();
		active = true;
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
