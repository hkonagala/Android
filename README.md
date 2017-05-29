# Android

The friendfinder app has a front end implementation of MainActivity, LoginActivity, signupActivity and MapsActivity with respective functionalities.
A web service using php scripts to connect to the database provided by XAMPP.
Created different web services for each functionality of the app, i.e., registerUser for signup, login for loginActivity, findFriends and updateUsers for locating nearby users within a radius of 1km.
In findFriends.php, implemented a function to calculate the distance between two latitudes and longitudes and queries to pull up users based on the distance (1km). To increase the distance we just need to modify the if condition to "<=x km" where x=5/10.

Used a separate php script "createTable" to create a table in the database friendfinder. The table consists of unique fields like id (auto generated), email, full_name,password,last_active_time, latitude, longitude etc. use this script to create an environment for the app.

Config.php can be used to connect to the server and the database friendfinder.

place all the php scripts in xampp/htdocs/friendfinder/
For the app to work, change the ipaddress in strings.xml baseUrl.
