/**
 *
 */
package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;

/**
 * @author Cheick Mahady SISSOKO
 * @date 8 nov. 2014 20:37:34
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_POSTS")
public class Post extends Standard {

    /**
     * The author of this post.
     */
    @ManyToOne(cascade = CascadeType.PERSIST)
    public User author;
    
    /**
     * The title of the post
     */
    @Required(message = "required.post.title")
    @Column(name = "TITLE", nullable = false)
    @MaxSize(value = 70, message = "required.post.title.max")
    public String title;

    /**
     * The main content of the post
     */
    @Required(message = "required.post.content")
    @Lob
    @Column(name = "CONTENT")
    public String content;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    public List<PostAttachment> attachments;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    public List<PostComment> comments;

    /**
     *
     */
    public Post() {
        super();
    }

    /**
     * Function returns a short summary of the content of the post
     *
     * @return string
     */
    public String summary() {
        if (content == null) {
            return "";
        }
        return content.length() <= 100 ? content : content.substring(0, 100)
                + "...";
    }

    /**
     * Returns true if the post belongs to the given user
     *
     * @param user
     * @param author
     * @return
     */
    public boolean isMine(User user) {
        if (user == null) {
            return false;
        }
        return user.equals(this.author);
    }

    /**
     * This is a list of the i most recent posts
     *
     * @param i
     * @return
     */
    public static List<Post> recents(int i) {
        List<Post> recents = Post.find("order by createDate desc").fetch(i);
        return recents;
    }
    
    @Override
    public String toString() {
    	return title;
    }
	
	@Override
	public Association createAssociation(Attachable attachable) {
		if(attachable instanceof Attachment) {
			Long at_id = ((Attachment)attachable).id;
			if(at_id == null) {
				return null;
			}
			PostAttachment old = PostAttachment.findById(new PostAttachmentId(id, at_id));
			if(old != null) {
				return old;
			}
			PostAttachment at = new PostAttachment(id, at_id);
			at.save();
			return at;
		}
		if(attachable instanceof Comment) {
			Long at_id = ((Comment)attachable).id;
			if(at_id == null) {
				return null;
			}
			PostComment old = PostComment.findById(new PostCommentId(id, at_id));
			if(old != null) {
				return old;
			}
			PostComment at = new PostComment(id, at_id);
			at.save();
			return at;
		}
		return null;
	}

}
