# Reading schema

# --- !Ups

CREATE TABLE Reading (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    targetTempC double NOT NULL,
    postcode varchar(20) NOT NULL,
    country varchar(20) NOT NULL,
    externalTempC double NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Reading;