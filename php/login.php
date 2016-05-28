<?php
 $con=mysql_connect("127.0.0.1:3306","root","");
    if(!$con){
                      die('Could not connect:'.mysql_error());
                        echo "failed to connect the database";
               }
                        mysql_select_db("test",$con);
$email=$_POST['email'];
$password=$_POST['password'];
$result=mysql_query("SELECT * from login where email='$email' AND password='$password'");
$number_of_rows=mysql_num_rows($result);
if($number_of_rows>0){
echo "true";
}else{
echo "false";
}
mysql_close($con);
?>
