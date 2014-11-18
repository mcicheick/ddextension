/**
 * 
 */
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;

/**
 * @author Cheick Mahady SISSOKO
 * @date 11 nov. 2014 15:33:41
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "T_MESSAGES")
public class Message extends Standard {
	
	@Column(name = "SUBJECT")
    public String subject;
	//Le contenu du message est obligatoire
    @Required(message = "required.message.content")
    @MaxSize(2000)
    @Lob
    @Column(name = "CONTENT")
    public String content;
	/**
	 * 
	 */
	@Column(name = "TOKEN")
	public String token;
    @ManyToOne
    @Required(message = "required.message.from")
    public User from;
    @ManyToOne
    @Required(message = "required.message.to")
    public User to;
	/**
	 * 
	 */
	public Message() {
		super();
	}

}
