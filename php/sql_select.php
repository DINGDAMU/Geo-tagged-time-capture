<?php
  $con=mysql_connect("127.0.0.1:3306","root","");
  if(!$con){
            die('Could not connect:'.mysql_error());
             echo "failed to connect the database";
     }
         mysql_select_db("test",$con);
     #$sql="SELECT * from information where address='$address' order by time";
     #$result=mysql_query("SELECT * from information WHERE                           address='$_POST[address]' ORDER BY time");
     $address=$_POST['address'];
     $result=mysql_query("SELECT * from information where address='$address' order by time");
     $number_of_rows = mysql_num_rows($result);
         $temp_array  = array();
    
             if($number_of_rows > 0) {
                         while ($row = mysql_fetch_array($result)) {
                                         $temp_array[] = $row;
                                               }
                           }
       
            echo json_encode(array("information"=>$temp_array));
          mysql_close($con);
        
                     ?>
