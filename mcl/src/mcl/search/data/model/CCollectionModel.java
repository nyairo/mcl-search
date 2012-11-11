package mcl.search.data.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import mcl.search.data.CCollection;
import mcl.search.data.Common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author nyairo
 *
 */
public class CCollectionModel extends BaseModel  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CCollectionModel.class);
	
	@Override
	public Common getNew() {
		
		CCollection cons = new CCollection();
		
		return cons.initialiaze();
	}
	
	public Long insertConsult(CCollection consult){
		Long id = null;
		try {
			id = insertObject(consult);
		} catch (SQLException e) {
			log.error("Error ocurred when inserting consult.",e);
		} 
		return id;
	}	

	public CCollection getConsult(String consult_id){		
		try {
			return (CCollection) getObject(getNew(),CCollection.PRIMARY_KEY+"=?", new String[]{consult_id});
		} catch (SQLException e) {
			log.error("Error retrieving consult with consult_id:"+consult_id,e);
		}
		return null;
	}	
	
	public List<CCollection> getConsults() throws SQLException {
		return (List<CCollection>) getObjects(new CCollection());
	}
	
	public void updateConsult(){
		
	}
	
	public boolean deleteConsult(CCollection consult) throws SQLException{
		return deleteObject(consult,CCollection.PRIMARY_KEY);
	}

	public List<CCollection> getConsults(String where, String[] values) throws SQLException {
		 
		return (List<CCollection>) getObjects(new CCollection(), where, values);
	}
	
	/*@SuppressWarnings("unchecked")
	public List<Message> getMessages(String device_id, String receipt) throws SQLException{
		
		return (List<Message>) getObjects(new Message(), 
				Consult.TABLE_NAME+"."+Consult.DEVICE_ID+"=? AND "
				+Consult.TABLE_NAME+"."+Consult.STATUS+"=? AND "
				+Consult.TABLE_NAME+"."+Consult.UID+" NOT IN ("+receipt+") ",
				new String[]{device_id,Consult.STATUS_OUTBOX});
				
	}*/
}
