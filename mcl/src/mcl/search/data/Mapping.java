package mcl.search.data;

import java.io.Serializable;

public class Mapping implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Property whose value we seek to register in the map
	 */
	private String[] properties;
	
	/**
	 * Our mapped property
	 */
	private String mapped;
	/**
	 * The table the values it to be found.
	 */
	private String table;
	/**
	 * The column on the foreign table that maps our table.
	 */
	private String mappedBy;
	
	private String selection;
	
	/**
	 * 
	 * @param properties
	 * @param selection
	 * @param mapped
	 * @param table
	 * @param mappedBy
	 */
	public Mapping(String[] properties, String selection, String mapped, String table,String mappedBy){
		this.properties = properties;
		this.selection = selection;
		this.mapped = mapped;
		this.table = table;
		this.mappedBy = mappedBy;		
	}
	
	public String getJoin(String ltable){
		return " JOIN "+table+" ON "+ltable+"."+mapped+" = "+table+"."+mappedBy+" ";
	}
	
	
	public void setProperties(String[] properties) {
		this.properties = properties;
	}

	public String[] getProperties() {
		return properties;
	}
	
	public void setTable(String table) {
		this.table = table;
	}
	public String getTable() {
		return table;
	}
	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}
	public String getMappedBy() {
		return mappedBy;
	}	

	public void setMapped(String mapped) {
		this.mapped = mapped;
	}

	public String getMapped() {
		return mapped;
	}

	/*public String getSelection() {
		StringBuffer sel = new StringBuffer();
		int length = properties.length;
		for(int i=0;i<length;i++){
			sel.append(table+"."+properties[i]);
			//sel.append(properties[i]);
			if(i!=length-1)
				sel.append(",");
		}
		return sel.toString();
	}*/
	public String getSelection() {
	
		return this.selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}	
}
	