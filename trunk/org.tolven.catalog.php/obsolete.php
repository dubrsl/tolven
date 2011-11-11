<html>
    <head>
        <title>Tolven V2.1 Obsolete plugins</title>
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
                <!-- End branding -->
            </div>
            <div class="stretcher"></div>
            <!-- End header -->
        </div>
        <!-- Begin content -->
        <div id="content" style="padding: 1em 1em 1em 1em">
<p>This page contains a list of
obsolete plug-ins. The plug-ins are still available for download but should not be used in new work. Click on the plug-in to view advice on replacement plugin-ins, if any.
</p>
    </table>
            <h3>
                Obsolete Plugins
            </h3>
            <table>
                <thead>
                    <tr>
                        <th style="border-bottom: thin black solid" title="Link to Wiki">Plugin</th>
                        <th style="border-bottom: thin black solid">Version</th>
                        <th style="border-bottom: thin black solid">Files</th>
                        <th style="text-align: right;border-bottom: thin black solid">Size</th>
                        <th style="border-bottom: thin black solid">
                            Date Published
                            <span title="New version published within 15 days">(* new)</span>
                        </th>
                    </tr>
                </thead>
<?  
    $obsolete = file('obsolete.txt',FILE_SKIP_EMPTY_LINES |FILE_IGNORE_NEW_LINES);
	foreach ($obsolete as $o) {
		$obsolete[$o] = trim($o);
	}
    $path = "./plugins";
    $dir_handle = @opendir($path) or die("Unable to open $path");
    // Collect plugin file names 
    $plugins = array();
    while ($file = readdir($dir_handle)) {
        if(pathinfo($file,PATHINFO_EXTENSION)!='zip') continue;
    $fileName = "plugins/" . $file;
    $shortNameLen = strrpos($file, "-");
    $shortName = substr($file,0,$shortNameLen);
	// If this is an obsolete plugin, then don't show it here 
	if (!in_array($shortName, $obsolete)) continue;
    $version = substr($file,
                $shortNameLen+1,strrpos($file,".")-strlen($file));
        $fileTime = filemtime($fileName);
        if (array_key_exists($shortName, $plugins)) {
            $plugin = $plugins[$shortName];
            if ($plugin[2] < $fileTime) {
               $plugins[$shortName] = 
                    array($shortName, $version, $fileTime, $fileName);
             }
        } else {
           $plugins[$shortName] = 
                    array($shortName, $version, $fileTime, $fileName);
        }
    } 
    closedir($dir_handle); 
    sort($plugins);
    foreach ($plugins as $plugin) {
      echo "<tr>";
        echo "<td><a href=\"http://wiki.tolven.org/doc/index.php/Plugin:$plugin[0]\">$plugin[0]</a></td>";
        echo "<td><a href=\"http://wiki.tolven.org/doc/index.php/Plugin:$plugin[0]#Version_$plugin[1]\">$plugin[1]</a></td>";
        echo "<td>";
          echo "<a href=\"$plugin[3]\">zip</a> ";
          echo "<a href=\"manifest.php?zip=$plugin[3]\">manifest</a> ";
          echo "<a href=\"md5.php?zip=$plugin[3]\">md5</a> ";
          echo "<a href=\"prev.php?shortName=$plugin[0]\">prev</a> ";
           echo "<a href=\"javadoc/$plugin[0]-$plugin[1]/\">javadoc</a>";
        echo "</td>";
        echo "<td style=\"text-align:right\">";
          echo "" . ceil(filesize($plugin[3])/1024) . "k";      
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
      </div>
    </body>
</html>

