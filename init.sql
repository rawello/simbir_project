CREATE DATABASE accountdb;
CREATE DATABASE documentdb;
CREATE DATABASE hospitaldb;
CREATE DATABASE timetabledb;

\c accountdb

CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  lastName VARCHAR(255),
  firstName VARCHAR(255),
  username VARCHAR(255),
  password VARCHAR(255),
  roles VARCHAR(255)
);

INSERT INTO account (lastName, firstName, username, password, roles) VALUES
('admin', 'admin', 'admin', 'admin', 'admin'),
('manager', 'manager', 'manager', 'manager', 'manager'),
('doctor', 'doctor', 'doctor', 'doctor', 'doctor'),
('user', 'user', 'user', 'user', 'user');
