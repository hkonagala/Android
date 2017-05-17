<?php

include "config.php";

function distancetocalculate($lat1, $lon1, $lat2, $lon2, $unit) {

 $theta = $lon1 - $lon2;
 $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
 $dist = acos($dist);
 $dist = rad2deg($dist);
 $miles = $dist * 60 * 1.1515;
 $unit = strtoupper($unit);

 if ($unit == "K") {
   return ($miles * 1.609344);
 } else if ($unit == "N") {
     return ($miles * 0.8684);
   } else {
       return $miles;
     }
}

try
{
	$input = json_decode(file_get_contents('php://input'), true);
	$latitude = floatval($input["latitude"]);
	$longitude = floatval($input["longitude"]);
	$email = $input["email"];

	// Create connection to database
	$conn = new mysqli(dbservername, dbusername, dbpassword, dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
	//check for users nearby
	$query = "SELECT email, full_name, last_active_time, latitude, longitude FROM users WHERE email <> '$email' AND last_active_time > TIME(DATE_SUB(NOW(),INTERVAL 1 HOUR)) ORDER BY email ASC";
	$result = $conn->query($query);
	
	#json array
	$newarray = array();
	
	while($row = $result->fetch_assoc())
	{
		$newLatitude = floatval($row["latitude"]);
		$newLongitude = floatval($row["longitude"]);
		$distance = distancetocalculate($latitude, $longitude, $newLatitude, $newLongitude, "K");
		if($distance <= 1)
		{
			$newArray["email"] = $row['email'];
			$newArray["full_name"] = $row['full_name'];
			$newArray["last_active_time"] = $row['last_active_time'];
			$newArray["latitude"] = $row['latitude'];
			$newArray["longitude"] = $row['longitude'];
			$newarray[] = $newArray;
		}
	}
	header('Content-type: application/json');
	echo json_encode($newarray);
	
}catch(Exception $e)
{
	echo 'Exception caught: ', $e->getMessage(), "\n";
}finally
{
	$conn->close();
}
?>







