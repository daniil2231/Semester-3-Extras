CREATE TABLE country
(
    id   int     NOT NULL AUTO_INCREMENT,
    code char(2) NOT NULL,
    name varchar(50),
    PRIMARY KEY (id),
    UNIQUE (code),
    UNIQUE (name)
);

CREATE TABLE student
(
    id         int NOT NULL AUTO_INCREMENT,
    pcn        int NOT NULL,
    name       varchar(50),
    country_id int,
    PRIMARY KEY (id),
    UNIQUE (pcn),
    FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE user
(
    id         int          NOT NULL AUTO_INCREMENT,
    username   varchar(20)  NOT NULL,
    password   varchar(100) NOT NULL,
    student_id int NULL,
    PRIMARY KEY (id),
    UNIQUE (username),
    FOREIGN KEY (student_id) REFERENCES student (id)
);

CREATE TABLE user_role
(
    id        int         NOT NULL AUTO_INCREMENT,
    user_id   int         NOT NULL,
    role_name varchar(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

