<html>
<head>
<title>MD5 Checksum</title>
<link href="../css/tolven.css" rel="stylesheet" type="text/css" />
</head>
<body>

<?
echo("<h3>MD5 for ");
echo( $_GET['zip']);
echo("</h3>");
echo (md5_file( $_GET['zip']));
?>

</body>
</html>
