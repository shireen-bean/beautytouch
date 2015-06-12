# Machine log schema

# --- !Ups

CREATE TABLE machine_log (
  machine_id INT NOT NULL,
  jammed BOOL default 0 NOT NULL,
  traffic INT default 0 NOT NULL,
  time TIMESTAMP NOT NULL,
  CONSTRAINT machine_id FOREIGN KEY (machine_id) REFERENCES machines (id) on update cascade
);

# --- !Downs

DROP TABLE machine_log;
