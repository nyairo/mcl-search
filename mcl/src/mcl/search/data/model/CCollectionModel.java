package mcl.search.data.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import mcl.search.data.CCollection;
import mcl.search.data.Common;
import mcl.search.data.User;

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
	

	public CCollection getCCollection(Integer id, String name) {
		try {
			
			CCollection col = (CCollection) getObject(getNew(), CCollection.USERID+"=? AND "+CCollection.NAME+"=?", new String[]{id.toString(),name});
			if(col!=null)
				col.put(CCollection.CONCEPTS,new ConceptModel().getConcepts((Integer) col.get(CCollection.PRIMARY_KEY)));
			return col; 
			
		} catch (SQLException e) {
			log.error("Error retrieving collection with name/userid:" + name+"/"+id, e);
		}
		return null;
	}
	
	public CCollection collection(Integer id, String name) throws SQLException{
		CCollection col = getCCollection(id,name);
		if(col==null){
			col = (CCollection) getNew();
			col.put(CCollection.NAME, name);
			col.put(CCollection.USERID, id);
			insertCCollection(col);
		}//else{
		//	col.put(CCollection.CONCEPTS,new ConceptModel().getConcepts((Integer) col.get(CCollection.PRIMARY_KEY)));
		//}
		return col;
	}
	
	public Integer insertCCollection(CCollection collection){
		Integer id = null;
		try {
			id = insertObject(collection);
		} catch (SQLException e) {
			log.error("Error ocurred when inserting collection.",e);
		} 
		return id;
	}	

	public CCollection getCCollection(String collection_id){		
		try {
			return (CCollection) getObject(getNew(),CCollection.PRIMARY_KEY+"=?", new String[]{collection_id});
		} catch (SQLException e) {
			log.error("Error retrieving collection with collection_id:"+collection_id,e);
		}
		return null;
	}	
	
	public List<CCollection> getCCollections() throws SQLException {
		return (List<CCollection>) getObjects(new CCollection());
	}
	
	public void updateCCollection(){
		
	}
	
	public boolean deleteCCollection(CCollection collection) throws SQLException{
		return deleteObject(collection,CCollection.PRIMARY_KEY);
	}

	public List<CCollection> getCCollections(String where, String[] values) throws SQLException {
		 
		return (List<CCollection>) getObjects(new CCollection(), where, values);
	}

	/*@SuppressWarnings("unchecked")
	public List<Message> getMessages(String device_id, String receipt) throws SQLException{
		
		return (List<Message>) getObjects(new Message(), 
				CCollection.TABLE_NAME+"."+CCollection.DEVICE_ID+"=? AND "
				+CCollection.TABLE_NAME+"."+CCollection.STATUS+"=? AND "
				+CCollection.TABLE_NAME+"."+CCollection.UID+" NOT IN ("+receipt+") ",
				new String[]{device_id,CCollection.STATUS_OUTBOX});
				
	}*/
}
