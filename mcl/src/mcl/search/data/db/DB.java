package mcl.search.data.db;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import mcl.search.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DB {

	private static Map<String, BigDecimal> cache = new HashMap<String, BigDecimal>();

	private static final Log log = LogFactory.getLog(DB.class);

	private static DB instance = null;
	private ConnectionPool pool = null;	

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

	public static Session getSession(Config config) throws SQLException {

		if (instance == null)
			instance = new DB(Config.getInstance());

		if(instance.pool==null)
			instance.configure(Config.getInstance());
		if(instance.pool==null)//If we can't still initialize
			return null;
		return new Session(instance.pool.checkout());
	}

	public static DB getIntance() {
		return instance;
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
		BigDecimal seqid = null;

		seqid = cache.get(entityName);

		if (seqid == null) {
			// PreparedStatement seqQ = null;
			ResultSet rs = null;
			Session session = getSession(null);
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
			c = instance.pool.checkout();
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
			instance.pool.checkin(c);
		}
		return new BigDecimal(id);
	}

	public void checkIn(Connection c) {
		instance.pool.checkin(c);
	}

	public static void close(PreparedStatement pst, ResultSet rs) {
		close(rs);
		close(pst);
	}

	public void destroy(Connection c) {
		instance.pool.destroy(c);
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
}