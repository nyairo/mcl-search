package mcl.search.data;

public class CatalogUrl extends Common {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME 	= "Catalogurl";
	public static final String CATALOGURLID 	= "catalogurl_id";
	public static final String CATALOGID 	= "catalog_id";
	public static final String URL 		= "url";
	public static final String DESCRIPTION 		= "description";
	
	private static final String properties[][] = {{CATALOGURLID,LONG},{CATALOGID,LONG},{URL,STRING},{DESCRIPTION,STRING}};
	
	public static final String PRIMARY_KEY = CATALOGURLID;
	
	private String names = null;
	/**
	 * Get the array of properties of this class and any superclass 
	 */
	@Override
	public String[][] getProperties() {
		
		return concat(properties,super.getProperties());
	}
	
	@Override
	public String getTable() {
		
		return TABLE_NAME;
	}

	@Override
	public String getPrimaryKey() {
		
		return PRIMARY_KEY;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getNames() {
		return names;
	}

}
