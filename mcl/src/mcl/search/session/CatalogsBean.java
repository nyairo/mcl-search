package mcl.search.session;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import mcl.search.data.Catalog;
import mcl.search.data.model.CatalogModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@ManagedBean(name="catalogsBean")
@SessionScoped
public class CatalogsBean implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CatalogsBean.class);

	private List<Catalog> catalogs = null;
	//private List<CatalogUrl> urls = new ArrayList<CatalogUrl>();
		
	public CatalogsBean(){
		//load();	
	}

	public void setCatalogs(List<Catalog> catalogs) {
		this.catalogs = catalogs;
	}

	public List<Catalog> getCatalogs() {
		//if(catalogs==null)
			load();	
		return catalogs;
	}

	@SuppressWarnings("unchecked")
	private void load() {
		//load catalogs
		log.debug("Loading catalogs");
		CatalogModel model = new CatalogModel();
		
		try {
			setCatalogs((List<Catalog>) model.getObjects(new Catalog()));
			//if(getCatalogs()!=null)
				//for(Catalog c: getCatalogs()){					
					//setUrls(c.getUrls());
				//}
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
	}
/*
	public void setUrls(List<CatalogUrl> urls) {
		for(CatalogUrl u: urls){
			u.setNames("XXXXX");
		}
		this.urls.addAll(urls);
	}

	public List<CatalogUrl> getUrls() {
		return urls;
	}	
*/	
}

