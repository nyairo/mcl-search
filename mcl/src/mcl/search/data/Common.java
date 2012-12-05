package mcl.search.data;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import mcl.search.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Common extends HashMap<String,Object> implements Serializable{
	
	public static final Log log = LogFactory.getLog(Common.class);
	
	public static final String DATE 	= "DATE";
	//public static final String DATE_LONG 	= "DATE_LONG";
	public static final String BOOLEAN 	= "BOOLEAN";
	public static final String STRING 	= "STRING";
	public static final String LONG 	= "LONG";
	//private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL,Locale.UK);
	
	public static final String CREATED 	= "created";
	public static final String UPDATED 	= "updated";
	public static final String ACTIVE 	= "active";	
	public static final String UID 		= "uid";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private static final String properties[][] = {{CREATED,DATE}, {UPDATED,DATE},
		{ACTIVE,BOOLEAN},{UID,STRING}};
	
	public abstract String getTable();
	
	public String getFQTable(){
		return "`"+Config.getInstance().getDB()+"`.`"+getTable()+"`";
	}
	
	public abstract String getPrimaryKey();
	
	public String[][] getProperties(){
		return properties;
	}
	
	public Mapping[] getMappings(){
		return null;
	}
	
	//public static <T> String[] concat(String[] first, String[] second) {
	public static String[][] concat(String[][] first, String[][] second) {
		  String[][] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
	}

	public void load(ResultSet rs) throws SQLException {
		for(String[] prop: getProperties()){						
			putString(rs,prop[0]);
		}
		Mapping[] mappings = getMappings();
		if(mappings!=null)
		for(Mapping map: mappings){
			for(String prop: map.getProperties()){
				putString(rs,prop);
			}
		}
		putString(rs,getPrimaryKey());
	}
	
	private void putString(ResultSet rs,String property) throws SQLException{
		put(property,rs.getObject(property));
		/*String strVal = "";
		if(STRING.equals(property[1])){
			strVal = rs.getString(property[0]);			
		}
		else if(LONG.equals(property[1])){
			Long val = rs.getLong(property[0]);
			if(val==null)
				strVal = "";
			else
				strVal =val.toString();
		}
		else if(BOOLEAN.equals(property[1])){
			Boolean val = rs.getBoolean(property[0]);						
			if(val==null)
				strVal = "";
			else if(val)
				strVal = "true";
			else
				strVal = "false";				
		}
		else if(DATE.equals(property[1])){
			Date val = rs.getDate(property[0]);
			if(val==null)
				strVal = "";
			else
				strVal = dateFormat.format(val);
		}
		put(property[0],strVal);*/
	}
	
	public String stringify(){
		StringBuffer json = new StringBuffer("{");
		
		
		
		Mapping[] mappings = getMappings();
		if(mappings!=null)
		for(Mapping map: mappings){
			for(String prop: map.getProperties()){				
				json.append(prop+":'"+get(prop)+"',");
			}
		}
		
		String[][] properties = getProperties();
		int length = properties.length;
		
		for(int i=0;i<length;i++){
			String[] prop = properties[i];			
			if(i==length-1)
				json.append(prop[0]+":'"+get(prop[0])+"'}");
			else
				json.append(prop[0]+":'"+get(prop[0])+"',");
		}
		
		return json.toString();
	}
	/**
	 * Unstringfy flat json objects
	 *   
	 * @param json
	 */
	public void unStringfy(String json){
		if(json==null)
			throw new IllegalArgumentException("Invalid json");
		json = json.replace('}', ' ');
		json = json.replace('{', ' ');
		
		StringTokenizer tokenizer = new StringTokenizer(json,",");
		
		while(tokenizer.hasMoreTokens()){
			unStringfyValue(tokenizer.nextToken());			
		}		
	}
	
	private void unStringfyValue(String token){
		StringTokenizer tokenizer = new StringTokenizer(token,":");
		String property = tokenizer.nextToken();
		
		String value = tokenizer.nextToken();
			if(property!=null){
				property = property.replace('\"', ' ');
				property = property.replace('\'', ' ');
				property = property.trim();
			}
			if(value!=null){
				value = value.replace('\"', ' ');
				value = value.replace('\'', ' ');
				value = value.trim();
			}
			put(property,value);						
	}
	
	/*
	public Object getDBType(String[] property){		
		if(STRING.equals(property[1])){
			return get(property[0]);
		}
		else if(LONG.equals(property[1])){
			Long val = null;
			try{				
				val = new Long(get(property[0]));
			}catch(Exception ex){
				log.info("Failed converting to long");
			}
			return val;
		}
		else if(BOOLEAN.equals(property[1])){
			String val = null;						
			val = get(property[0]);
			if("true".equals(val))
					return Boolean.TRUE;
			else if("false".equals(val))
				return Boolean.FALSE;
			else
				return null;
		}
		else if(DATE.equals(property[1])){
			return new Date();
		}
		return get(property[0]);
	}
	*/
	public Object[][] getDefaults(){
		return null;
	}
	public Common initialiaze() {
		Object[][] defaults = getDefaults();
		if(defaults!=null)
			for(Object[] prop: defaults){
				if(prop.length>1)
					put(prop[0].toString(), prop[1]);
				else
					put(prop[0].toString(), null);
			}
		return this;
	}
	/*
	public Common initialiaze() {
		for(String[] prop: getProperties()){
			if(prop.length>2)
				put(prop[0], prop[2]);
			else
				put(prop[0], null);
		}
		return this;
	}
	*/
	/**
	 * Copy entries <b>to<b/> map passed as parameter.
	 * @param copyTo
	 * @return
	 */
	public Common copy(Common copyTo){
		copyTo.putAll(this);
		return copyTo;
	}
	
	/**
	 * Copy entries <b>from</b> the map passed as parameter.
	 * 
	 * @param copyFrom
	 */
	public void copyFrom(Common copyFrom){
		putAll(copyFrom);
	}
	
	/**
	 * get create table String as ONE statement
	 * 
	 */
	public String getCreate(String schema){
		String type = null;
		StringBuffer sb = new StringBuffer();		
		sb.append(" CREATE TABLE  `"+schema+"`.`"+getTable()+"` ( \n ");
		//sb.append(" CREATE TABLE  "+schema+"."+getTable()+" ( \n ");
  
		for(String[] prop: getProperties()){
			if(LONG==prop[1])
				type = " int(11) ";
			else if (STRING==prop[1])
				type = " varchar(225) ";
			else if (DATE==prop[1])
				type = " datetime ";
			else if (BOOLEAN==prop[1])
				type = " int(1) ";
			
			if(prop[0]==getPrimaryKey()){
				sb.append("`"+prop[0]+"` "+ type +" NOT NULL AUTO_INCREMENT , \n");
				//sb.append(prop[0]+"  "+ type +" NOT NULL AUTO_INCREMENT, \n");
			}else{
				sb.append(" "+prop[0]+" "+type +" , \n");
			}					
		}
		sb.append("PRIMARY KEY  ("+getPrimaryKey()+") \n");//primary key
		//sb.append("PRIMARY KEY  (`"+getPrimaryKey()+"`) \n");//primary key
		sb.append(") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8; \n");
		return sb.toString();
	}
	
	public  String appendTable(String prop){
		return getTable()+"."+prop;
	}
}
