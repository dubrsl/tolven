<html>
<head>
<title>Manifest</title>
<link href="../css/tolven.css" rel="stylesheet" type="text/css" />
</head>
<body>

<?
echo("<h3>Plugin Manifest for ");
echo( $_GET['zip']);
echo("</h3>");

$zip = zip_open($_GET['zip']);
while ($zip_entry = zip_read($zip)) {
    $file = zip_entry_name($zip_entry);
    if (zip_entry_name($zip_entry) == "tolven-plugin.xml") {
//       echo("<p>$file</p>");
       $fileSize = zip_entry_filesize($zip_entry);
        if (zip_entry_open($zip, $zip_entry)) {
           echo("<pre>");
           echo( htmlentities(zip_entry_read( $zip_entry, $fileSize )));
           zip_entry_close( $zip_entry );
           echo("</pre>");
        }
    }
}
zip_close($zip);

?>

</body>
</html>
