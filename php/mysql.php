<?php
$con=mysql_connect("127.0.0.1:3306","root","");
if(!$con){
    die('Could not connect:'.mysql_error());
    echo "failed to connect the database";
}
mysql_select_db("test",$con);

$sql="INSERT INTO information(username,url,coordinates,address,time)
	VALUES
		('$_POST[username]','$_POST[url]','$_POST[coordinates]','$_POST[address]','$_POST[time]')";
			
if (!mysql_query($sql,$con))
  {
  die('Error: ' . mysql_error());
  }
echo "1 record added";

mysql_close($con);

?>
