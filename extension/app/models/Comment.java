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
 * @date 8 nov. 2014 21:02:34
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_COMMENTS")
public class Comment extends Standard implements Attachable {
	
    /**
     * The main content of the Article
     */
    @Required(message = "required.comment.content")
    @Lob
    @Column(name = "CONTENT")
    public String content;
	@ManyToOne
	public User author;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="comment")
	public List<PostComment> posts;

	@OneToMany(cascade = CascadeType.ALL, mappedBy="comment")
	public List<InformationComment> informations;
	/**
	 * 
	 */
	public Comment() {
		super();
	}

}
