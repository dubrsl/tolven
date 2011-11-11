<html>
    <head>
        <title>Tolven V2.1 Software Downloads</title>
        <link href="../css/tolven.css" rel="stylesheet" type="text/css" />
        <link href="../css/screen.css" rel="stylesheet" type="text/css" />
    </head>
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
	if (in_array($shortName, $obsolete)) continue;
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
?>
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
<p>This page contains a list of
all plug-ins applicable to this release of Tolven. Normally,
you do not need to download individual plug-ins. The
plug-ins are downloaded automatically when you run the config-tolven
script. The two exceptions to this are:</p>
<ul>
<li>The 
<?php
   $installerPlugin = "";
   foreach ($plugins as $plugin) {
    if ($plugin[0]=="org.tolven.installer") {
		$installerPlugin = $plugin[3];
		break;
    } 
   }
echo '<a href="';
echo $installerPlugin;
echo '">Installer</a>';
?>
 plug-in which
contains scripts and XML files you will need to install Tolven.</li>
<li>Plugins that contain template plugin configuration files that you are likely to use in your application. </li> 
</ul>
<p>Plug-ins in this catalog do not
depend on previous Tolven catalogs.
Therefore, in a default installation, the only repositoryLibrary file
in your tolven/tolven-config/plugins.xml file should be this catalog.</p>
<p>After a version of a plug-in is
published, it is never changed or deleted.
Therefore, when Tolven updates a plug-in in any way, it's version
number is incremented. </p>
<p>Catalog snapshots are provided in order to maintain a stable catalog
for deployment. To use a snapshot, point your repositoryLibrary to one
of the snapshot folders rather than the main catalog folder.</p>
<ul>
  <li>Click a plug-in file name or
version number to view that plug-in's release notes.</li>
</ul>
<ul>
  <li>Click <span style="font-style: italic;">prev</span> to view all versions of the plug-in.</li>
</ul>
<ul>
<li>Click <span style="font-style: italic;">javadoc</span>
to access the Javadoc for a plug-in, when one is available.</li>
</ul>
<ul>
<li>In the rare case that you
are directed to do so or if you require some specific information from
a plug-in other than the installer plug-in or the OpenDS config plug-in, click <span
 style="font-style: italic;">zip</span> to download that plug-in.</li>
</ul>
<ul>
<li>Click <span style="font-style: italic;">md5</span> to compute the MD5 hash of the file, which must match the hash
maintained in the plugins.xml file.</li>
</ul>
            <table>
                <tr>
                    <td width="20em" style="padding:0.5em">
                        <h3>Documentation</h3>
                        <p>
                            <table>
                                <tr>
                                    <td>
                                        <a href="http://wiki.tolven.org/doc/index.php/V21_20110919">
                                            <span style="color:red">Critical Release Notes</span>
                                        </a>
                                    </td>
                                    <td>
                                        <a href="onc/plugins.xml">ONC Configuration</a> (Not certified)
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <a href="http://wiki.tolven.org/doc/index.php/Tolven_V2.1_Linux_Quick_Start_Install"> Installation Guide (Linux)</a>
                                    </td>
                                    <td>
                                        <a href="http://wiki.tolven.org/doc/index.php/Tolven_V2.1_Windows_Quick_Start_Install"> Installation Guide (Windows)</a>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <a href="http://wiki.tolven.org/doc/index.php/Developer%27s_Guide"> Developer's Guide</a>
                                    </td>
                                    <td>
                                        <a href="kit.php">Tolven Kit Download Page</a>
                                    </td>
                                </tr>
                            </table>
                        </p>
                    </td>
                    <td width="40%" style="padding:0.5em">
                    </td>
                </tr>
            </table>
            <h3>Snapshots</h3>
            <table>
                <thead>
                    <tr>
                        <th style="border-bottom: thin black solid">Name</th>
                        <th style="border-bottom: thin black solid">Date</th>
                        <th style="border-bottom: thin black solid">Manifest</th>
                    </tr>
                </thead>
<?
    $path = "./snapshots";
    $dir_handle = @opendir($path) or die("Unable to open $path");
    // Collect snapshot file names 
    $snapshots = array();
    while ($file = readdir($dir_handle)) {
        if ($file=='.') continue;
        if ($file=='..') continue;
        $fileName = "snapshots/" . $file;
        if (!is_dir($fileName)) continue;
        $fileTime = filemtime($fileName);
        $snapshots[$file] = array($file, $fileTime, $fileName);
    } 
    closedir($dir_handle); 
    sort($snapshots);
    foreach (array_reverse($snapshots, true) as $snapshot) {
      echo "<tr>";
        echo '<td><a href="http://wiki.tolven.org/doc/index.php/';
          echo $snapshot[0];
          echo '">';
          echo $snapshot[0];
          echo "</a></td>";
        echo "<td>";
           echo date ("Y-m-d H:i T", $snapshot[1]);
        echo "</td>";
        echo '<td><a href="';
          echo $snapshot[2];
          echo '/plugins.xml">Manifest</a></td>';
      echo "</tr>\n";
    }
 ?>
    </table>
            <h3>
                List of Plugins
                <a href="plugins.xml" style="font-size: 60%">plugins.xml</a>
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
     <br/><a href="obsolete.php">Obsolete plugins</a>
      </div>
    </body>
</html>

