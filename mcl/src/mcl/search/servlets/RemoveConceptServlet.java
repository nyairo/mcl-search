package mcl.search.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.CCollection;
import mcl.search.data.Concept;
import mcl.search.data.User;
import mcl.search.data.db.DB;
import mcl.search.data.db.Session;
import mcl.search.data.model.CCollectionModel;
import mcl.search.data.model.ConceptModel;
import mcl.search.data.model.UserModel;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

/**
 * Servlet implementation class RemoveConceptServlet
 */
public class RemoveConceptServlet extends AbstractFacesServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(RemoveConceptServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RemoveConceptServlet() {
		super();
	}
	

	
	private int removed = 0;
	
	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		//Sample collection JSON
		/**
		 {"user":"23123",
		  "name":"test",
		  "concepts":[{"source_id":1,"source":"CIEL"},{"source_id":1,"source":"PIH"},{"source_id":2,"source":"PIH"}]
		  }
		 */
		
		CCollectionModel cmodel = new CCollectionModel();
		ConceptModel conmodel =  new ConceptModel();
		removed = 0;
		try{
			JSONObject j = new JSONObject(q);
			//get user
			UserModel userM = new UserModel();
			User user = userM.user(j.getString(USER));
			//get/create collection
			CCollection collection = cmodel.collection(user.getID(),(String) j.get(NAME));
			//add concepts
			JSONArray concepts = j.getJSONArray(CONCEPTS);
			JSONObject c;
			//List<Concept> existing = (List<Concept>) collection.get(CCollection.CONCEPTS);
			
			for (int i=0;i<concepts.length();i++){
				c =  (JSONObject) concepts.get(i);
				String c_source = (String) c.get(SOURCE);
				Integer c_source_id = (Integer) c.get(SOURCE_ID);
				if(c_source!=null&&c_source_id!=null)					
					remove(collection.getID(),c_source_id, c_source,conmodel);				
			}

			collection.put(CCollection.UPDATEDBY, user.getID());
			cmodel.updateObject(collection);
			writeResponse("{"+removed+"} concepts were removed ", response);
		}catch(Exception ex){
			writeResponse(null, response);
		}
	}

	private void remove(Integer col, Integer c_source_id, String c_source, ConceptModel conmodel) throws  JSONException {
		
		Concept ncon = conmodel.getNew();		
		
				
		try {
			if(conmodel.deleteObjects(ncon, " "+Concept.COLLECTIONID+"=? AND "
					+Concept.SOURCE+"=? AND "+Concept.SOURCE_ID+"=?", new String[]{
					col.toString(),c_source_id.toString(),c_source
			}))	
				removed++;
		} catch (SQLException e) {			
			log.info(e);
		}
	}

	
	

}
