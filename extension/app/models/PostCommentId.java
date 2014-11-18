/**
 * 
 */
package models;

import java.io.Serializable;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 21:07:38
 */
@SuppressWarnings("serial")
public class PostCommentId implements Serializable {

	final long post_id;
	final long comment_id;

	/**
	 * @param post_id
	 * @param comment_id
	 */
	public PostCommentId(long post_id, long comment_id) {
		this.post_id = post_id;
		this.comment_id = comment_id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (comment_id ^ (comment_id >>> 32));
		result = prime * result + (int) (post_id ^ (post_id >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostCommentId other = (PostCommentId) obj;
		if (comment_id != other.comment_id)
			return false;
		if (post_id != other.post_id)
			return false;
		return true;
	}

}
