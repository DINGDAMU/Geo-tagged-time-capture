<?php
$con=mysql_connect("127.0.0.1:3306","root","");
if(!$con){
    die('Could not connect'.mysql_error());
    echo "failed to connect the database";
}
mysql_select_db("test",$con);

 $result=mysql_query("SELECT * from login where email='$_POST[email]' ");

  $number_of_rows=mysql_num_rows($result);
  if($number_of_rows==0){
  $sql="INSERT INTO login(username,email,password,profile_url)
          VALUES('$_POST[username]','$_POST[email]','$_POST[password]','$_POST[profile_url]')";
  if(!mysql_query($sql,$con)){
      die('Error:'.mysql_errori());
  }
      echo "1 record added";
  }else{
      echo "Duplicated!";
              }
     
mysql_close($con);

?>
