package mcl.search.data.model;

import java.sql.SQLException;
import java.util.List;

import mcl.search.data.Catalog;
import mcl.search.data.CatalogUrl;
import mcl.search.data.Common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CatalogModel extends BaseModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CatalogModel.class);

	public Long insertCatalog(Catalog con) throws SQLException {
		long id =  insertObject(con);
		List<CatalogUrl> urls = (List<CatalogUrl>) con.get(Catalog.URLS); 
		if(urls!=null)
			for(CatalogUrl url: urls){
				if(url.get(CatalogUrl.CATALOGID)==null){
					url.put(CatalogUrl.CATALOGID,id);
					insertObject(url);
				}
			}
		return id;
	}

	public List<?> getObjects(Common data) throws SQLException {
		List<Catalog> cats = null;
		try{	
			cats = (List<Catalog>) super.getObjects(new Catalog());
			if(cats!=null)
			for(Catalog c: cats){
				c.put(Catalog.URLS,getCatalogURLs(((Integer) c.get(Catalog.CATALOGID)).toString()));			
			}
			} catch (SQLException e) {
				e.printStackTrace();
				log.debug(e.getMessage());
			}
	return cats;
		
 }
	
	public Catalog getCatalog(String Catalogid) {
		try {
			return (Catalog) getObject(getNew(), Catalog.CATALOGID+"=?", new String[]{Catalogid});
		} catch (SQLException e) {
			log.error("Error retrieving Catalog with Catalogid:" + Catalogid, e);
		}
		return null;
	}

	public Catalog getCatalog(String where, String[] values) {
		try {
			return (Catalog) getObject(getNew(), where, values);
		} catch (SQLException e) {
			log.error("Error retrieving con", e);
		}
		return null;
	}

	public Catalog getNew() {
		Catalog con = new Catalog();

		return (Catalog) con.initialiaze();
	}
	
	public CatalogUrl getNewUrl() {
		CatalogUrl con = new CatalogUrl();

		return (CatalogUrl) con.initialiaze();
	}

	public void updateCatalog() {

	}

	public List<Catalog> getCatalogs(String catalogid) throws SQLException {
		return (List<Catalog>) getObjects(new Catalog(),Catalog.CATALOGID+"=?", new String[]{catalogid});
	}
	
	public List<CatalogUrl> getCatalogURLs(String catalogid) throws SQLException {
		return (List<CatalogUrl>) getObjects(new CatalogUrl(),CatalogUrl.CATALOGID+"=?", new String[]{catalogid});
	}

	public int deleteCatalog(Catalog con) throws SQLException {
		return deleteObject(con, con.getPrimaryKey());
	}
}
