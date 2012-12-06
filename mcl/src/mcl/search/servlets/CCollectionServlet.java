package mcl.search.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.CCollection;
import mcl.search.data.User;
import mcl.search.data.model.CCollectionModel;
import mcl.search.data.model.UserModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONObject;

/**
 * Servlet implementation class CCollectionServlet
 */
public class CCollectionServlet extends AbstractFacesServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(AddConceptServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	
	
	public CCollectionServlet() {
		super();
	}

	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
		//Sample collection JSON
		/**
		 {"user":"23123",
		  "name":"test"
		  }
		 */
				
		CCollectionModel cmodel = new CCollectionModel();
		CCollection collection = null;
		try{
			JSONObject j = new JSONObject(q);
			//get user
			UserModel userM = new UserModel();
			User user = userM.user(j.getString(USER));
			//get/create collection
			collection = cmodel.getCCollection(user.getID(),(String) j.get(NAME));	
			
			writeResponse(collection.stringify(), response);
		}catch(Exception ex){
			writeResponse(null, response);
		}
	}
}
