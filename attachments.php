<?php  
$baseline_id = $_POST["baseline_id"];
$photo_path = $_POST["photo_path"];  
$video_path = $_POST["video_path"];
$photo_status = $_POST["photo_status"];
$video_status = $_POST["video_status"];
$mime_type = $_POST["mime_type"];
$user = "root";  
$password = "";  
$host ="localhost";  
$db_name ="newdetail";  
$con = mysqli_connect($host,$user,$password,$db_name);    
$sql = "INSERT  INTO attachments" . "(baseline_id , photo_path , video_path , photo_status , video_status , mime_type		)" . "VALUES ('$baseline_id' , '$photo_path' , '$video_path' , '$photo_status' , '$video_status' , '$mime_type')" ; 
if(mysqli_query($con,$sql))  
{  
    echo "Data inserted successfully....";  
}  
else   
{  
    echo "some error occured";  
}  
mysqli_close($con);  
?>  