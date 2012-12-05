package mcl.search.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mcl.search.data.User;
import mcl.search.data.model.UserModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class Login
 */
public class Login extends AbstractFacesServlet {

	private static final long serialVersionUID = 1L;
	public static final String authenticated = "authenticated";
	public static final String authenticatedUser = "authenticatedUser";
	public static final String user = "user";
	public static final String password = "password";
	private static final Log log = LogFactory.getLog(Login.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	@Override
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter(user);
		String passwd = request.getParameter(password); 
		String remote = request.getRemoteAddr();
		
		log.info("Login attempt in user:" + username + ":Password:******:from"
				+ remote);

		String message = "";
		User user = null;
		boolean auth = false;
		if (username != null && passwd != null) {

			auth = new UserModel().login(username, passwd);
		}

		if (auth) {
/*
			LoggedInUserBean loggedInUserBean = (LoggedInUserBean) getManagedBean(
					"loggedInUserBean", getFacesContext(request, response));
			loggedInUserBean.setUsername(username);
			loggedInUserBean.setPassword(passwd);
			loggedInUserBean.setValid("true");
			LoggedInUserBean.setLoggedin(true);
*/
			request.getSession().setAttribute(authenticated, true);
			request.getSession().setAttribute(authenticatedUser, user);
			message = authenticated;
			log.info("User authenticated");
		} else {
			request.getSession().setAttribute(authenticated, false);
			request.getSession().setAttribute(authenticatedUser, null);
			message = "Not " + authenticated;
			log.info("User not authenticated");
		}

		/*
		 * Set the content type(MIME Type) of the response.
		 */

		response.setContentType("text/plain");

		PrintWriter out = response.getWriter();

		out.write(message);
		if (!auth) 
			request.getSession().invalidate();

	}

}
