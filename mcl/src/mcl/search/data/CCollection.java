package mcl.search.data;

import java.io.Serializable;

public class CCollection extends Common implements Serializable {

	/**
	 * 
	 */	
	private static final long serialVersionUID = 1L;
	
	public static final String PRIMARY_KEY 	= "collection_id";	
	public static final String NAME	= "name";
	public static final String USERID	= "user_id";	
	public static final String PUBLIC		= "public";
	public static final String UPDATEDBY	= "updatedby";
	
	
	
	public static final String properties[][] = {{PRIMARY_KEY,INTEGER},{NAME,STRING},
		{USERID,STRING},{PUBLIC,STRING},{UPDATEDBY,DATE}};	

	public static final String TABLE_NAME = "Collection";

	public static final String CONCEPTS = "concepts";
	
	
	private Mapping[] mappings = new Mapping[]{
			//new Mapping(new String[]{"ufirst","ulast"},User.TABLE_NAME+"."+User.FIRSTNAME+" as ufirst, "+User.TABLE_NAME+"."+User.LASTNAME+" as ulast ",Consult.PROVIDER,User.TABLE_NAME,User.USERNAME)
			};
	
	@Override
	public String getTable() {
		
		return TABLE_NAME;
	}
	
	@Override
	public String[] getChildren(){
		return new String[]{CONCEPTS};
	}
	
	@Override
	public Mapping[] getMappings(){
		return mappings;		
	}

	/**
	 * Get the array of properties of this class and any superclass 
	 */
	@Override
	public String[][] getProperties() {
		
		return concat(properties,super.getProperties());
	}
	

	public CCollection getNew(){
		CCollection collection = new CCollection();
		return (CCollection) collection.initialiaze();
	}

	@Override
	public String getPrimaryKey() {
		
		return PRIMARY_KEY;
	}

	@Override
	public String[] getStatements() {
		
		return null;
	}
	
}

