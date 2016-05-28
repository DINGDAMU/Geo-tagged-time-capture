<?php
$con=mysql_connect("127.0.0.1:3306","root","");
if(!$con){
    die('Could not connect'.mysql_error());
    echo "failed to connect the database";
}
mysql_select_db("test",$con);

$base_path = "./profile/"; //存放目录
  if(!is_dir($base_path)){
           mkdir($base_path,0777,true);
            }
         $target_path = $base_path . basename ( $_FILES ['attach'] ['name'] );
    if (move_uploaded_file ( $_FILES ['attach'] ['tmp_name'], $target_path )) {
                 
              
                 
              
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
    }else{
  echo "fail to upload the profile";
    }

mysql_close($con);

?>
