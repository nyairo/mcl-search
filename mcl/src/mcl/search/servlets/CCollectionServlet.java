package mcl.search.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.CCollection;
import mcl.search.data.model.CCollectionModel;

import org.apache.commons.fileupload.FileItem;

/**
 * Servlet implementation class CCollectionServlet
 */
public class CCollectionServlet extends AbstractFacesServlet {
	
	private static final long serialVersionUID = 1L;
	
	//private static final Log log = LogFactory.getLog(ConsultServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CCollectionServlet() {
		super();
	}

	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
				
		CCollectionModel model = new CCollectionModel();
		CCollection collection = (CCollection) model.getNew();
		try{
			List<?> items = getItems(request); 
			Iterator<?> iter = items.iterator();    	
			while (iter.hasNext()) {
			    FileItem item = (FileItem) iter.next();
		
			    if (item.isFormField()) {
			        processFormField(item, collection);
			    }
			}    	
			collection.put(CCollection.STATUS, CCollection.STATUS_INBOX);
			collection.put(CCollection.UPDATEDBY, 0);
			writeResponse(model.insertConsult(collection), response);
		}catch(Exception ex){
			writeResponse(null, response);
		}
	}

	
	

}
