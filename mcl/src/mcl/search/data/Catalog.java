package mcl.search.data;


public class Catalog extends Common {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME 	= "Catalog";
	public static final String CATALOGID 	= "catalog_id";
	public static final String NAME 		= "name";
	
	private static final String properties[][] = {{CATALOGID,INTEGER},{NAME,STRING}};
	
	public static final String PRIMARY_KEY = CATALOGID;
	
	public static final String URLS = "urls";
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

	@Override
	public String[] getStatements() {
		// TODO Auto-generated method stub
		return null;
	}
}
