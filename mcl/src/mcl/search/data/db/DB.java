package mcl.search.data.db;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcl.search.Config;
import mcl.search.data.CCollection;
import mcl.search.data.Catalog;
import mcl.search.data.CatalogUrl;
import mcl.search.data.Common;
import mcl.search.data.Concept;
import mcl.search.data.User;
import mcl.search.data.model.CatalogModel;
import mcl.search.data.model.UserModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DB {

	private static Map<String, BigDecimal> cache = new HashMap<String, BigDecimal>();

	private static final Log log = LogFactory.getLog(DB.class);

	private static DB db_instance = null;
	private static DB sdb_instance = null; //Superuser db_instance when we need it
	private ConnectionPool pool = null;

	private boolean superuser = false;
	private static Common[] tables = new Common[]{new CCollection(),new Concept(),
		new User(), new Catalog(), new CatalogUrl()	
	};

	private DB(Config config) {		
		configure(config);
	}	
	
	private void configure(Config config){
		try {
			if (config.getDBCLASSNAME() != null)
				Class.forName(config.getDBCLASSNAME());
			// Class.forName("org.postgresql.Driver");
			else
				new IllegalArgumentException("No DB class name specified.");

		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		log.info("JDBC Driver Registered!");

		try {
			StringBuffer connstr = new StringBuffer("jdbc:");
			connstr.append(config.getDBTYPE()).append("://");// RDMS
			connstr.append(config.getDBHOST()).append(":");// Host machine
			connstr.append(config.getDBPORT()).append("/");// port
			connstr.append(config.getDB());// database
			String user = config.getDBUSER();// user
			String password = config.getDBPW();// password

			log.debug("Getting connection for :" + connstr.toString()
					+ " user:" + user + " password:*****");// + password);
			pool = new ConnectionPool(connstr.toString(), user, password);
			// "jdbc:postgresql://127.0.0.1:5432/gportal", "gportal",
			// "gportal");

		} catch (SQLException e) {
			log.debug("Connection Failed! Check output console");
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param isBatch
	 * @return
	 * @throws SQLException
	 */
	public static Session getSession(boolean isBatch) throws SQLException {

		if (db_instance == null)
			db_instance = new DB(Config.getInstance());

		if(db_instance.pool==null)
			db_instance.configure(Config.getInstance());
		if(db_instance.pool==null)//If we can't still initialize
			return null;
		return new Session(db_instance.pool.checkout(), isBatch);
	}
	
	public static Session getSSession(String suser, String spassword, boolean isBatch) throws SQLException {
		Config.getSInstance(suser,spassword);
		if (sdb_instance == null||!sdb_instance.superuser)
			sdb_instance = new DB(Config.getSInstance());

			sdb_instance.pool.checkout();
			if(sdb_instance.pool==null)
				sdb_instance.configure(Config.getSInstance());
			if(sdb_instance.pool==null)//If we can't still initialize
				return null;	
		return new Session(sdb_instance.pool.checkout(), isBatch);
	}

	public static DB getIntance() {
		return db_instance;
	}
	
	public static DB getSIntance() {
		return sdb_instance;
	}
	

	public static void close(Statement st) {
		try {
			if (st != null)
				st.close();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	public static BigDecimal getNextID(String entityName) throws SQLException {
		return DB.getNextID(entityName, false);
	}	
	
	public static BigDecimal getNextID(String entityName, boolean s) throws SQLException {
		BigDecimal seqid = null;

		seqid = cache.get(entityName);

		if (seqid == null) {
			// PreparedStatement seqQ = null;
			ResultSet rs = null;
			Session session = getSession(false);
			try {
				session.createStatement("select seq.ad_sequence_id from ad_sequence seq where seq.name=?");
				session.getStmt().setString(1, entityName);
				session.execute();
				rs = session.getRs();

				if (rs.next())
					seqid = (BigDecimal) rs.getBigDecimal(1);

			} catch (SQLException e1) {
				log.error(e1.getMessage(), e1);
			} finally {
				session.close();
			}

		}
		log.debug("Sequence id for " + entityName + " is " + seqid);

		Integer id = 0;
		CallableStatement pst = null;
		Connection c = null;
		try {
			if(s)
				c = sdb_instance.pool.checkout();			
			else
				c = db_instance.pool.checkout();
			pst = c.prepareCall("{call nextid(?,?,?)}");
			pst.registerOutParameter(3, Types.INTEGER);
			log.debug("ID for " + entityName + ":" + seqid + ": intValue :"
					+ seqid.intValueExact());
			pst.setInt(1, seqid.intValueExact());
			pst.setString(2, "N");
			pst.execute();
			id = pst.getInt(3);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DB.close(pst);
			if(s)
				sdb_instance.pool.checkin(c);
			else
				db_instance.pool.checkin(c);
			
		}
		return new BigDecimal(id);
	}

	
	public void checkIn(Connection c, boolean s) {
		if(s)
			sdb_instance.pool.checkin(c);
		else
			db_instance.pool.checkin(c);
	}
	
	
	
	public void checkIn(Connection c) {
		checkIn(c, false);
	}

	public static void close(PreparedStatement pst, ResultSet rs) {
		close(rs);
		close(pst);
	}

	public void destroy(Connection c, boolean s) {
		sdb_instance.pool.destroy(c);
	}

	public void destroy(Connection c) {
		db_instance.pool.destroy(c);
	}

	
	
	public static boolean isValid(Connection c) {
		Statement stmt = null;
		ResultSet rs = null;
		boolean valid = false;
		try {
			stmt = c.createStatement();
			// oracle
			//rs = stmt.executeQuery("SELECT 1 FROM Dual");
			// others
			rs = stmt.executeQuery("SELECT 1");
			if (rs.next()) {
				valid = true; // connection is valid
			}
		} catch (SQLException e) {						
		} finally {

			try {
				if (stmt != null)
					stmt.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
			}
		}
		return valid;
	}
	
	/**
	 * 
	 * @param schema
	 * @param user
	 * @param userpassword
	 * @param suser
	 * @param spassword
	 * @return
	 */
	public static String createDB(String schema, String user,String userpassword,  String suser, String spassword){
		
		try {
			
			
			Session session = DB.getSSession(suser,spassword, true);	
			
			try{
				session.createStatement("DROP USER '"+user+"'@'localhost';");
				session.execute();
				session.close();
			}catch(Exception ex){
				session.closeS();
			}
			
			session = DB.getSSession(suser, spassword, true);
			
			session.createStatement();
			
			Config.getInstance().setProperty(Config.DB, schema);
			Config.getInstance().setProperty(Config.DBUSER, user);
			Config.getInstance().setProperty(Config.DBPW, userpassword);
			Config.getInstance().write();
			
			
			session.addBatch("CREATE DATABASE `"+schema+"` /*!40100 DEFAULT CHARACTER SET latin1 */; \n");
			session.addBatch("FLUSH PRIVILEGES; \n");
			
			session.addBatch("CREATE USER '"+user+"'@'localhost' IDENTIFIED BY '"+userpassword+"'; \n");
			session.addBatch("GRANT USAGE ON `"+schema+"`.* TO '"+user+"'@'localhost';");
			//session.addBatch("UPDATE USER SET password=PASSWORD('"+userpassword+"') where USER='"+user+"'@'localhost';");
			session.addBatch("GRANT ALL PRIVILEGES ON `"+schema+"`.* TO '"+user+"'@'localhost' WITH GRANT OPTION; \n");

			String[] otherStmts = null;
			for(Common c: tables){
				session.addBatch(" DROP TABLE IF EXISTS "+c.getFQTable()+"; \n");
				session.addBatch(c.getCreate(schema)+"\n");
				otherStmts = c.getStatements(schema);
				if(otherStmts!=null){
					for(String s: otherStmts){
						session.addBatch(s);
					}
				}
			}											
			session.executeBatchQuery();
			session.closeS();
			
			UserModel userm = new UserModel();
			User newUser = (User) userm.getNew().initialiaze();
			newUser.put(User.USERNAME, user);
			newUser.put(User.PASSWORD, userpassword);
			userm.insertUser(newUser);
			
			/*session  = DB.getSession(true);
			String catTable = "`"+schema+"`.`"+Catalog.TABLE_NAME+"`";
			session.createStatement();
			session.addBatch("INSERT INTO "+catTable+" ( "+Catalog.NAME+" ) VALUES('TEST');");
			session.executeBatchQuery();
			session.close();*/
			
			List urls  = new ArrayList();
			CatalogModel catm = new CatalogModel();
			Catalog cat = (Catalog) catm.getNew().initialiaze();
			cat.put(Catalog.NAME,"Manage Concept Collection");
			CatalogUrl url = null;
			
			url = (CatalogUrl) catm.getNewUrl().initialiaze();
			url.put(CatalogUrl.URL, "GET /faces/addconcepts?q");
			url.put(CatalogUrl.DESCRIPTION, "Add concepts to collection");
			urls.add(url);
			
			url = (CatalogUrl) catm.getNewUrl().initialiaze();
			url.put(CatalogUrl.URL, "GET /faces/removeconcepts?q");
			url.put(CatalogUrl.DESCRIPTION, "Remove concepts from collection");
			urls.add(url);
			
			url = (CatalogUrl) catm.getNewUrl().initialiaze();
			url.put(CatalogUrl.URL, "GET /faces/collection?q");
			url.put(CatalogUrl.DESCRIPTION, "List collection for a given criteria");
			urls.add(url);
			
			cat.put(Catalog.URLS,urls);
			
			catm.insertCatalog(cat);
			
			cat = (Catalog) catm.getNew().initialiaze();
			cat.put(Catalog.NAME,"Manage tags");			
			
			url = (CatalogUrl) catm.getNewUrl().initialiaze();
			url.put(CatalogUrl.URL, "GET /faces/addtags?q");
			url.put(CatalogUrl.DESCRIPTION, "Add tags to concept");
			urls.add(url);
			cat.put(Catalog.URLS,urls);
			
			catm.insertCatalog(cat);
			
			
			cat = (Catalog) catm.getNew().initialiaze();
			cat.put(Catalog.NAME,"Management");			
			
			url = (CatalogUrl) catm.getNewUrl().initialiaze();
			url.put(CatalogUrl.URL, "GET /faces/createdb?q");
			url.put(CatalogUrl.DESCRIPTION, "Create application schema.");
			urls.add(url);
			cat.put(Catalog.URLS,urls);
			
			catm.insertCatalog(cat);
								
		} catch (SQLException e) {
			
			e.printStackTrace();
			return e.getMessage();
		}
		
		return "Schema was created successfully.";
	}
}