/**
 * 
 */
package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import play.Play;
import play.db.Model.BinaryField;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.IO;
import services.AWSHelper;

/**
 * @author Cheick Mahady SISSOKO
 * @date 6 nov. 2014 13:41:19
 * 
 * @since djammadev v1
 */
public class Data implements BinaryField, UserType {

	private String UUID;
	private String type;
	private File file;
	/**
	 * Il faut que subdir se termine par "/"
	 */
	private String subdir;
	private Boolean remote;
	InputStream is;
	private String path;
	public Data() {
		remote = false;
		subdir = "";
	}

	/**
	 * 
	 * @param UUID
	 * @param type
	 * @param remote
	 * @param subdir
	 */
	private Data(String UUID, String type, boolean remote, String subdir) {
		this.UUID = UUID;
		this.type = type;
		this.remote = remote;
		this.subdir = subdir;
		this.path = subdir + UUID;
	}

	public InputStream get() {
		if (remote) {
			if (is != null) {
				return is;
			}
			return is = AWSHelper.getStream(path);
		}
		if (exists()) {
			try {
				return new FileInputStream(getFile());
			} catch (Exception e) {
				throw new UnexpectedException(e);
			}
		}
		return null;
	}

	@Override
	public void set(InputStream is, String type) {
		this.set(is, type, remote);
	}

	/**
	 * 
	 * @param is
	 * @param type
	 * @param remote
	 */
	public void set(InputStream is, String type, boolean remote) {
		set(is, type, remote, "");
	}

	/**
	 * 
	 * @param is
	 * @param type
	 * @param remote
	 * @param subdir
	 */
	public void set(InputStream is, String type, boolean remote, String subdir) {
		this.UUID = Codec.UUID();
		this.type = type;
		this.remote = remote;
		if(subdir != null && subdir.isEmpty() && !subdir.endsWith("/")){
			subdir += "/";
		}
		this.subdir = subdir;
		this.path = subdir + UUID;
		IO.write(is, getFile());
		if (remote) {
			AWSHelper.saveFile(path, file);
			file.delete();
			this.is = AWSHelper.getStream(path);
		}
	}

	public long length() {
		if (remote && is != null) {
			return 0;
		}
		return getFile().length();
	}

	public String type() {
		return type;
	}

	public boolean exists() {
		if (remote) {
			if (is != null) {
				return true;
			}
			return (this.is = AWSHelper.getStream(path)) != null;
		}
		return UUID != null && getFile().exists();
	}

	public File getFile() {
		if (file == null) {
			file = new File(getStore(), path);
			File root = file.getParentFile();
			if(!root.exists()){
				root.mkdirs();
			}
		}
		return file;
	}

	public String getUUID() {
		return UUID;
	}

	public String getSubdir() {
		return subdir;
	}

	public Boolean getRemote() {
		return remote;
	}

	public String url() {
		if (remote) {
			return AWSHelper.getUrl(path);
		}
		return Play.configuration
				.getProperty("attachments.path", "attachments") + "/" + path;
	}

	//

	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return Data.class;
	}

	private static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	public boolean equals(Object o, Object o1) throws HibernateException {
		if (o instanceof Data && o1 instanceof Data) {
			return equal(((Data) o).UUID, ((Data) o1).UUID)
					&& equal(((Data) o).type, ((Data) o1).type)
					&& equal(((Data) o).remote, ((Data) o1).remote)
					&& equal(((Data) o).subdir, ((Data) o1).subdir);
		}
		return equal(o, o1);
	}

	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	@SuppressWarnings("deprecation")
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object o)
			throws HibernateException, SQLException {
		String val = (String) StringType.INSTANCE.get(resultSet, names[0]);

		if (val == null || val.length() == 0 || !val.contains("|")) {
			return new Data();
		}
		String tab[] = val.split("[|]");
		if (tab.length == 3) {
			return new Data(val.split("[|]")[0], val.split("[|]")[1],
					Boolean.valueOf(val.split("[|]")[2]), "");
		}
		return new Data(val.split("[|]")[0], val.split("[|]")[1],
				Boolean.valueOf(val.split("[|]")[2]), val.split("[|]")[3]);
	}

	public void nullSafeSet(PreparedStatement ps, Object o, int i)
			throws HibernateException, SQLException {
		if (o != null) {
			ps.setString(i, ((Data) o).UUID + "|" + ((Data) o).type + "|"
					+ ((Data) o).remote + "|" + ((Data) o).subdir);
		} else {
			ps.setNull(i, Types.VARCHAR);
		}
	}

	public Object deepCopy(Object o) throws HibernateException {
		if (o == null) {
			return null;
		}
		return new Data(((Data) o).UUID, ((Data) o).type, ((Data) o).remote,
				((Data) o).subdir);
	}

	public boolean isMutable() {
		return true;
	}

	public Serializable disassemble(Object o) throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object assemble(Serializable srlzbl, Object o)
			throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Object replace(Object o, Object o1, Object o2)
			throws HibernateException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	//

	public static String getUUID(String dbValue) {
		return dbValue.split("[|]")[0];
	}

	public static Boolean getRemote(String dbValue) {
		return Boolean.valueOf(dbValue.split("[|]")[2]);
	}

	public static String getSubdir(String dbValue) {
		return dbValue.split("[|]")[3];
	}

	public static File getStore() {
		String name = Play.configuration.getProperty("attachments.path",
				"attachments");
		File store = null;
		if (new File(name).isAbsolute()) {
			store = new File(name);
		} else {
			store = Play.getFile(name);
		}
		if (!store.exists()) {
			store.mkdirs();
		}
		return store;
	}

	public void delete() {
		if (remote) {
			AWSHelper.deleteFile(path);
		} else if (exists()) {
			getFile().delete();
		}
	}
	
	@Override
	public String toString() {
		return UUID;
	}

}