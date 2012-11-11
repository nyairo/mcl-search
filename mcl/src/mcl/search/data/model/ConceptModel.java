package mcl.search.data.model;

import java.sql.SQLException;
import java.util.List;

import mcl.search.data.Concept;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConceptModel extends BaseModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ConceptModel.class);

	public Long insertConcept(Concept con) throws SQLException {

		return insertObject(con);
	}

	public Concept getConcept(String conceptid) {
		try {
			return (Concept) getObject(getNew(), Concept.CONCEPTID+"=?", new String[]{conceptid});
		} catch (SQLException e) {
			log.error("Error retrieving Concept with conceptid:" + conceptid, e);
		}
		return null;
	}

	public Concept getConcept(String where, String[] values) {
		try {
			return (Concept) getObject(getNew(), where, values);
		} catch (SQLException e) {
			log.error("Error retrieving con", e);
		}
		return null;
	}

	public Concept getNew() {
		Concept con = new Concept();

		return (Concept) con.initialiaze();
	}

	public void updateConcept() {

	}

	public List<Concept> getConcepts(String collectionid) throws SQLException {
		return (List<Concept>) getObjects(new Concept(),Concept.COLLECTIONID+"!=?", new String[]{collectionid});
	}

	public boolean deleteConcept(Concept con) throws SQLException {
		return deleteObject(con, con.getPrimaryKey());
	}
}
