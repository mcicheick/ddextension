package notifiers;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;

import models.Message;
import models.User;

import org.apache.commons.mail.EmailAttachment;

import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;

public class Mails extends Mailer {

	/**
	 * 
	 * @param user
	 */
	public static void welcome(User user) {
		setSubject(Messages.get("welcome.message.object", user.fullName()));
		addRecipient(user.email);
		setFrom("Surofinkele <noreplay@surofinkele.com>");
		send(user);
	}

	/**
	 * 
	 * @param user
	 */
	public static void lostPassword(User user) {
		setFrom("Surofinkele <noreplay@surofinkele.com>");
		setSubject(Messages.get("password.reset", user.fullName()));
		addRecipient(user.fullName() + " <" + user.email + ">");
		send(user);
	}

	/**
	 * 
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void sendMail(HashMap<String, Object> args)
			throws MalformedURLException {
		File[] attachments = (File[]) args.get("attachments");
		args.remove("attachments");
		Message message = (Message) args.get("message");
		if (message == null) {
			throw new NullPointerException("Message mustn't be null!");
		}
		if (message != null) {
			if (message.from == null || message.to == null) {
				throw new NullPointerException(
						"Message.from and Message.to mustn't be null");
			}
		}
		setFrom(message.from.fullName() + "<" + message.from.email + ">");
		setSubject(message.subject);
		addRecipient(message.to.fullName() + "<" + message.to.email + ">");
		if (attachments != null) {
			for (File attachment : attachments) {
				if (attachment != null) {
					EmailAttachment attach = new EmailAttachment();
					attach.setPath(attachment.getPath());
					addAttachment(attach);
				}
			}
		}
		send(args);
	}

}