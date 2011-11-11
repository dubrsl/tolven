<?php
class Plugins {

	private $plugins;
	private $path;
	private $obsolete;
	
   function __construct($path) {
		$this->plugins = array();
		$this->path = $path;
		$this->loadObsolete();
		$this->loadPlugins();
	}
	
	function loadObsolete() {
		$this->obsolete = file('obsolete.txt',FILE_SKIP_EMPTY_LINES |FILE_IGNORE_NEW_LINES);
		foreach ($this->obsolete as $o) {
			$this->obsolete[$o] = trim($o);
		}
	}
	
	function loadPlugins() {
		$dir_handle = @opendir($this->path) or die("Unable to open $this->path");
	// Collect plugin file names 
		while ($file = readdir($dir_handle)) {
			if(pathinfo($file,PATHINFO_EXTENSION)!='zip') continue;
			$fileName = $this->path . "/" . $file;
			$shortNameLen = strrpos($file, "-");
			$shortName = substr($file,0,$shortNameLen);
			// If this is an obsolete plugin, then don't show it here 
			if (in_array($shortName, $this->obsolete)) continue;
			$version = substr($file,
						$shortNameLen+1,strrpos($file,".")-strlen($file));
			$fileTime = filemtime($fileName);
			if (array_key_exists($shortName, $this->plugins)) {
				$plugin = $this->plugins[$shortName];
				if ($plugin[2] < $fileTime) {
				   $this->plugins[$shortName] = 
						array($shortName, $version, $fileTime, $fileName);
				 }
			} else {
			   $this->plugins[$shortName] = 
						array($shortName, $version, $fileTime, $fileName);
			}
		} 
		closedir($dir_handle); 
		sort($this->plugins);
	}
	function getPlugins() {
		return $this->plugins;
	}
}

	
?>