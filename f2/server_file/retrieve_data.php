<?php
	require 'db_conn.php';
	
	if ( isset($_POST['ID']))
	{
		$id = number_format(mysqli_real_escape_string($conn, $_POST['ID']));
		 
		$query = "SELECT * FROM Landmark_infromation WHERE ID = '$id'";
		$result = mysqli_query($conn, $query);
		if (mysqli_num_rows($result) == 1)
		{
			$row = mysqli_fetch_assoc($result);
			
			$struct = array(
				"ID" => $row['ID'],
				"Name" => $row['Name'],
				"City" => $row['City'],
				"Message" => $row['Message'],
				"Information" => $row['Information'],
				"Long_min" => $row['Long_min'],
				"Long_max" => $row['Long_max'],
				"Lat_min" => $row['Lat_min'],
				"Lat_max" => $row['Lat_max']
			);
			echo json_encode($struct);
		}
		else
		{
			$struct = array( 
				"ID" => -1,
				"Name" => NULL,
				"City" => NULL,
				"Message" =>NULL,
				"Information" => NULL,
				"Long_min" => 0,
				"Long_max" => 0,
				"Lat_min" => 0,
				"Lat_max" => 0
			);
			echo json_encode($struct);
		}
	}
	else if(isset($_POST['getFood']))
	{
		$city = $_POST['getFood'];
		$ID=array();
		$Food_Name=array();
		 
		$query = "SELECT * FROM food WHERE City = '$city'";
		$result = mysqli_query($conn, $query);
		$count = mysqli_num_rows($result);
		for ($var = 0; $var < $count; $var++)
		{
			$row = mysqli_fetch_assoc($result);
			array_push($ID, $row["Food_ID"]); 
			array_push($Food_Name, $row["Name"]);
		}
		if ($count > 0){
			$index = rand()%$count;
			$struct = array(
				"Food_ID" => $ID[$index], 
				"Name" => $Food_Name[$index]
			);
			echo json_encode($struct);
		}else{
			$struct = array(
				"Food_ID" => NULL, 
				"Name" => NULL
			);
			echo json_encode($struct);
		}
	}
	else if(isset($_POST['getNearLandmark']) && $_POST['City'])
	{
		$id = $_POST['getNearLandmark'];
		$city = $_POST['City'];
		$ID_arr = array();
		$name_arr = array();
		$msg_arr = array();
		
		$query = "SELECT `ID`, `Name`, `Message` FROM Landmark_infromation WHERE City = '$city' and `Assign_to` != (SELECT `Assign_to` FROM Landmark_infromation WHERE `ID` = '$id') and `Category` = (SELECT `Category` FROM Landmark_infromation WHERE `ID` = '$id')";
		$result = mysqli_query($conn, $query);
		
		$count = mysqli_num_rows($result);
		for ($var = 0; $var < $count; $var++)
		{
			$row = mysqli_fetch_assoc($result);
			array_push($ID_arr, $row["ID"]);
			array_push($name_arr, $row["Name"]);
			array_push($msg_arr, $row["Message"]);
		}
		if ($count > 0){
			$index = rand()%$count; 
			$struct = array(
				"Name" => $name_arr[$index],
				"Message" => $msg_arr[$index]
			);
			echo json_encode($struct);
		}else{
			
			$query = "SELECT `ID`, `Name`, `Message`  FROM Landmark_infromation WHERE City = '$city' and `Assign_to` != (SELECT `Assign_to` FROM Landmark_infromation WHERE `ID` = '$id')";
			$result = mysqli_query($conn, $query);
			
			$count = mysqli_num_rows($result);
			for ($var = 0; $var < $count; $var++)
			{
				$row = mysqli_fetch_assoc($result);
				array_push($ID_arr, $row["ID"]);
				array_push($name_arr, $row["Name"]);
				array_push($msg_arr, $row["Message"]);
			}
			if ($count > 0){
				$index = rand()%$count; 
				$struct = array(
					"Name" => $name_arr[$index],
					"Message" => $msg_arr[$index]
				);
				echo json_encode($struct);
			}else{
				$struct = array(
					"Name" => NULL, 
					"Message" => NULL
				);
				echo json_encode($struct);
			}
		}
	}
	else if(isset($_POST['get_food_image']))
	{
		$ID = $_POST['get_food_image'];
		

		$query = "SELECT Food_img FROM food WHERE Name ='$ID';";
		$result = mysqli_query($conn, $query);
		if(mysqli_num_rows($result) == 1)
		{
			$row = mysqli_fetch_assoc($result);
			echo $row['Food_img'];
		}
		else
			echo "NULL";
	}