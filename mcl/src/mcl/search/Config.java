package mcl.search;


import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class Config {

	private static final Log log = LogFactory.getLog(Config.class);

	private static Config sconf_instance = null;
	private static Config conf_instance = null;
	private static final String DBTYPE 				= "dbtype";
	private static final String DBHOST 				= "dbhost";
	private static final String DBPORT 				= "dbport";
	public static final String DB 					= "db";
	private static final String DBCLASSNAME 		= "dbclassname";
	public static final String DBUSER 				= "dbuser";
	public static final String DBPW 				= "dbpw";		
	private static final String MAIL_PROTOCOL 		= "mail_protocol";
	private static final String MAIL_HOST			= "mail_host";
	private static final String MAIL_USER 			= "mail_user";
	private static final String MAIL_USER_PASSWORD 	= "mail_user_password";
	private static final String MAIL_PORT 			= "mail_port";
	private static final String MAIL_FROM_ADDRESS 	= "mail_from_address";

	private static final String MAIL_USE_AUTH 		= "mail_use_auth";

	private static final String dbuser = "dbuser";
	private static final String dbpw = "dbpw";

	private Properties properties = null;

	/**
	 * private initializer to maintain a singleton
	 * 
	 * @throws Exception
	 */
	//private Config(FacesContext context) {
	private Config() {

		String log4jfilename = "/WEB-INF/log4j.properties";
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		URL url;
		try {
			url = context.getResource(log4jfilename);//context.getExternalContext().getResource(log4jfilename);

			if (url != null) {
				Properties p = new Properties();
				p.load(context.getResourceAsStream(log4jfilename));
				PropertyConfigurator.configure(p);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		log.debug("read configuration");
		load_conf(context);

	}

	private void load_conf(ExternalContext context) {
		try {
			String config_filename = "/WEB-INF/conf.properties";
			properties = new Properties();
			properties.load(context.getResourceAsStream(config_filename));
			log.debug("read configuration ok");
		} catch (Exception e) {
			log.equals(e);
		}
		
	}

	public static Config getInstance() {
		if (conf_instance == null) {
			conf_instance = new Config();
		}
		return conf_instance;
	}
	
	
	public static Config getSInstance() {
		
		return sconf_instance;
	}
		
	public static Config getSInstance(String suser, String spassword) {
		if (sconf_instance == null ) {
			sconf_instance = new Config();			
			sconf_instance.setProperty(dbuser, suser);
			sconf_instance.setProperty(dbpw, spassword);
			sconf_instance.setProperty(DB, "information_schema"); //Assume user is root or has super user privs
		}
		return sconf_instance;
	}
	
	
	
	private String getProperty(String name) {
		return (String) properties.get(name);

	}

	/**
	 * 
	 * @return
	 */
	public String getDBHOST() {
		return getProperty(DBHOST);
	}

	public String getDBPORT() {
		return getProperty(DBPORT);
	}

	public String getDB() {
		return getProperty(DB);
	}

	public String getDBUSER() {
		return getProperty(DBUSER);
	}

	public String getDBPW() {
		return getProperty(DBPW);
	}

	/**
	 * @return the dBTYPE
	 */
	public String getDBTYPE() {
		return getProperty(DBTYPE);
	}

	public String getDBCLASSNAME() {
		return getProperty(DBCLASSNAME);
	}

	public String getMAIL_HOST() {
		
		return getProperty(MAIL_HOST);		
	}
	
	public String getMAIL_PROTOCOL() {
		
		return getProperty(MAIL_PROTOCOL);		
	}

	public String getMAIL_USER() {
		return getProperty(MAIL_USER);
	}

	public String getMAIL_USER_PASSWORD() {
		return getProperty(MAIL_USER_PASSWORD);
	}

	public String getMAIL_PORT() {
		return getProperty(MAIL_PORT);
	}

	public String getMAIL_FROM_ADDRESS() {
		return getProperty(MAIL_FROM_ADDRESS);
	}

	public Object getMAIL_USE_AUTH() {
		
		return getProperty(MAIL_USE_AUTH);
	}

	public void setDB(String schema) {
		properties.put(DB, schema);		
		write();
	}
	public void setProperty(String prop, String value){
		if(value!=null)
			properties.put(prop, value);
	}
	public void write(){
		log.debug("write configuration");
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		try {
			String config_filename = "/WEB-INF/conf.properties";		
			String url = context.getRealPath(config_filename);
			FileOutputStream out = new FileOutputStream(new File(url));			
			properties.store(out, null);
			out.close();
			out = null;
			load_conf(context);
			log.debug("write configuration ok");
		} catch (Exception e) {
			log.equals(e);
		}
	}

}
