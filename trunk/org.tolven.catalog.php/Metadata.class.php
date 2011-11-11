<?php
include_once ('Manifest.class.php');
class Metadata {

	private $urlPrefix;
	private $document;
	private $plugin;
	
	function __construct ( $urlPrefix )
	{
		$this->urlPrefix = $urlPrefix;
		$plugin_array = array();
		$this->document = new DOMDocument( '1.0','UTF-8');
		$this->document->preserveWhiteSpace = false;
		$this->document->formatOutput   = true;
		$this->plugins = $this->document->createElementNS( 'urn:tolven-org:plugins:1.0', 'plugins' );
		$info = $this->document->createElement( 'info', ' ' );
		$ts = $this->document->createElement( 'timestamp', date ("YmdHis") );
		$info->appendChild($ts);
		$this->plugins->appendChild($info);
		$this->document->appendChild( $this->plugins );
	}
	
	// This adds only the plugin itself if it doesn't exist. In any case, it returns the DomNode for the specified plugin.
	function addPlugin($shortName) {
		$xpath = new DOMXPath($this->document);
		$xpath->registerNamespace('p', "urn:tolven-org:plugins:1.0");
		$query = 'plugin[@id="' . $shortName .'"]';
		$idNode = $xpath->query($query);
		if ($idNode==false) {
			echo "invalid XPath<br/>";
		}
		if ($idNode->length==0) {
			$plugin = $this->document->createElement( 'plugin' );
			$id = $this->document->createAttribute( 'id');
			$id->value = $shortName;
			$plugin->appendChild( $id );
			$this->plugins->appendChild($plugin);
			return $plugin;
		} else {
			return $idNode->item(0);
		}
	}
	
	// Add a version to a plugin
	function addVersion($shortName, $version) {
		$plugin = $this->addPlugin( $shortName );
		$versionElement = $this->document->createElement( 'version' );
		$id = $this->document->createAttribute( 'id');
		$id->value = $version;
		$versionElement->appendChild( $id );
		$file = 'plugins/' . $shortName . '-' . $version . '.zip';
		$uri = $this->document->createElement('uri', $this->urlPrefix . '/' . $file);
		$versionElement->appendChild( $uri );
		$md5 = $this->document->createElement('messageDigest');
		$mdType = $this->document->createAttribute( 'type');
		$mdType->value = "md5";
		$md5->appendChild( $mdType );
		$md5Value = $this->document->createElement('value', md5_file($file));
		$md5->appendChild( $md5Value );
		$versionElement->appendChild( $md5 );
		$plugin->appendChild( $versionElement );
	}
	
	// Add one dependent to a plugin. Note: the plugin that we depend on may not actually exist in this catalog.
	function addDependent( $shortName, $id, $version ) {
		$plugin = $this->addPlugin( $shortName );
		$dependentElement = $this->document->createElement( 'dependent' );
		$idNode = $this->document->createAttribute( 'id');
		$idNode->value = $id;
		$versionNode = $this->document->createAttribute( 'version');
		$versionNode->value = $version;
		$dependentElement->appendChild( $idNode );
		$dependentElement->appendChild( $versionNode );
		$plugin->appendChild( $dependentElement );
	}

	// Add dependents is done as a second step. We must open the manifest for a plugin and ourself as a dependent to all plugins we depend on.
	function addDependents( $shortName, $version ) {
		$file = 'plugins/' . $shortName . '-' . $version . '.zip';
		$pluginName = $shortName . '-' . $version;
		$m = new Manifest($file);
		foreach ($m->requires as $r) {
			$this->addDependent($r->nodeValue, $shortName, $version); 
		}
	}
	
	// Save this plugins.xml to a file
	function save() {
		$this->document->save("plugins.xml");
	}
}
?>