<html>
<head>
	<title>Tolven Catalog Administration</title>
    <link href="../css/tolven.css" rel="stylesheet" type="text/css" />
    <link href="../css/screen.css" rel="stylesheet" type="text/css" />
</head>
    <body style="text-align: left">
        <div id="header">
            <!-- Begin branding -->
            <div id="branding" style="width:auto;margin:0.5em">
                <a href="http://tolven.org/index.html">
                    <img src="../images/logo_tolvenorg.gif" alt="Tolven.org home page"
                        class="home-logo" />
                </a>
                <!-- Begin nav -->
                <div id="nav">
                    <ul id="navUtility">
                        <li>
                            <a href="http://www.tolvenhealth.com">Visit TolvenHealth.com</a>
                        </li>
                    </ul>
                    <!-- End nav -->
                </div>
                <h2 style="text-align:center">V2.1 QA Catalog - Administration</h2>
                <!-- End branding -->
            </div>
            <div class="stretcher"></div>
            <!-- End header -->
        </div>
        <!-- Begin content -->
        <div id="content" style="padding: 1em 1em 1em 1em">
		<form method="post" >
			<p>List catalog contents.</p>
			<input type="hidden" name="action" value="list" >
			<input type="submit" name="Submit" value="List">
		</form>
		<form method="post" >
			<p>Delete catalog contents.</p>
			<input type="hidden" name="action" value="deleteall" >
			<input type="submit" name="Submit" value="Delete">
		</form>
		<form method="post" >
			<p>Copy the latest active plugins from the named catalog to this catalog.</p>
			<input type="hidden" name="action" value="copy" >
			<input type="submit" name="Submit" value="Copy Catalog From">
			<input type="text" name="source" value="/download/v21/catalog/plugins" size="50" >
		</form>
		<form method="post" >
			<p>Display manifest for the specified plugin.</p>
			<input type="hidden" name="action" value="manifest" >
			<input type="submit" name="Submit" value="Show Manifest for ">
			<input type="text" name="plugin" value="plugins/org.tolven.analysis-2.1.0.zip" size="50" >
		</form>
		<form method="post" >
			<p>Generate metadata (plugins.xml) for this catalog.</p>
			<input type="hidden" name="action" value="genmetadata" >
			<input type="submit" name="Submit" value="Generate Metadata">
		</form>
<?php
date_default_timezone_set("UTC");
include_once ('Plugins.class.php');
include_once ('Metadata.class.php');
include_once ('Manifest.class.php');
if ($_POST!=null) {
	// List the plugins in this catalog
	if ($_POST["action"]=="list") {
		$p = new Plugins("./plugins");
		foreach ($p->getPlugins() as $plugin) {
			echo $plugin[0] .'-' . $plugin[1] . ' ' . date ("Y-m-d H:i",$plugin[2]). "<br/>";
		}
	// Delete contents of current plugin catalog
	} else if ($_POST["action"]=="deleteall") {
		$unlinkpath = "./plugins";
		$dir_handle = @opendir($unlinkpath) or die("Unable to open $unlinkpath");
		while ($file = readdir($dir_handle)) {
			if(pathinfo($file,PATHINFO_EXTENSION)!='zip') continue;
			unlink( $unlinkpath ."/". $file);
		}
		closedir($dir_handle);
		echo "Catalog contents deleted";
	// Copy contents from another catalog to this catalog
	} else if ($_POST["action"]=="copy") {
		$p = new Plugins($_POST['source']);
		foreach ($p->getPlugins() as $plugin) {
			copy($plugin[3], "./plugins/" . $plugin[0] . '-' . $plugin[1] . '.zip');
			echo $plugin[0] .'-' . $plugin[1] . ' ' . date ("Y-m-d H:i",$plugin[2]). "<br/>";
		}
		echo "Catalog contents of " . $_POST["source"] . " copied to this catalog";
	// Display the contents of a plugin's manifest
	} else if ($_POST["action"]=="manifest") {
		$m = new Manifest($_POST['plugin']);
	// Create a new plugins.xml file
	} else if ($_POST["action"]=="genmetadata") {
		$p = new Plugins("./plugins");
		$m = new Metadata('http://tolven.org/downloadqa/v21/catalog');
		foreach ($p->getPlugins() as $plugin) {
//			echo $plugin[0] .'-' . $plugin[1] . ' ' . date ("Y-m-d H:i",$plugin[2]). "<br/>";
			$m->addVersion($plugin[0],$plugin[1]);
		}
		foreach ($p->getPlugins() as $plugin) {
			$m->addDependents($plugin[0],$plugin[1]);
		}
		$m->save();
	}
}
?>
</body>
</html>