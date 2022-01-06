DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT  PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    reservation_date VARCHAR(50) NOT NULL,
    source_loc VARCHAR(50) NOT NULL,
    destination_loc VARCHAR(50) NOT NULL
);