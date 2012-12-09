package mcl.search.servlets;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.CCollection;
import mcl.search.data.Concept;
import mcl.search.data.User;
import mcl.search.data.model.CCollectionModel;
import mcl.search.data.model.ConceptModel;
import mcl.search.data.model.UserModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

/**
 * Servlet implementation class DeleteCollectionServlet
 */
public class DeleteCollectionServlet extends AbstractFacesServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(DeleteCollectionServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteCollectionServlet() {
		super();
	}
		
	private int removed = 0;
	
	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	
		//Sample collection JSON
		/**
		 {"user":"23123",
		  "name":"test"		
		  }
		 */
		
		CCollectionModel colmodel = new CCollectionModel();
		ConceptModel conmodel =  new ConceptModel();
		removed = 0;
		try{
			JSONObject j = new JSONObject(q);
			//get user
			UserModel userM = new UserModel();
			User user = userM.user(j.getString(USER));
			//get/create collection
			CCollection collection = colmodel.collection(user.getID(),(String) j.get(NAME));			
			//remove(collection.getID(), conmodel);		
			//collection.put(CCollection.UPDATEDBY, user.getID());
			removed = colmodel.deleteCCollection(collection);
			writeResponse("{"+removed+"} collection(s) were removed ", response);
		}catch(Exception ex){
			writeResponse(null, response);
		}
	}

	private void remove(Integer col, ConceptModel conmodel) throws  JSONException {
		
		Concept ncon = conmodel.getNew();		
						
		try {
			removed = removed+conmodel.deleteObjects(ncon, " "+Concept.COLLECTIONID+"=? "
					, new String[]{
					col.toString()});
				
				
		} catch (SQLException e) {			
			log.info(e);
		}
	}

	
	

}
