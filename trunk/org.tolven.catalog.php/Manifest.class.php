<?php
class Manifest {

	private $document;
	public $requires;
	private $id;
	private $version;
	
	// The plugin zip file. We will open and extract the manifest from the zip file
	function __construct ( $file ) {
		$this->extractManifest($file);
		$this->document = new DOMDocument( );
		$this->document->load('tmp/tolven-plugin.xml');
//		echo "loaded " . $file . "<br/>";
//		echo "<pre> " . $this->document->saveXML() . "</pre>";
		$xpath = new DOMXPath($this->document);
		$xpath->registerNamespace('m', "urn:tolven-org:tpf:1.0");
		$idNode = $xpath->query("@id");
		$this->id = $idNode->item(0)->nodeValue;
		$versionNode = $xpath->query("@version");
		$this->version = $versionNode->item(0)->nodeValue;
//		echo "id: " . $this->id . "-" . $this->version . "<br/>";
		if (!$this->requires = $xpath->query("m:requires/m:import/@plugin-id")) {
			echo "Invalid xpath query<br/>";
			return;
		}
//		foreach ($this->requires as $r) {
//			echo "Require " . $r->nodeName .'='. $r->nodeValue . "<br/>";
//		}
	}
	function extractManifest($file) {
		$zip = new ZipArchive;
		if ($zip->open($file) === TRUE) {
			$zip->extractTo('tmp','tolven-plugin.xml');
			$zip->close();
		} else {
			echo 'failed';
		}
			
	}
}	
?>