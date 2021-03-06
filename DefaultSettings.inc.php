<?php

/**
 * NOTE: Must define MCL_ROOT in LocalSettings.inc.php
 */

/**
 * mcl_mode - Set the MCL_MODE to OpenMRS only so that this works immediately on install.
 *  - MCL_MODE_OPENMRS_ONLY - Use this if using MCL without the additional MCL database
 *  - MCL_MODE_ENHANCED - Use this to enable enhanced features if using the additional MCL database
 */
define(  'MCL_MODE_OPENMRS_ONLY',    1  );
define(  'MCL_MODE_ENHANCED',        2  );
$mcl_mode = MCL_MODE_OPENMRS_ONLY;

/**
 * index_mode - MCL_FULLTEXT_MODE_OFF queries concept names and concept descriptions using 
 * regular expressions, which can be very slow due to lack of indexes. MCL_FULLTEXT_MODE_ON
 * searches are much faster, but it creates 2 new tables and takes up more space. Use the
 * setup_fts.php script to enable or disable fulltext searching.
 */
define(  'MCL_FULLTEXT_MODE_OFF',    0  );
define(  'MCL_FULLTEXT_MODE_ON',     1  );

/**
 * filter_scope - Used in ConceptSearch to determine whether filters apply 
 * globally (MCL_FILTER_SCOPE_GLOBAL) or only to the text component of 
 * queries (MCL_FILTER_SCOPE_TEXT). Default is text only.
 */
define(  'MCL_FILTER_SCOPE_GLOBAL',  1  );
define(  'MCL_FILTER_SCOPE_TEXT',    2  );

/**
 * search_glue - Use in ConceptSearchTermCollection to set the relationship of
 * contained search objects. Default is 'OR'.
 */
define(  'MCL_CONCEPT_SEARCH_GLUE_AND',  'AND'  );
define(  'MCL_CONCEPT_SEARCH_GLUE_OR',   'OR'   );

/**
 * concept_list_type - Concept lists can be source from either the custom MCL list
 * table or the OpenMRS map source table. mcl_mode must be enhanced to use MCL concept lists.
 */
define(  'MCL_CLTYPE_CONCEPT_LIST',  1  );
define(  'MCL_CLTYPE_MAP_SOURCE',    2  );

/**
 * search_term_type - Used in ConceptSearchTerm to determine term type.
 */
define(  'MCL_SEARCH_TERM_TYPE_RESERVED'             ,  -100  );
define(  'MCL_SEARCH_TERM_TYPE_DEFAULT'              ,     0  );
define(  'MCL_SEARCH_TERM_TYPE_ALL'                  ,   100  );
define(  'MCL_SEARCH_TERM_TYPE_CONCEPT_ID'           ,     1  );
define(  'MCL_SEARCH_TERM_TYPE_CONCEPT_ID_RANGE'     ,     2  );
define(  'MCL_SEARCH_TERM_TYPE_MAP_CODE'             ,     3  );
define(  'MCL_SEARCH_TERM_TYPE_MAP_CODE_RANGE'       ,     4  );
define(  'MCL_SEARCH_TERM_TYPE_TEXT'                 ,     5  );	// concept name or definition
define(  'MCL_SEARCH_TERM_TYPE_CONCEPT_NAME'         ,     6  );
define(  'MCL_SEARCH_TERM_TYPE_CONCEPT_DESCRIPTION'  ,     7  );
define(  'MCL_SEARCH_TERM_TYPE_UUID'                 ,     8  );	// 36-character string
define(  'MCL_SEARCH_TERM_TYPE_LIST'                 ,     9  );
define(  'MCL_SEARCH_TERM_TYPE_MAP_SOURCE'           ,    10  );
define(  'MCL_SEARCH_TERM_TYPE_IN'                   ,    11  );
define(  'MCL_SEARCH_TERM_TYPE_NOT_IN'               ,    12  );
define(  'MCL_SEARCH_TERM_TYPE_IN_LIST'              ,    13  );
define(  'MCL_SEARCH_TERM_TYPE_NOT_IN_LIST'          ,    14  );
define(  'MCL_SEARCH_TERM_TYPE_IN_SOURCE'            ,    15  );
define(  'MCL_SEARCH_TERM_TYPE_NOT_IN_SOURCE'        ,    16  );
define(  'MCL_SEARCH_TERM_TYPE_COLLECTION'           ,    17  );
define(  'MCL_SEARCH_TERM_TYPE_FTS'                  ,    18  );

/**
 * search_term_operator_type - Determines the behavior of an operator.
 */
define(  'MCL_OPERATOR_NONE',              0  );
define(  'MCL_OPERATOR_SEARCH_TERM_TYPE',  1  );
define(  'MCL_OPERATOR_LIST',              2  );
define(  'MCL_OPERATOR_FILTER',            3  );
define(  'MCL_OPERATOR_CUSTOM',            4  );	// Custom function

/**
 * Concept dictionary constants
 */
define(  'MCL_UUID_LENGTH',  36  );

/**
 * concept source types
 */
define(  'MCL_SOURCE_TYPE_LIST'        ,  1    );
define(  'MCL_SOURCE_TYPE_DICTIONARY'  ,  2    );
define(  'MCL_SOURCE_TYPE_MAP'         ,  4    );
define(  'MCL_SOURCE_TYPE_ALL'         ,  7    );

define(  'MCL_SOURCE_DEFAULT_DICT_ID'  ,  0    );
define(  'MCL_SOURCE_SEARCH_ALL'       ,  100  );


/**
 * Used to bump sorting relevancy of exact matches.
 */
define(  'MCL_MAX_RELEVANCY'           ,  9999999  );


/**
 * MCL database connection. If MCL_MODE_OPENMRS_ONLY, then this is the
 * location of the openmrs database. If in MCL_MODE_ENHANCED, then this is
 * the connection information of the MCL database schema, which contains 
 * connection information for the other databases. 
 * These values should be set in LocalSettings.inc.php
 */
$mcl_db_host  =  '';
$mcl_db_uid   =  '';
$mcl_db_pwd   =  '';


/**
 * Default database names. Should be modified in LocalSettings.inc.php if different.
 */
$mcl_default_concept_dict_db    =  'openmrs';
$mcl_default_concept_dict_name  =  'OpenMRS';
$mcl_enhanced_db_name           =  'mcl';


// TODO: *********** retire this
/**
 * Default dictionary array. Used if not in ENHANCED mode.
 */
/*
$arr_default_dicts  =  array($mcl_default_concept_dict_db=>array(
	'db_name'    =>  $mcl_default_concept_dict_db,
	'dict_name'  =>  $mcl_default_concept_dict_name)
);
 */


// TODO ****** This should be retired for every dictionary except the default when in OPENMRS_ONLY mode 
/**
 * Array indicating the dates that the concept dictionaries were last updated.
 * Should be modified in LocalSettings.inc.php.
 */
$arr_dict_last_updated  =  array(
	'openmrs'  =>  'Jan 1, 1980',
);


/**
 * Array of default search attributes.
 */
$arr_search_attr_default = array (
	'debug'            =>  false  ,
	'verbose'          =>  false  ,
	'retired'          =>  false  ,
	'q'                =>  ''     ,
	'h'                =>  null   ,
	'slim_results'     =>  false  ,
	'class'            =>  null   ,
	'datatype'         =>  null   ,
	'use_cache'        =>  true   ,
	'source'           =>  ''     ,
	'sort'             =>  0
);

?>