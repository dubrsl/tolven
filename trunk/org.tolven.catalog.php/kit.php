<html>
    <head>
        <title>Tolven V2.1 Kit Download</title>
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
                <h2 style="text-align:center">Tolven V2.1 Software</h2>
                <!-- End branding -->
            </div>
            <div class="stretcher"></div>
            <!-- End header -->
        </div>
        <!-- Begin content -->
        <div id="content" style="padding: 1em 1em 1em 1em">
        <h3><a href="index.php">Catalog</a> &gt; Kit Download</h3>
		
<p>Download the tolven kit from this page.* </p>
<?  
	$kitPrefix = "tolven-V";
	$kitplugin = "org.tolven.assembler.pluginframework";
    $path = "./plugins";
    $dir_handle = @opendir($path) or die("Unable to open $path");
    // Collect plugin file names 
    $plugins = array();
    $versions = array();
	echo "<p>Temporary Log</p>";
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
		if (!is_readable ( '../kit/' . $kitFile )) {
			echo '<p>Extracting ' . $kitFile . '</p>';
			$zip = new ZipArchive;
			if ($zip->open($fileName) === TRUE) {
				$zip->extractTo('../kit',$kitFile);
				$zip->close();
			} else {
				echo 'failed';
			}
		} else {
			echo '<p>' . $kitFile . ' exists</p>';
		}
    } 
	echo "<p>End Log</p>";
    closedir($dir_handle); 
    rsort($plugins);
?>
<table>
  <thead>
    <tr>
      <th>Kit</th>
      <th>Date Published (* new)</th>
   </tr>
  </thead>
<?php
    foreach ($plugins as $plugin) {
      echo "<tr>";
        echo "<td>";
          echo "<a href=\"../kit/$plugin[4]\">$plugin[4]</a> ";
        echo "</td>";
          echo "<td>";
            echo "" . date ("Y-m-d H:i", $plugin[2]);
            // Flag less than two weeks old
           if ((time() - $plugin[2]) < 3600*24*15) {
               echo " *";
            }
        echo "</td>";
      echo "</tr>\n";
    }
 ?>
</table>
<p>*Technically, the kit is stored in the <code>org.tolven.assembler.pluginframework</code> plugin. It is extracted from that plugin and made available as a link from this page.
</p>
 	
      </div>
    </body>
</html>

