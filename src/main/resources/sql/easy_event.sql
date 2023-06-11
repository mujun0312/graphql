DROP TABLE IF EXISTS tb_event;
CREATE TABLE tb_event (
    id int(10) NOT NULL AUTO_INCREMENT,
    title varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    description varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    price float NOT NULL,
    date timestamp,
    creator_id int(10) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_creator_id FOREIGN KEY (creator_id) REFERENCES tb_user(id)
) ENGINE=InnoDB AUTO_INCREMENT=1022 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id int(10) NOT NULL AUTO_INCREMENT,
    email varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    password varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_booking;
CREATE TABLE tb_booking (
    id int(10) NOT NULL AUTO_INCREMENT,
    user_id int(10) NOT NULL,
    event_id int(10) NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tb_user(id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES tb_event(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE INDEX idx_user_id ON tb_booking (user_id)