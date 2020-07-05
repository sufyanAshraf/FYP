<?php
	require 'db_conn.php'; 

	
	if (isset($_POST['add']))
	{
		$name = $_POST['Name'];
		$Information = $_POST['Information'];
		$Long_min = $_POST['Long_min'];
		$Long_max = $_POST['Long_max'];
		$Lat_min = $_POST['Lat_min'];
		$Lat_max = $_POST['Lat_max'];
		$Msg = $_POST['Message'];
		$city = $_POST['City'];
		$Assign = $_POST['Assign'];
		$Category = $_POST['Category'];
		
		 
		$query = "INSERT INTO Landmark_infromation (Name, City, Long_min, Long_max, Lat_min, Lat_max, Assign_to, Category, Information, Message)
		VALUES ('$name','$city', '$Long_min', '$Long_max', '$Lat_min', '$Lat_max', '$Assign', '$Category' '$Information', '$Msg');";

		if(mysqli_query($conn, $query))
		{
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['add_food']))
	{
		 
		$Dish = $_POST['Name'];
		$city =$_POST['City'];
		 
		$query = "INSERT INTO food (Name, City)
		VALUES ('$Dish', '$city');";

		if(mysqli_query($conn, $query))
		{
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['update']) )
	{
		
		$ID = $_POST['update'];
		$name = $_POST['Name'];
		$Information = $_POST['Information'];
		$Long_min = $_POST['Long_min'];
		$Long_max = $_POST['Long_max'];
		$Lat_min = $_POST['Lat_min'];
		$Lat_max = $_POST['Lat_max'];
		$Msg =$_POST['Message'];
		$city =$_POST['City'];
		$Assign =$_POST['Assign'];
		$Category =$_POST['Category'];

		$query = "UPDATE Landmark_infromation SET Name ='$name', City ='$city' , Long_min= '$Long_min', Long_max = '$Long_max', Lat_min= '$Lat_min', Lat_max='$Lat_max', Assign_to = '$Assign', Category = '$Category', Information = '$Information', Message = '$Msg' WHERE ID='$ID';";

		if(mysqli_query($conn, $query))
		{
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['delete']))
	{
		$id = $_POST['delete'];

		$query = "DELETE FROM Landmark_infromation WHERE ID = '$id';";

		if(mysqli_query($conn, $query)){
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['delete_food']))
	{
		$id = $_POST['delete_food'];

		$query = "DELETE FROM food WHERE Food_ID = '$id';";

		if(mysqli_query($conn, $query)){
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['update_food']))
	{
		$ID = $_POST['update_food']; 
		$Dish = $_POST['Name']; 
		$city =$_POST['City'];
		 
		$query = "UPDATE food SET Name ='$Dish', City ='$city' WHERE Food_ID ='$ID';";

		if(mysqli_query($conn, $query))
		{
			echo "True";
		}
		else
		{
			echo "False";
		}
	}
	else if (isset($_POST['food_img']) && isset($_POST['food_id']))
	{ 

		$ID = $_POST['food_id'];
		$res1 = $_POST['food_img']; 

		$query = "UPDATE food SET Food_img ='$res1' WHERE Food_ID ='$ID';";
		if(mysqli_query($conn, $query))
		{
			echo "True";
		}
		else
		{
			echo "False";
		}
	}