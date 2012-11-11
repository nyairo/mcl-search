package mcl.search.data;

public class User extends Common {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME 	= "User";
	
	public static final String USER_ID 		= "user_id";
	public static final String USERNAME 	= "username";
	public static final String FIRSTNAME	= "firstname";
	public static final String MIDDLENAME 	= "middlename";
	public static final String LASTNAME 	= "lastname";
	public static final String EMAIL 		= "email";
	public static final String ADMIN 		= "admin";		
	public static final String PASSWORD 	= "password";
	public static final String PHONE 		= "phone"; 
	
	public static final String PRIMARY_KEY = USER_ID;
	
	private static final String properties[][] = {{USER_ID,LONG},{USERNAME,STRING},
		{FIRSTNAME,STRING}, {MIDDLENAME,STRING}, 
		{LASTNAME,STRING}, {EMAIL,STRING}, {ADMIN,BOOLEAN}, 
		 {PASSWORD,STRING},{PHONE,STRING}};
	
	
	public static Object defaults[][] = {{ADMIN,Boolean.FALSE}};
	
	public Object[][] getDefaults(){
		return defaults;
	}
	
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
