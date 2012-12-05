package mcl.search.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import mcl.search.data.Common;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFacesServlet extends HttpServlet {
    
    /**
	 * 
	 */	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AbstractFacesServlet.class);
	
	public static final String ERROR = "error";
	
	public AbstractFacesServlet() {
        super();
    }
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
    }    
    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    /** Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response); 
    }
    protected void log(FacesContext facesContext, String message) {
        facesContext.getExternalContext().log(message);
    }
    /** Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */ 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {

            FacesContextFactory contextFactory  = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY); 
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);                
        }
        return facesContext;
    }
    protected Application getApplication(FacesContext facesContext) {
        return facesContext.getApplication();        
    }
    protected Object getManagedBean(String beanName, FacesContext facesContext) {        
       
    	return getApplication(facesContext).getVariableResolver().resolveVariable(facesContext, beanName);
    }

    
    protected void writeResponse(Long id, HttpServletResponse response) throws IOException{				
		String message = ERROR;
		if(id!=null)
			message = id.toString();	
		writeResponse(response,message);
    }
    
    protected void writeResponse(HttpServletResponse response, String message) throws IOException{		
    	response.setContentType("text/plain");
		PrintWriter out = response.getWriter();		
		out.write(message);
    }

    
    protected List<?> getItems(HttpServletRequest request){
    	// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

    	// Parse the request
    	List<?> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e1) {
			log.error(e1);
		}
		return items;
    }
    
    protected void processFormField(FileItem item, Common data) {
		data.put(item.getFieldName(), item.getString());		
	}
    
    // You need an inner class to be able to call FacesContext.setCurrentInstance
    // since it's a protected method
    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }     
} 