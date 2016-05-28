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
                 $target_path = $base_path . basename ( $_FILES ['attach'] ['name']     );
        if (move_uploaded_file ( $_FILES ['attach'] ['tmp_name'], $target_path      )) {
$sql="UPDATE login set profile_url='$_POST[profile_url]' where email='$_POST[email]'";
 if(!mysql_query($sql,$con)){
           die('Error:'.mysql_errori());
 }
        }
  mysql_close($con);

?>
