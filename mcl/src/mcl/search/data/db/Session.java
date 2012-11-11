package mcl.search.data.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Manage a database session
 * @author nyairo
 *
 */
public class Session {
	
	private static final Log log = LogFactory.getLog(Session.class);
	
	private Connection connection = null;
	private ResultSet rs = null;
	private PreparedStatement stmt = null;
	private boolean isOpen = false;
	
	public Session(Connection c){
		connection = c;
		isOpen = true;
	}
	

	/**
	 * Create special prepared statement that returns generated keys
	 * 
	 * @param sql
	 * @return Generated auto keys
	 * @throws SQLException
	 */
	public void createInsertStatement(String sql) throws SQLException{
		stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		log.info("Creating prepared insert statement");		
	} 
	
	
	public ResultSet getRs(){		
			
		return rs;
	}
	
	public PreparedStatement getStmt() {
		return stmt;
	}
	
	public boolean execute() throws SQLException{
		boolean success = stmt.execute();
		rs = stmt.getResultSet();
		return 	success;	
	}
	
	public int executeUpdate() throws SQLException{
		int count = stmt.executeUpdate();
		rs = stmt.getGeneratedKeys();
		return 	count;	
	}
	
	
	public PreparedStatement createStatement(String sql)
	throws SQLException {
		stmt = connection.prepareStatement(sql); 
		
	return stmt;
	}	
	public boolean isOpen(){
		return isOpen;
	}	
	public void close(){
		DB.close(stmt, rs);
		
		if (DB.isValid(connection))
			DB.getIntance().checkIn(connection);
		else
			DB.getIntance().destroy(connection);		
		isOpen = false;
	}
	public void executeQuery() throws SQLException {
		rs = stmt.executeQuery();		
	}
}
