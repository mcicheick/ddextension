/**
 * 
 */
package models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 20:57:05
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "AS_POST_COMMENTS")
@IdClass(InformationCommentId.class)
public class InformationComment extends Association {

	@Id
	@Column(name = "POST_ID")
	public long information_id;
	@Id
	@Column(name = "COMMENT_ID")
	public long comment_id;
	/**
	 * The post to which the comment is attached
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "information_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Information post;

	/**
	 * The comment attached to
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Comment comment;

	/**
	 * 
	 */
	public InformationComment() {
		super();
	}

	/**
	 * @param information_id
	 * @param comment_id
	 */
	public InformationComment(long information_id, long comment_id) {
		this();
		this.information_id = information_id;
		this.comment_id = comment_id;
	}



	@Override
	public String toString() {
		return "InformationAttachment[" + information_id + ", " + comment_id + "]";
	}

}
