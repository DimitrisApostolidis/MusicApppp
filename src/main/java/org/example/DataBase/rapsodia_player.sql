

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";



CREATE TABLE `artists` (
  `artist_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `bio` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `artists` (`artist_id`, `name`, `bio`) VALUES
(1, 'rihanna', 'rihanna is a pop and R&B singer based in the US'),
(2, 'Adele', 'Adele is a british singer-songwriter'),
(3, 'Kanye West', 'Kanye West is an American rapper, singer, songwriter and record producer');


CREATE TABLE `favourite_songs` (
  `user_id` int(11) NOT NULL,
  `song_id` int(11) NOT NULL,
  `favorite_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `favourite_songs` (`user_id`, `song_id`, `favorite_date`) VALUES
(1, 3, '2023-07-22 00:00:00'),
(2, 3, '0008-04-24 00:00:00'),
(3, 1, '2017-06-24 00:00:00');


CREATE TABLE `playlist` (
  `playlist_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `playlist` (`playlist_id`, `name`, `user_id`) VALUES
(1, 'pop', 2),
(2, 'R&B', 1);



CREATE TABLE `playlist_song` (
  `playlist_id` int(11) NOT NULL,
  `song_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `playlist_song` (`playlist_id`, `song_id`) VALUES
(1, 2),
(2, 1);


CREATE TABLE `song` (
  `song_id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `artist` varchar(100) DEFAULT NULL,
  `album` varchar(100) DEFAULT NULL,
  `duration` time DEFAULT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `artist_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `song` (`song_id`, `title`, `artist`, `album`, `duration`, `genre`, `artist_id`) VALUES
(1, 'Diamonds', 'rihanna', 'unapologetic', '03:45:00', 'pop-electronic-R&B', 1),
(2, 'Heartless', 'Kanye West', '808s&heartbreak', '03:31:00', 'pop', 3),
(3, 'rolling in the deep', 'Adele', '21', '03:48:00', 'rhythm and blues-soul', 2);


CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


INSERT INTO `user` (`user_id`, `username`, `password`, `email`) VALUES
(1, 'john', 'pass1', 'john@gmail.com'),
(2, 'maria', 'pass2', 'maria@gmail.com'),
(3, 'xristos', 'pass3', 'xristos@gmail.com');


ALTER TABLE `artists`
  ADD PRIMARY KEY (`artist_id`);

ALTER TABLE `favourite_songs`
  ADD PRIMARY KEY (`user_id`,`song_id`),
  ADD KEY `song_id` (`song_id`);

ALTER TABLE `playlist`
  ADD PRIMARY KEY (`playlist_id`),
  ADD KEY `user_id` (`user_id`);

ALTER TABLE `playlist_song`
  ADD PRIMARY KEY (`playlist_id`,`song_id`),
  ADD KEY `song_id` (`song_id`);

ALTER TABLE `song`
  ADD PRIMARY KEY (`song_id`),
  ADD KEY `artist_id` (`artist_id`);

ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

ALTER TABLE `artists`
  MODIFY `artist_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

ALTER TABLE `playlist`
  MODIFY `playlist_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;


ALTER TABLE `song`
  MODIFY `song_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

ALTER TABLE `favourite_songs`
  ADD CONSTRAINT `favourite_songs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `favourite_songs_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`song_id`);

ALTER TABLE `playlist`
  ADD CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `playlist_song`
  ADD CONSTRAINT `playlist_song_ibfk_1` FOREIGN KEY (`playlist_id`) REFERENCES `playlist` (`playlist_id`),
  ADD CONSTRAINT `playlist_song_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `song` (`song_id`);

ALTER TABLE `song`
  ADD CONSTRAINT `song_ibfk_1` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`artist_id`);
COMMIT;
