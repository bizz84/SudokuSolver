<?php

/* File to retrieve all json files */

$files = glob($directory . "*json");

$count = count($files);
$i = 0;
echo "[";
// Print contents
foreach($files as $file)
{
	$json = file_get_contents($file);
	$next = $i < $count - 1 ? "," : "";
	echo "{\"".$file."\":". $json . "}" .$next;
	//echo $json.$next;
	$i++;
}
echo "]";

?>
