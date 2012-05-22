<?php

if (isset($_POST["name"]) && isset($_POST["solution"])) {

	$output_file = $_POST["name"] . ".solution";
	$fh = fopen($output_file, 'w');
	fwrite($fh, $_POST["solution"]."\n");

	echo "File $output_file written on server";
}
else {
	echo "Invalid request";
}

?>
