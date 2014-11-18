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
@IdClass(PostCommentId.class)
public class PostComment extends Association {

	@Id
	@Column(name = "POST_ID")
	public long post_id;
	@Id
	@Column(name = "COMMENT_ID")
	public long comment_id;
	/**
	 * The post to which the comment is attached
	 */
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "post_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Post post;

	/**
	 * The comment attached to
	 */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_id", updatable = false, insertable = false, referencedColumnName = "id")
	public Comment comment;

	/**
	 * 
	 */
	public PostComment() {
		super();
	}

	/**
	 * @param post_id
	 * @param comment_id
	 */
	public PostComment(long post_id, long comment_id) {
		this();
		this.post_id = post_id;
		this.comment_id = comment_id;
	}



	@Override
	public String toString() {
		return "PostAttachment[" + post_id + ", " + comment_id + "]";
	}

}
