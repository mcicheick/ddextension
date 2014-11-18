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
@Table(name = "AS_POST_ATTACHMENTS")
@IdClass(PostAttachmentId.class)
public class PostAttachment extends Association {

	@Id
	@Column(name = "POST_ID")
	public long post_id;
	@Id
	@Column(name = "ATTACHMENT_ID")
	public long attachment_id;
	/**
	 * The post to which the attachment is attached
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "post_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Post post;

	/**
	 * The attachment attached to
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Attachment attachment;

	/**
	 * 
	 */
	public PostAttachment() {
		super();
	}

	/**
	 * 
	 */
	public PostAttachment(long post_id, long attachment_id) {
		this();
		this.post_id = post_id;
		this.attachment_id = attachment_id;

	}

	@Override
	public String toString() {
		return "PostAttachment[" + post_id + ", " + attachment_id + "]";
	}

}
