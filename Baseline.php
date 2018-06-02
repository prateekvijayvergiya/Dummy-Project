<?php  
$name = $_POST["name"];
$photo_title = $_POST["photo_title"];  
$video_title = $_POST["video_title"];
$message = $_POST["message"];
$user = "root";  
$password = "";  
$host ="localhost";  
$db_name ="newdetail";  
$con = mysqli_connect($host,$user,$password,$db_name);    
$sql = "INSERT  INTO baseline" . "(name , photo_title , video_title , message		)" . "VALUES ('$name' , '$photo_title' , '$video_title' , '$message')" ; 
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