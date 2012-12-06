package mcl.search.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.json.JSONObject;

import mcl.search.data.db.DB;

public class CreateDB extends AbstractFacesServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	public CreateDB(){
		super();
	}
	
	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//get parameters
		//create db
		
		
		//http://localhost:8080/mcl/faces/createdb?g
		//schema=mclcc&user=ccuser&password=password&suser=root&spassword=password
		/**
		 {schema:"mclcc",user:"ccuser",password:"password",suser:"root",spassword:"password"}
		 */
		String user = request.getParameter(USER);
		String passwd = request.getParameter(PWORD);
		String suser = request.getParameter(SUSER);
		String spassword = request.getParameter(SPWORD);
		String schema = request.getParameter(SCHEMA_NAME);
		try{
			JSONObject j = new JSONObject(q);
			
			user = (String) j.get(USER);
			passwd = (String) j.get(PWORD);
			suser = (String) j.get(SUSER);
			spassword = (String) j.get(SPWORD);
			schema = (String) j.get(SCHEMA_NAME);
						
			writeResponse(DB.createDB(schema, user,passwd, suser, spassword), response);
		}catch(Exception ex){
			writeResponse(null, response);
		}		
	}

}
