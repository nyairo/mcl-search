package mcl.search.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.db.DB;

public class CreateDB extends AbstractFacesServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SCHEMA_NAME = "schema";
	public static final String USER = "user";
	public static final String PWORD = "password";
	public static final String SUSER = "suser";
	public static final String SPWORD = "spassword";

	public CreateDB(){
		super();
	}
	
	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//get parameters
		//create db
		String user = request.getParameter(USER);
		String passwd = request.getParameter(PWORD);
		String suser = request.getParameter(SUSER);
		String spassword = request.getParameter(SPWORD);
		String schema = request.getParameter(SCHEMA_NAME);
		
		String result = DB.createDB(schema, user,passwd, suser, spassword);
		
		response.setContentType("text/plain");

		PrintWriter out = response.getWriter();

		out.write(result);
	}

}
