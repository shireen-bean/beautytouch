# Machine log schema

# --- !Ups

CREATE TABLE machine_log (
  machine_id INT NOT NULL,
  jammed BOOL default 0 NOT NULL,
  traffic INT default 0 NOT NULL,
  time TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE machine_log;
