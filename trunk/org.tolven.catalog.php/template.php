<?php  
	// Extract template plugins.xml
	$kitPrefix = "tolven-V";
	$kitplugin = "org.tolven.assembler.pluginframework";
    $path = "./plugins";
    $dir_handle = @opendir($path) or die("Unable to open $path");
    // Collect plugin file names 
    $plugins = array();
    $versions = array();
    while ($file = readdir($dir_handle)) {
        if(pathinfo($file,PATHINFO_EXTENSION)!='zip') continue;
        $fileName = "plugins/" . $file;
		$shortNameLen = strrpos($file, "-");
		$shortName = substr($file,0,$shortNameLen);
		if ($shortName!=$kitplugin) continue;
		$version = substr($file,$shortNameLen+1,strrpos($file,".")-strlen($file));
        $versionKey = "";
        $vs = explode(".", $version);
        foreach ($vs as $v) {
           $versionKey += str_pad($v, 10, "0", STR_PAD_LEFT);
        }
        $fileTime = filemtime($fileName);
		// When possible, make sure the kit has been extracted. Don't extract more than once though. Put it in kit/ sub-folder.
		$kitFile =  $kitPrefix . $version . ".zip";
        $plugins[$versionKey] = array($versionKey, $version, $fileTime, $fileName, $kitFile);
    } 
    closedir($dir_handle); 
    rsort($plugins);
	$p = $plugins[0];
	echo 'File: ' .$p[3];
	$zip = new ZipArchive;
		if ($zip->open($fileName) === TRUE) {
			$zip->extractTo('../kit',$kitFile);
			$zip->close();
		} else {
			echo 'failed';
		}
	} else {
		echo '<p>' . $kitFile . ' exists</p>';
		header('Content-Description: File Transfer');
		header('Content-Type: application/octet-stream');
		header('Content-Disposition: attachment; filename='.basename($kitFile));
		header('Content-Transfer-Encoding: binary');
		header('Expires: 0');
		header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
		header('Pragma: public');
		header('Content-Length: ' . filesize($kitFile));
    ob_clean();
    flush();
    readfile($file);
	}
?>
