<?php
$base_path = "./upload/"; //存放目录  
if(!is_dir($base_path)){  
        mkdir($base_path,0777,true);  
        }  
$target_path = $base_path . basename ( $_FILES ['attach'] ['name'] );  
if (move_uploaded_file ( $_FILES ['attach'] ['tmp_name'], $target_path )) {  
            echo "success!";  
        } else {  
            echo "fail!";
        }
include 'mysql.php';          
       
?>  
