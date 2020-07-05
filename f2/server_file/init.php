<?php
	$servername = "localhost";
	$username = "root";
	$password = "";

	$connCreate = mysqli_connect($servername, $username, $password);
	$sql = "CREATE DATABASE IF NOT EXISTS landmark";
	mysqli_query($connCreate, $sql);
	mysqli_close($connCreate);

	require 'db_conn.php';

	// table to store user data

	$sql = 'CREATE TABLE IF NOT EXISTS `Landmark_infromation` (
			  `ID` int(11) NOT NULL AUTO_INCREMENT,
			  `Name` varchar(100) NOT NULL,
			  `Information` varchar(5000) NOT NULL,
			  `Message` varchar(250) NOT NULL,
			  `City` varchar(100) NOT NULL,
			  `Long_min` double NOT NULL,
			  `Long_max` double NOT NULL,
			  `Lat_min` double NOT NULL,
			  `Lat_max` double NOT NULL,
			  PRIMARY KEY (`ID`)
			);';
	mysqli_query($conn, $sql);
	
	
	$sq1 = 'CREATE TABLE IF NOT EXISTS `food` (
			`Food_ID` int(11) NOT NULL AUTO_INCREMENT, 
			`Name` varchar(100) NOT NULL, 
			`City` varchar(100) NOT NULL, 
			PRIMARY KEY (`Food_ID`)
			);';
	
	mysqli_query($conn, $sql);
	
	
	//74.30,74.31,31.58,31.59
	//INSERT INTO `food`( `Name`, `City`) VALUES ( 'Chicken Lahori','lahore'), ( 'Gosht karahi','lahore') ,( 'Haleem','lahore') ,( 'Chicken tikka','lahore') ,( 'Dahi bhallay','lahore') ,( 'Gol gappa','lahore') ,( 'Seekh kababs','lahore') ,( 'Dal gosht','lahore')
	/** 
	
	$info1 = null;
	$info2 = null;
	$info3 = null;
	
	function setval(){
		global $info1, $info2, $info3;
	
	
	$info1 = 'The Lahore Fort is a citadel in the city of Lahore, Punjab, Pakistan. The fortress is located at the northern end of walled city Lahore, 
		and spreads over an area greater than 20 hectares. It contains 21 notable monuments, some of which date to the era of Emperor 
		Akbar. The Lahore Fort is notable for having been almost entirely rebuilt in the 17th century, when the Mughal Empire was at the height
		of its splendour and opulence.\n\n 
		Though the site of the Lahore Fort has been inhabited for millennia, the first record of a fortified structure at the site was in regard to 
        an 11th-century mud-brick fort. The foundations of the modern Lahore Fort date to 1566 during the reign of Emperor Akbar, who bestowed the  
        fort with a syncretic architectural style that featured both Islamic and Hindu motifs. Additions from the Shah Jahan period are characterized  
        by luxurious marble with inlaid Persian floral designs, while the fort}s grand and iconic Alamgiri Gate was constructed by the last of the  
        great Mughal Emperors, Aurangzeb, and faces the renowned Badshahi Mosque.\n\n 
        After the fall of the Mughal Empire, Lahore Fort was used as the residence of Emperor Ranjit Singh, founder of the Sikh Empire. The fort 
        then passed to British colonialists after they annexed Punjab following their victory over the Sikhs at the Battle of Gujrat in February 
        1849. In 1981, the fort was inscribed as a UNESCO World Heritage Site for its outstanding repertoire of Mughal monuments dating from the 
        era when the empire was at its artistic and aesthetic zenith.';
 
	$info2 = 'Minar-e-Pakistan is a national monument located in Lahore, Pakistan. The tower was built between 1960 and 1968 on the site 
                where the All-India Muslim League passed the Lahore Resolution on 23 March 1940 - the first official call for a separate and 
                independent homeland for the Muslims of British India, as espoused by the two-nation theory. The resolution eventually helped 
                lead to the emergence of an independent Pakistani state in 1947.The tower reflects a blend of Mughal/Islamic and modern 
                architecture.\n\n
                The tower was designed and supervised by, Nasreddin Murat-Khan a Russian-born Pakistani architect and civil engineer .The 
                foundation stone was laid on 23 March 1960. Construction took eight years, and was completed on 21 October 1968 at an estimated
                 cost of Rs 7,058,000. The money was collected by imposing an additional tax on cinema and horse racing tickets at the demand of 
                Akhtar Hussain, governor of West Pakistan. Today, the minaret provides a panoramic view to visitors who can}t climb up the stairs
                 or access the top, by means of an elevator. The parks around the monument include marble fountains and an artificial lake.\n
                The tower was designed and supervised by, Nasreddin Murat-Khan a Russian-born Pakistani architect and civil engineer . The 
                foundation stone was laid on 23 March 1960. Construction took eight years, and was completed on 21 October 1968 at an estimated
                 cost of Rs 7,058,000. The money was collected by imposing an additional tax on cinema and horse racing tickets at the demand of 
                Akhtar Hussain, governor of West Pakistan. Today, the minaret provides a panoramic view to visitors who can}t climb up the stairs
                 or access the top, by means of an elevator. The parks around the monument include marble fountains and an artificial lake.\n
                The tower was designed and supervised by, Nasreddin Murat-Khan a Russian-born Pakistani architect and civil engineer .The 
                foundation stone was laid on 23 March 1960. Construction took eight years, and was completed on 21 October 1968 at an estimated
                 cost of Rs 7,058,000. The money was collected by imposing an additional tax on cinema and horse racing tickets at the demand of 
                Akhtar Hussain, governor of West Pakistan. Today, the minaret provides a panoramic view to visitors who can}t climb up the stairs
                 or access the top, by means of an elevator. The parks around the monument include marble fountains and an artificial lake.\n
                The tower was designed and supervised by, Nasreddin Murat-Khan a Russian-born Pakistani architect and civil engineer. The 
                foundation stone was laid on 23 March 1960. Construction took eight years, and was completed on 21 October 1968 at an estimated
                 cost of Rs 7,058,000. The money was collected by imposing an additional tax on cinema and horse racing tickets at the demand of 
                Akhtar Hussain, governor of West Pakistan. Today, the minaret provides a panoramic view to visitors who can}t climb up the stairs
                 or access the top, by means of an elevator. The parks around the monument include marble fountains and an artificial lake.';
 
	$info3 = 'The Badshahi Mosque is a Mughal era mosque in Lahore, capital of the Pakistani province of Punjab, Pakistan. The mosque is located 
                 west of Lahore Fort along the outskirts of the Walled City of Lahore, and is widely considered to be one of Lahore}s most iconic landmarks.\n\n
                The Badshahi Mosque was built by Emperor Aurangzeb in 1671, with construction of the mosque lasting for two years until 1673. The mosque is an 
                important example of Mughal architecture, with an exterior that is decorated with carved red sandstone with marble inlay. It remains the largest 
                mosque of the Mughal-era, and is the second-largest mosque in Pakistan. After the fall of the 
                Mughal Empire, the mosque was used as a garrison by the Sikh Empire and the British Empire, and is now one of Pakistan}s most iconic sights.
                The mosque is located adjacent to the Walled City of Lahore, Pakistan. The entrance to the mosque lies on the western side of the rectangular 
                Hazuri Bagh, and faces the famous Alamgiri Gate of the Lahore Fort, which is located on the eastern side of the Hazuri Bagh. The mosque is also 
                located next to the Roshnai Gate, one of the original thirteen gates of Lahore, which is located to the southern side of the Hazuri Bagh.\n\n
                Near the entrance of the mosque lies the Tomb of Muhammad Iqbal, a poet widely revered in Pakistan as the founder of the Pakistan Movement which 
                led to the creation of Pakistan as a homeland for the Muslims of British India. Also located near the mosque}s entrance is the tomb of Sir Sikandar 
                Hayat Khan, who is credited for playing a major role in preservation and restoration of the mosque.';
	}
 
	setval();
 
	$q = "INSERT INTO landmark_infromation( Name, Information, Long_min, Long_max, Lat_min, Lat_max) VALUES 
	('Lahore fort', '$info1', 74.300, 74.319, 31.580, 31.597);";
	
	 
	$q .= "INSERT INTO landmark_infromation( Name, Information, Long_min, Long_max, Lat_min, Lat_max) VALUES
	('Minar-e-Pakistan', '$info2',74.300, 74.319, 31.580, 31.597);";
	
	 
	$q .= "INSERT INTO landmark_infromation( Name, Information, Long_min, Long_max, Lat_min, Lat_max) VALUES
	('Badshahi Mosque', '$info3', 74.300, 74.319, 31.580, 31.597)"; 

	 
	if ($conn->multi_query($q) === TRUE) {
		echo "New records created successfully";
	} else {
		echo "Error: " . $sql . "<br>" . $conn->error;
	} 
	
	
	 */
	 
	 