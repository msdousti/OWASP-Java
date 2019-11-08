DROP DATABASE IF EXISTS testdb;
-- DROP USER IF EXISTS readonly@localhost;

CREATE DATABASE testdb;
CREATE USER IF NOT EXISTS readonly@localhost IDENTIFIED BY 'MyVeryLongPassphrase';
GRANT SELECT ON testdb.* TO readonly@localhost;
FLUSH PRIVILEGES;

USE testdb;

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE users
(
    id         int(10) UNSIGNED                    NOT NULL AUTO_INCREMENT,
    username   varchar(255)                        NOT NULL,
    role       enum ('normal','superuser','admin') NOT NULL DEFAULT 'normal',
    password   varchar(255)                        NOT NULL,
    created_at timestamp(0)                        NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    updated_at timestamp(0)                        NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (id)
);

-- ----------------------------
-- Table structure for guestbook
-- ----------------------------
CREATE TABLE guestbook
(
    id         int(10) UNSIGNED    NOT NULL AUTO_INCREMENT,
    userId     int(10) UNSIGNED    NOT NULL,
    comment    varchar(500)        NOT NULL,
    approved   tinyint(2) UNSIGNED NOT NULL DEFAULT 0,
    created_at timestamp(0)        NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
    updated_at timestamp(0)        NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
    PRIMARY KEY (id),
    CONSTRAINT guestbook_ibfk_1 FOREIGN KEY (userId) REFERENCES users (id)
);

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO users (username, role, password)
VALUES ('sadeq', 'admin', 'IGATe'),
       ('ali', 'normal', 'Rkh'),
       ('reza', 'normal', 'aRmtR'),
       ('hossein', 'normal', 'ACYgMu'),
       ('javad', 'superuser', 'tI'),
       ('mahmood', 'normal', 'oNvaNit'),
       ('kambiz', 'admin', '1');

-- ----------------------------
-- Records of guestbook
-- ----------------------------
INSERT INTO guestbook (userId, comment)
VALUES (6, 'Oh my God! This is the best website on the Internet!'),
       (4, 'Wow! I really like your customer service. Thanks'),
       (2, 'Hey! Where can I ask a simple question?'),
       (3, 'Extra comment'),
       (2,
        'Provides great advice...Through case studies and user friendly diagrams and statistics you\'re able to understand the best approach...The ideas in this site and its analytical approach have inspired me, and I would recommend the site to anyone involved in fund-raising, sponsorship or funding applications.');


SET FOREIGN_KEY_CHECKS = 1;
