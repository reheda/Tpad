-- PostgreSql
CREATE TYPE enum_types AS ENUM ('Simple','Simple numeric','Multi-valued','Multi-valued numeric','Repeating','Repeating numeric');
CREATE TABLE IF NOT EXISTS attributes (
  id serial PRIMARY KEY,
  type enum_types NOT NULL,
  name varchar(255) NOT NULL,
  deactivated boolean NOT NULL DEFAULT FALSE,
  group_id serial NOT NULL,
  group_name varchar(255) NOT NULL,
  last_update date NOT NULL DEFAULT CURRENT_DATE,
  comment varchar(500) DEFAULT NULL
);