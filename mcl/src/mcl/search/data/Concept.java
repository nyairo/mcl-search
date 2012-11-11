package mcl.search.data;

public class Concept extends Common {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final String TABLE_NAME 	= "Concept";
	
	public static final String CONCEPTID 		= "user_id";
	public static final String COLLECTIONID 	= "collection_id";
	public static final String SOURCE 		= "source"; 

	private static final String properties[][] = {{CONCEPTID,LONG},{SOURCE,STRING},
		{COLLECTIONID,LONG}, {SOURCE,STRING}};
	
	public static final String PRIMARY_KEY = CONCEPTID;
		
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

	public static String appendTable(String prop){
		return User.TABLE_NAME+"."+prop;
	}


}
