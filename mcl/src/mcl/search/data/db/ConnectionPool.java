package mcl.search.data.db;

import java.util.*;
import java.util.Date;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionPool implements Runnable
{	
	
	private static Log log = LogFactory.getLog(ConnectionPool.class);
	
	// Number of initial connections to make.
	private int m_InitialConnectionCount = 5;	
	// The number of milliseconds a connection can be in use.
	private int m_ConnectionUsageTimeout = 60000;
	// A list of available connections for use.
	private Vector<Connection> m_AvailableConnections = new Vector<Connection>();
	// A list of connections being used currently.
	private Vector<Connection> m_UsedConnections = new Vector<Connection>();
	// The URL string used to connect to the database
	private String m_URLString = null;
	// The username used to connect to the database
	private String m_UserName = null;	
	// The password used to connect to the database
	private String m_Password = null;

	private Thread m_CleanupThread = null;
	
	private Map<Connection, Long> timer = new HashMap<Connection, Long>();
											 
	//Constructor
	public ConnectionPool(String urlString, String user, String passwd) throws SQLException
	{
		// Initialize the required parameters
		m_URLString = urlString;
		m_UserName = user;
		m_Password = passwd;

		for(int cnt=0; cnt<m_InitialConnectionCount; cnt++)
		{
			// Add a new connection to the available list.
			m_AvailableConnections.addElement(getConnection());
		}
		
		 // Create the cleanup thread
        m_CleanupThread  = new Thread(this);
        m_CleanupThread.start(); 
	}	
	
	private Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(m_URLString, m_UserName, m_Password);
	}
	
	public synchronized Connection checkout() throws SQLException
	{
		Connection newConnxn = null;
		
		
		log.info("Available Connections : " + availableCount());
		log.info("     Used Connections : " + usedCount());
		log.info("  Initial Connections : " + initialCount());

		
		if(m_AvailableConnections.size() == 0)
		{
			// Im out of connections. Create one more.
			 newConnxn = getConnection();
			// Add this connection to the "Used" list.
			 m_UsedConnections.addElement(newConnxn);
			// We dont have to do anything else since this is
			// a new connection.			
		}
		else
		{
			// Connections exist !
			// Get a connection object
			newConnxn = (Connection)m_AvailableConnections.lastElement();
			// Remove it from the available list.
			m_AvailableConnections.removeElement(newConnxn);
			// Add it to the used list.
			m_UsedConnections.addElement(newConnxn);			
		}		
		
		startTimer(newConnxn);
		// Either way, we should have a connection object now.
		return newConnxn;
	}

	public synchronized void checkin(Connection c)
	{
		if(c != null)
		{
			// Remove from used list.
			m_UsedConnections.removeElement(c);
			// Add to the available list
			m_AvailableConnections.addElement(c);	
			stopTimer(c);
		}
	}

	/**
	 * Close connection with errors
	 * @param c
	 */
	public synchronized void destroy(Connection c)
	{
		if(c != null)
		{
			// Remove from used list.
			m_UsedConnections.removeElement(c);
			try {
				c.rollback();
				c.close();
			} catch (SQLException e) {				
				//e.printStackTrace();
			}
			
			c = null;
			// Add to the available list
			//m_AvailableConnections.addElement(c);	
			stopTimer(c);
		}
	}
	
	
	private synchronized void startTimer(Connection c){
		timer.put(c, new Date().getTime());
	}
	
	private synchronized Long stopTimer(Connection c){
		return timer.remove(c);
	}
	
	private int getUsage(Connection c){
		Long time = timer.get(c);
		time = new Date().getTime()-time;
		return  time.intValue();
	}
	public int initialCount()
	{
		return m_InitialConnectionCount;
	}
	
	public int usedCount()
	{
		return m_UsedConnections.size();
	}
	
	public int availableCount()
	{
		return m_AvailableConnections.size();
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
				synchronized(this)
				{
					while(m_AvailableConnections.size() > m_InitialConnectionCount)
					{
						// Clean up extra available connections.
						Connection c = (Connection)m_AvailableConnections.lastElement();
						m_AvailableConnections.removeElement(c);
							
						// Close the connection to the database.
						c.close();
					}
					
					//Clean up connection that have been open for too long
					Iterator<Connection> i = m_UsedConnections.iterator();
					Connection c = null;					
					while(i.hasNext()){
						c = i.next();						
						if(getUsage(c)>this.m_ConnectionUsageTimeout){
							c.close();
							m_UsedConnections.remove(c);
						}
					}
					
					//Test available connections
					i = m_AvailableConnections.iterator();
					while(i.hasNext()){
						c = i.next();
						try{
							Statement statement = c.createStatement();
						      String testSQL = "SELECT now()";
						      ResultSet rs = statement.executeQuery(testSQL);
						      if(rs!=null)
						    	  rs.close();
						      if(statement!=null)
						    	  statement.close();
						 //Error occured. Remove connection.     
						}catch(Exception ex){
							m_AvailableConnections.remove(c);
						}
					}
					// Clean up is done				
				}								
				// Now sleep for 1 minute
				Thread.sleep(60000 * 1);
			}	
		}
		catch(SQLException sqle)
		{
			log.error(sqle);
		}
		catch(Exception e)
		{
			log.error(e);
		}
	}

	  public static void main (String[] args)
	    {	
	         try 
	         {
	              Class.forName("org.gjt.mm.mysql.Driver").newInstance(); 				
	         }
	         catch (Exception E) 
	         {
	            System.err.println("Unable to load driver.");
	            E.printStackTrace();
	         }
			
	         try
	         {		
	               ConnectionPool cp = new ConnectionPool("jdbc:mysql://localhost/test", "", "");
			
	               Connection []connArr = new Connection[7];
			
	               for(int i=0; i<connArr.length;i++)
	               {
	                   connArr[i] = cp.checkout();
	                   System.out.println("Checking out..." + connArr[i]);
	                   System.out.println("Available Connections ... " + cp.availableCount());
	               }				

	               for(int i=0; i<connArr.length;i++)
	               {
	                   cp.checkin(connArr[i]);
	                   System.out.println("Checked in..." + connArr[i]);
	                   System.out.println("Available Connections ... " + cp.availableCount());
	               }
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	     }
/*
 * Runtime.getRuntime().addShutdownHook(new Thread() {
    public void run() { database.close(); }
});	
 */
}

