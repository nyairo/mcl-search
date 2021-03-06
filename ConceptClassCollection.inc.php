<?php

require_once(MCL_ROOT . 'Collection.inc.php');
require_once(MCL_ROOT . 'ConceptClass.inc.php');

class ConceptClassCollection extends Collection
{
	private $css_dict = null;

	public function merge(ConceptClassCollection $ccc)
	{
		foreach ($ccc->getKeys() as $key) {
			$this->Add($key, $ccc->Get($key));
		}
	}
	public function setDefaultDictionary(ConceptSearchSource $css_dict)
	{
		$this->css_dict = $css_dict;
	}

	/**
	 * Selector is a string key or an array of keys. Keys may be in one of these formats:
	 * 
	 * Fully specified Integer:     <dictionary_name>:class(<concept_class_id>)
	 * Fully specified name:		<dictionary_name>:class(<concept_class_name>)
	 * Default dictionary:  		<concept_class_id>
	 * Only name:					<concept_class_name>
	 * 
	 * <concept_class_name> must be quoted if it contains non-word characters.
	 * Note that setDefaultDictionary() must be called if using the default dictionary format.
	 */
	public function getClasses($selector)
	{
		$ccc = new ConceptClassCollection();
		if (!is_array($selector)) $selector = array($selector);
		foreach ($selector as $key)
		{
			$key = strtolower(trim($key));

			// <dictionary_name>:class(<concept_class_id>) OR <dictionary_name>:class(<concept_class_name>)
			if (  (  strpos(  $key  ,  ':class('  ,  0               )  !== false  )  &&  
				  (  strpos(  $key  ,  ')'        ,  strlen($key)-1  )  !== false  )  ) 
			{
				// Parse out dict_name and class_identifier
				$dict_name = trim(  substr($key, 0, strpos($key, ':class')  )  );
				$class_identifier = trim(  substr(
						$key, 
						strlen($dict_name) + strlen(':class('),
						strlen($key) - strlen($dict_name) - strlen(':class(') - 1
					)  );
				if (substr($class_identifier, 0, 1) == '"' && 
					substr($class_identifier, strlen($class_identifier) - 1, 1) == '"')
				{
					$class_identifier = trim(substr($class_identifier, 1, strlen($class_identifier) - 2));
				}

				// <dictionary_name>:class(<concept_class_id>)
				if ($this->isInteger($class_identifier)) {
					$new_key = $dict_name . ':class(' . $class_identifier . ')';
						if ($this->IsMember($new_key)) $ccc->Add($new_key, $this->Get($new_key));
				} 

				// <dictionary_name>:class(<concept_class_name>)
				else
				{
					foreach ($this->getKeys() as $class_key) {
						$class = $this->Get($class_key);
						if (  (  strtolower($class->name)  ==  $class_identifier   )  && 
							  (  strtolower($class->getSourceDictionary()->dict_db)  ==  $dict_name  )  ) 
						{
							$ccc->Add($class_key, $class);
							break;
						}
					}
				}

			}

			// <concept_class_id> OR <concept_class_name>
			else 
			{
				// <concept_class_id> - this uses the default dictionary setting
				if (  $this->isInteger($key)  )
				{
					// Default dictionary key
					if (!$this->css_dict) {
						trigger_error('Default dictionary must be set in ConceptClassCollection if using default dictionary format', E_USER_ERROR);
					}
					$key = $this->css_dict->dict_db . ':class(' . $key . ')';
					if ($this->IsMember($key)) $ccc->Add($key, $this->Get($key));
				}

				// <concept_class_name> - a single term canmatch multiple dictionaries
				else
				{
					foreach ($this->getKeys() as $class_key) {
						$class = $this->Get($class_key);
						if (  (  strtolower($class->name)  ==  $key   )  ) 
						{
							$ccc->Add($class_key, $class);
						}
					}
				}
			}
		}
		return $ccc;
	}

	/**
	 * Get an array of all concept classes formatted for an HTML select dropdown.
	 * This method automatically combines classes of the same name across dictionaries
	 * into a single select box. Optionally send a collection of selected classes.
	 * @param ConceptClassCollection $coll_selected
	 * @return array
	 */
	public function getHtmlChecklistArray(ConceptClassCollection $coll_selected = null)
	{
		/**
		 * $arr_display has 3 fields: value, display, source. Source is another array of 
		 * dictionary sources in which the object is present.
		 */
		$arr_display = array();

		// Combine 
		foreach ($this->getKeys() as $key) 
		{
			$o = $this->Get($key);
			if (  !isset($arr_display[strtolower($o->name)])  ) {
				$arr_display[strtolower($o->name)] = array(
						'value'     =>  strtolower($o->name)  ,
						'display'   =>  $o->name              ,
						'hint'      =>  ''                    ,
						'selected'  =>  false                 ,
						'source'    =>  array()
					);
			}
			if (  ($arr_display[strtolower($o->name)]['selected'] === false)  &&
				  ($coll_selected->IsMember($key))                            )
			{
				$arr_display[strtolower($o->name)]['selected'] = true;
			}
			$arr_display[strtolower($o->name)]['source'][] = $o->getSourceDictionary()->dict_db;
		}

		// Set hint column to indicate the list of dictionaries
		foreach (array_keys($arr_display) as $key) {
			$hint = '';
			foreach ($arr_display[$key]['source'] as $source_text) {
				if ($hint) $hint .= ', ';
				$hint .= $source_text;
			}
			if ($hint) $hint = '"' . $arr_display[$key]['display'] . '" is in the following dictionaries: ' . $hint;
			$arr_display[$key]['hint'] = $hint;
		}

		return $arr_display;
	}

	private function isInteger($s)
	{
		if (  is_numeric($s)  &&  ((int)$s) == $s  ) {
			return true;
		}
		return false;
	} 
}

?>