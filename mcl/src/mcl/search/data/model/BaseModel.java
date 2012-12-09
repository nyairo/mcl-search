package mcl.search.data.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mcl.search.data.Common;
import mcl.search.data.Mapping;
import mcl.search.data.db.DB;
import mcl.search.data.db.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseModel  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(BaseModel.class);
	
	public Session getCon() throws SQLException{
		log.info("Retrieving db connection");
		return DB.getSession(false);
	}
	
	/***
	 * Create the sql statement without parameter values
	 * @param data
	 * @return
	 */
	protected String getInsertSql(Common data){
		String table = data.getFQTable();
		String[][] properties = data.getProperties();
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlParams = new StringBuffer();
		
		sql.append("INSERT INTO "+table+" (");		
		sqlParams.append(" VALUES (");
		
		int count = 0;
		for(String[] col: properties){
			count++;	
			if(col.length<3)
				if(count==properties.length){
					sql.append(col[0]+")");
					sqlParams.append("?)");
				}else{
					sql.append(col[0]+",");
					sqlParams.append("?,");
				}
		}
		String insertSql = sql.append(sqlParams.toString()).toString(); 
		log.info("Retrieving insert statement:"+insertSql);
		return insertSql;		
	}
	
	protected String getUpdateSql(Common data){
		String table = data.getFQTable();
		String[][] properties = data.getProperties();
		
		StringBuffer sql = new StringBuffer();		
					
		sql.append("UPDATE "+table+" SET ");				
		
		int count = 0;
		for(String[] col: properties){
			count++;			
			if(count==properties.length){
				sql.append(col[0]+"=?");			
			}else{
				sql.append(col[0]+"=?,");			
			}
		}		
		sql.append(" WHERE "+data.getPrimaryKey()+"="+data.get(data.getPrimaryKey()));
		
		String updateSql = sql.toString(); 
		log.info("Retrieving update statement:"+updateSql);
		return updateSql;		
	}
	
	/**
	 * Set parameters for an arbitrary prepared statement
	 * 
	 * @param insertStmt
	 * @param properties
	 * @param data
	 * @throws SQLException
	 */
	protected void setParameters(PreparedStatement insertStmt,
			String[][] properties, Common data) throws SQLException {
		
		for(int i=0;i<properties.length;i++){
			String[] prop = properties[i];			
			//insertStmt.setObject(i+1, data.getDBType(prop));
			insertStmt.setObject(i+1, data.get(prop[0]));
		}				
		log.info("Set parameters. No. of params:"+insertStmt.getParameterMetaData().getParameterCount());
	}
	
	protected void setParameters(PreparedStatement insertStmt,
			 String[] values) throws SQLException {
		if(values!=null)
			for(int i=0;i<values.length;i++){					
				insertStmt.setObject(i+1, values[i]);
			}				
		log.info("Set parameters. No. of params:"+insertStmt.getParameterMetaData().getParameterCount());
	}
	
	protected Integer insertObject(Common data) throws SQLException{
		
		//initialize values
		Date now = new Date();
		data.put(Common.CREATED, now);
		data.put(Common.UPDATED, now);
		data.put(Common.ACTIVE, Boolean.TRUE);
		
		String insertSql = null;
		Integer id = null;
		Session session = null;
		try{
			session = DB.getSession(false);		
			insertSql = getInsertSql(data);
			session.createInsertStatement(insertSql);			
			setParameters(session.getStmt(),data.getProperties(), data);
			session.executeUpdate();
			ResultSet rs = session.getRs();
			
			rs.first();		
			id = rs.getInt(1);
			log.info("Insert record to table:"+data.getFQTable()+". Generated key:"+id);
			data.put(data.getPrimaryKey(), id);
		}catch(Exception e){
			log.error(e);
		}finally{
			if(session!=null)
				session.close();
		}					
		return id;
	}
	
	public int update(String sql,String[] values ){
		int count = 0;
		Session session = null;
		try{
			session = DB.getSession(false);					
			session.createStatement(sql);			
			setParameters(session.getStmt(), values);
			count = session.executeUpdate();
			
			log.info("Update records in :"+sql);
		}catch(Exception e){
			log.error(e);
		}finally{
			if(session!=null)
				session.close();
		}					
		return count;		
	}
	
	public int updateObject(Common data) throws SQLException{
		
		//initialize values
		Date now = new Date();		
		data.put(Common.UPDATED, now);		
		
		String updateSql = null;
		int count = 0;
		Session session = null;
		try{
			session = DB.getSession(false);		
			updateSql = getUpdateSql(data);
			session.createStatement(updateSql);			
			setParameters(session.getStmt(),data.getProperties(), data);
			count = session.executeUpdate();
			
			log.info("Update record in table:"+data.getFQTable());
		}catch(Exception e){
			if(data!=null && updateSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+updateSql);
			else
				log.error(e);
		}finally{
			if(session!=null)
				session.close();
		}					
		return count;
	}
	
	/**
	 * 
	 * @param common
	 * @param property
	 * @param value
	 * @return
	 * @throws SQLException 
	 */
	protected Common getObject(Common data, String where, String[] values) throws SQLException {
		

		Session session = null;
		String findSql = null;
		try{
			session = DB.getSession(false);
			findSql = getSQL(data, where);
			session.createStatement(findSql);
			setParameters(session.getStmt(), values);
			session.executeQuery();
			ResultSet rs = session.getRs();
			boolean found = false;
			if(rs.first()){
				data.load(rs);
				found = true;
			}			
			if(found){
				log.info("Found record for table:"+data.getFQTable());
				log.info("Record:"+data.toString());
				return data;
			}
			log.info("Record not found on table with the given params:"+findSql);
		}catch(Exception e){
			if(data!=null && findSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+findSql);
			else
				log.error(e);
		}
		finally{
			if(session!=null)
				session.close();
		}
		return null;
	}
	
	
	public List<?> getObjects(Common data) throws SQLException {
		
		
		List<Common> rows = new ArrayList<Common>();
		
		Session session = null;
		String findSql = null;
		try{
			session = DB.getSession(false);
			findSql = getSQL(data, null);
			session.createStatement(findSql);
			session.executeQuery();
			ResultSet rs = session.getRs();
			//boolean found = false;
			while(rs.next()){
				data = data.getClass().newInstance();
				data.load(rs);
				rows.add(data);
				//found = true;
			}			
			//if(found){
				log.info("Found record for table:"+data.getFQTable());
				log.info("Record:"+data.toString());
				log.info("Record not found on table with the given params:"+findSql);
				return rows;
			//}
			
		}catch(Exception e){
			if(data!=null && findSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+findSql);
			else
				log.error(e);
		}
		finally{
			if(session!=null)
				session.close();
		}
		return null;
	}
	
	/**
	 * 
	 * @param data
	 * @param where
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	public List<?> getObjects(Common data, String where, String[] values) throws SQLException {
		
		List<Common> lst = null;
				
		Session session = null;
		String findSql = null; 
		try{
			session = DB.getSession(false);
			findSql = getSQL(data, where);
			session.createStatement(findSql);
			setParameters(session.getStmt(), values);
			session.executeQuery();
			ResultSet rs = session.getRs();
			
			lst = new ArrayList<Common>();
			
			while(rs.next()){
				data = data.getClass().newInstance();
				data.load(rs);	
				lst.add(data);				
			}			
			
			//log.info("Record not found on table with the given params:"+findSql);
			
		}catch(Exception e){
			if(data!=null && findSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+findSql);
			else
				log.error(e);
		}
		finally{
			if(session!=null)
				session.close();
		}
		return lst;
	}	
	
	/**
	 * Call when you need an initialized item. All properties are set to null.
	 * @return
	 */
	public abstract Common getNew();

	public int deleteObject(Common data, String property) throws SQLException {
		String deleteSql = "DELETE FROM "+data.getFQTable()+" WHERE "+property+"="+data.get(property);
		Session session = null;
		int success = 0;
		try{
			session = DB.getSession(false);			
			session.createStatement(deleteSql);
			success = session.executeUpdate();
						
			log.info("{"+success+"} Record(s) were deleted:"+deleteSql);
			
		}catch(Exception e){
			if(data!=null && deleteSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+deleteSql);
			else
				log.error(e);
		}finally{
			if(session!=null)
				session.close();
		}
		return success;		
	}
	

	public int deleteObjects(Common data, String where, String[] values) throws SQLException {
		
		Session session = null;
		int success = 0;
		String deleteSql = null;
		try{
			session = DB.getSession(false);	
			deleteSql = getDeleteSQL(data, where);
			session.createStatement(deleteSql);
			setParameters(session.getStmt(), values);			
			success = session.executeUpdate();
			
			log.info(success+" Record(s) deleted:"+deleteSql);
			
		}catch(Exception e){
			if(data!=null && deleteSql!=null)
				log.error(data.getFQTable()+"|"+e+"|"+deleteSql);
			else
				log.error(e);
		}finally{
			if(session!=null)
				session.close();
		}
		return success;		
	}
	
	private String getDeleteSQL(Common data, String where){
		 String deleteSql = "DELETE FROM "+data.getFQTable()+" WHERE "+where+";";
		 return deleteSql;
	}
	/*
	private String getSQL(Common data, String[] properties, String[] value, String[] ops){

		
		String[][] fields = data.getProperties();
		int length = fields.length;
		StringBuffer sel = new StringBuffer();
		for(int i=0;i<length;i++){
			sel.append(fields[i][0]);
			if(i!=length-1)
				sel.append(",");
		}
		
		String findSql = "SELECT "+sel.toString()+" FROM "+data.getFQTable()+" WHERE active=1";
		
		if(data.getFQTable()==User.TABLE_NAME)
			findSql = findSql+" AND "+User.USER_ID+" !=1";
		
		//String findSql = "SELECT * FROM "+data.getFQTable();
		
		if(properties!=null){
			int props = properties.length;
			
			if(props>0)
				findSql = findSql+" AND ";
			
			for(int i=0;i<props-1;i++){
				findSql = findSql+properties[i]+"="+value[i]+" "+ops[i]+" ";
			}
			if(props>0){
				findSql = findSql+properties[props-1]+"="+value[props-1];
			}
		}
		return findSql;
		
	}*/
	
	private String getSQL(Common data, String where) throws Exception{
		
		String[][] fields = data.getProperties();
		int length = fields.length;
		StringBuffer sel = new StringBuffer();
		String[] col = null;
		
		for(int i=0;i<length;i++){
			col = fields[i];
			
			if(col.length>2)//virtual column
				sel.append(col[2]+col[0]);
			else
				sel.append(data.getTable()+"."+col[0]);
				//sel.append(col[0]);
			if(i!=length-1)
				sel.append(",");
		}
		Mapping[] mappings = data.getMappings();
		String join ="";
		if(mappings!=null){
			for(int i=0;i<mappings.length;i++){
				sel.append(","+mappings[i].getSelection());
				join = join+mappings[i].getJoin(data.getFQTable());
			}
		}
		String findSql = "SELECT "+sel.toString()+" FROM "+data.getFQTable()+join;
		findSql = findSql+" WHERE "+data.getTable()+".active=1";	
		
		/*if(values!=null&&where!=null){
			length = values.length;
			
			if(length>0)
				findSql = findSql+" AND ";
			try{
			String val = null;
			for(int i=0;i<length;i++){
				val = values[i]==null?"null":values[i];
				where = where.replaceFirst("\\?", val);
			}
			}catch(Exception ex){
				throw ex;
			}
			findSql = findSql+where;
		}*/
		if(where!=null){
			where = " AND "+where;
			findSql = findSql+where;
		}
		
		return findSql;	
	}
	
}
