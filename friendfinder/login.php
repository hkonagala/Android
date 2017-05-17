<?php

include "config.php";

$input = json_decode(file_get_contents('php://input'), true);
$email = $input["email"];
$password = $input["password"];
$latitude = floatval(0);
$longitude = floatval(0);
 
// Create connection to database
$conn = new mysqli(dbservername, dbusername, dbpassword, dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$email = $conn->real_escape_string($email);
$password = $conn->real_escape_string($password);

//check for an existing table users
$query = "SELECT email from users where email = '$email' AND password = '$password'";
$result = $conn->query($query);
if (!$result || $result->num_rows == 0){
	echo "User does not exist! \n";
} else{
	echo "Login successfully! \n";
}

$conn->close();
?>