package mcl.search.data.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import mcl.search.data.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserModel extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(UserModel.class);


	public UserModel() {

	}

	public Integer insertUser(User user) throws SQLException {

		return insertObject(user);
	}

	public User getUser(String username) {
		try {
			return (User) getObject(getNew(), User.USERNAME+"=?", new String[]{username});
		} catch (SQLException e) {
			log.error("Error retrieving user with user name:" + username, e);
		}
		return null;
	}


	public User user(String username) throws SQLException{
		User user = getUser(username);
		if(user==null){
			user = getNew();
			user.put(User.USERNAME, username);
			insertUser(user);
		}
		return user;
	}
	
	public User getUser(String where, String[] values) {
		try {
			return (User) getObject(getNew(), where, values);
		} catch (SQLException e) {
			log.error("Error retrieving user", e);
		}
		return null;
	}
	

	public User getNew() {
		User user = new User();

		return (User) user.initialiaze();
	}

	public void updateUser() {

	}

	public List<User> getUsers() throws SQLException {
		return (List<User>) getObjects(new User(),User.USERNAME+"!=?", new String[]{"admin"});
	}

	public int deleteUser(User user) throws SQLException {
		return deleteObject(user, user.getPrimaryKey());
	}
	
	public boolean login(String username, String passwd){		
		User user = getUser(username);				
		return user!=null&&passwd.equals(user.get(User.PASSWORD));
	}
	
	public boolean login(User user, String passwd){
									
		return user!=null&&passwd.equals(user.get(User.PASSWORD));
	}
}
