ALTER TABLE `artists`
ADD COLUMN `cover_image` VARCHAR(255) DEFAULT NULL;

CREATE TABLE `albums` (
  `album_id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `release_year` YEAR DEFAULT NULL,
  `artist_id` INT(11) NOT NULL,
  `cover_image` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`album_id`),
  FOREIGN KEY (`artist_id`) REFERENCES `artists`(`artist_id`)
) ;



ALTER TABLE `song`
ADD COLUMN `cover_image` VARCHAR(255) DEFAULT NULL,
ADD COLUMN `biography` TEXT DEFAULT NULL;


CREATE TABLE `artist_biography` (
  `bio_id` INT(11) NOT NULL AUTO_INCREMENT,
  `artist_id` INT(11) NOT NULL,
  `biography` TEXT NOT NULL,
  PRIMARY KEY (`bio_id`),
  FOREIGN KEY (`artist_id`) REFERENCES `artists`(`artist_id`)
);




ALTER TABLE playlist_song
ADD COLUMN song_name VARCHAR(255);

UPDATE playlist_song
JOIN song ON playlist_song.song_id = song.song_id
SET playlist_song.song_name = song.title;



