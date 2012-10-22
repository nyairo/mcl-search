<?php

/**
 * Contains a list of concept IDs and a ConceptListDefinition.
 */
class ConceptList
{
	/**
	 * Object that implements the iConceptListDefinition interface
	 */
	public $cld = null;

	/**
	 * Array of concept ids grouped by dictionary ID.
	 *    [dict_id][concept_id] = <concept_id>
	 */
	private $arr_concept_id = null;


	/**
	 * Constructor
	 */
	public function __construct($cld) {
		$this->cld             =  $cld;
		$this->arr_concept_id  =  array();
	}

	/**
	 * Returns the ConceptListDefinition object.
	 */
	public function getDefinition() {
		return $this->cld;
	}

	/**
	 * Add a concept_id to the list
	 */
	public function addConcept($dict_db, $concept_id) {
		$this->arr_concept_id[$dict_db][$concept_id]  =  $concept_id;
	}

	/**
	 * True if the passed concept_id is a member of this list.
	 */
	public function isMember($dict_db, $concept_id) {
		return isset($this->arr_concept_id[$dict_db][$concept_id]);
	}

	/**
	 * Returns the entire array of concept ids.
	 */
	public function getArray() {
		return $this->arr_concept_id;
	}

	/**
	 * Returns a comma-separated string of dict_db:concept_id.
	 */
	public function getCsv() {
		$csv = '';
		foreach (array_keys($this->arr_concept_id) as $dict_db) {
			$csv .= $dict_db . ':' . implode(',' . $dict_db . ':', $this->arr_concept_id[$dict_db]);
		}
		return $csv;
	}

	/**
	 * Returns the number of concept_ids in the list.
	 */
	public function getCount() {
		$c = 0;
		foreach (array_keys($this->arr_concept_id) as $dict_db) {
			$c += count($this->arr_concept_id[$dict_db]);
		}
		return $c;
	}
}