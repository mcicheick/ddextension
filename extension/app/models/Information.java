/**
 * 
 */
package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPABase;

/**
 * Une information peut être une information générale <br>
 * Ou une information particulière comme par exemple : <br>
 * <ul>
 * <li>La description d'un projet</li>
 * <li>La description d'un service qu'on offre</li>
 * <li>La description d'une requête faire par les clients</li>
 * <li>Ou nos travaux déjà réalisés.</li>
 * </ul>
 * 
 * @author Cheick Mahady SISSOKO
 * @date 6 nov. 2014 12:06:22
 */
@Entity
@Table(name = "T_INFORMATIONS")
public class Information extends Standard {

	/**
	 * Le type d'information<br>
	 * <enum>INFORMATION</enum> Une information générale.<br>
	 * <enum>TYPE_INFON_1</enum><br>
	 * <enum>TYPE_INFON_2</enum><br>
	 * <enum>...</enum><br>
	 * <enum>TYPE_INFON_N</enum><br>
	 * 
	 * @author Cheick Mahady SISSOKO
	 * @date 8 nov. 2014 01:39:51
	 */
	public enum InformationType {
		INFORMATION("informations");

		private String templateName;

		/**
		 * Le nom du template utilisé pour afficher l'information.
		 * 
		 * @param templateName
		 */
		private InformationType(String templateName) {
			this.templateName = templateName;
		}

		public String templateName() {
			return templateName;
		}
	}

	@Column(name = "INFORMATION_TYPE")
	@Required(message = "required.information.informationType")
	@Enumerated(EnumType.STRING)
	public InformationType informationType;
	@ManyToOne
	public User author;
	@Column(name = "CONTENT")
	@Required(message = "required.information.content")
	@Lob
	@MaxSize(2048)
	public String content;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "information")
	public List<InformationAttachment> attachments;

	/**
	 * 
	 */
	public Information() {
		super();
		this.informationType = InformationType.INFORMATION;
	}

	/**
	 * 
	 * @param page
	 * @param pageMaxSize
	 * @param informationType
	 * @return
	 */
	public static Page<Information> informations(int page, int pageMaxSize,
			InformationType informationType) {
		if (page <= 0) {
			page = 1;
		}
		GenericModel.JPAQuery query = Information.find(
				"active = ? and informationType = ? order by createDate desc",
				true, informationType);
		int totalRowCount = query.fetch().size();
		int pageIndex = page;
		List<Information> data = query.fetch(pageIndex, pageMaxSize);
		int pageSize = pageMaxSize;
		Page<Information> informations = new Page(data, totalRowCount,
				pageIndex, pageSize);
		return informations;
	}

	@Override
	public <T extends JPABase> T delete() {
		if (attachments != null) {
			for (InformationAttachment att : attachments) {
				att.delete();
			}
		}
		return super.delete();
	}

	@Override
	public Association createAssociation(Attachable attachable) {
		if(attachable instanceof Attachment) {
			Long at_id = ((Attachment)attachable).id;
			if(at_id == null) {
				return null;
			}
			InformationAttachment old = Information.findById(new InformationAttachmentId(id, at_id));
			if(old != null) {
				return old;
			}
			InformationAttachment at = new InformationAttachment(id, at_id);
			at.save();
			return at;
		}
		if(attachable instanceof Comment) {
			Long at_id = ((Comment)attachable).id;
			if(at_id == null) {
				return null;
			}
			InformationComment old = InformationComment.findById(new InformationCommentId(id, at_id));
			if(old != null) {
				return old;
			}
			InformationComment at = new InformationComment(id, at_id);
			at.save();
			return at;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return informationType + "";
	}
}
