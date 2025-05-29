DROP DATABASE if exists TL;
CREATE DATABASE TL DEFAULT CHARSET=greek;
USE TL;

-- 1. Πίνακας χρηστών
CREATE TABLE users (
  id             SERIAL PRIMARY KEY,
  name           VARCHAR(100) NOT NULL,
  password       VARCHAR(20) NOT NULL,
  email          VARCHAR(150) UNIQUE NOT NULL,
  phone          VARCHAR(20)
);

-- 2. Πίνακας πελατών (client)
CREATE TABLE clients (
  user_id        INTEGER PRIMARY KEY
    REFERENCES users(id)
    ON DELETE CASCADE,
  address        TEXT,
  history        TEXT
);

-- 3. Πίνακας τεχνικών (technician)
CREATE TABLE technicians (
  user_id        INTEGER PRIMARY KEY
    REFERENCES users(id)
    ON DELETE CASCADE,
  specialty      VARCHAR(50) NOT NULL,
  rating         DECIMAL(2,1) DEFAULT 0.0
);

-- 4. Πρόγραμμα / Schedule: κάθε γραμμή μια ημέρα με timeslots
CREATE TABLE schedules (
  id             SERIAL PRIMARY KEY,
  technician_id  INTEGER NOT NULL
    REFERENCES technicians(user_id)
    ON DELETE CASCADE,
  day_of_week    SMALLINT NOT NULL  -- 0=Δευτέρα … 6=Κυριακή
);

CREATE TABLE timeslots (
  id             SERIAL PRIMARY KEY,
  schedule_id    INTEGER NOT NULL
    REFERENCES schedules(id)
    ON DELETE CASCADE,
  start_time     TIME NOT NULL,
  end_time       TIME NOT NULL,
  is_available   BOOLEAN NOT NULL DEFAULT TRUE
);

-- 5. Αίτημα για ραντεβού (appointment request)
CREATE TABLE appointment_requests (
  id                      SERIAL PRIMARY KEY,
  client_id               INTEGER NOT NULL
    REFERENCES clients(user_id)
    ON DELETE CASCADE,
  service_type            VARCHAR(50) NOT NULL,
  preferred_technician_id INTEGER
    REFERENCES technicians(user_id),
  requested_date          DATE NOT NULL,
  requested_time          TIME NOT NULL,
  status                  VARCHAR(20) NOT NULL
    CHECK (status IN ('Pending','Confirmed','Rejected'))
);

-- 6. Ραντεβού (appointments)
CREATE TABLE appointments (
  id                      SERIAL PRIMARY KEY,
  request_id              INTEGER UNIQUE NOT NULL
    REFERENCES appointment_requests(id)
    ON DELETE CASCADE,
  client_id               INTEGER NOT NULL
    REFERENCES clients(user_id)
    ON DELETE CASCADE,
  technician_id           INTEGER NOT NULL
    REFERENCES technicians(user_id)
    ON DELETE CASCADE,
  appointment_date        DATE NOT NULL,
  appointment_time        TIME NOT NULL,
  status                  VARCHAR(20) NOT NULL
    CHECK (status IN ('Confirmed','Cancelled'))
);

-- 7. Πληρωμές (payments)
CREATE TABLE payments (
  id           SERIAL PRIMARY KEY,
  appointment_id INTEGER NOT NULL
    REFERENCES appointments(id)
    ON DELETE CASCADE,
  amount       NUMERIC(10,2) NOT NULL,
  method       VARCHAR(20) NOT NULL
    CHECK (method IN ('online','cash')),
  status       VARCHAR(20) NOT NULL
    CHECK (status IN ('Pending','Completed','Failed'))
);

-- 8. Αξιολογήσεις (reviews)
CREATE TABLE reviews (
  id           SERIAL PRIMARY KEY,
  appointment_id INTEGER NOT NULL
    REFERENCES appointments(id)
    ON DELETE CASCADE,
  client_id    INTEGER NOT NULL
    REFERENCES clients(user_id),
  rating       SMALLINT NOT NULL
    CHECK (rating BETWEEN 1 AND 5),
  comment      TEXT
);

-- 9. Συνομιλίες & Μηνύματα (conversations & messages)
CREATE TABLE conversations (
  id           SERIAL PRIMARY KEY
);

CREATE TABLE conversation_users (
  conversation_id INTEGER NOT NULL
    REFERENCES conversations(id)
    ON DELETE CASCADE,
  user_id      INTEGER NOT NULL
    REFERENCES users(id)
    ON DELETE CASCADE,
  PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE messages (
  id              SERIAL PRIMARY KEY,
  conversation_id INTEGER NOT NULL
    REFERENCES conversations(id)
    ON DELETE CASCADE,
  sender_id       INTEGER NOT NULL
    REFERENCES users(id),
  content         TEXT NOT NULL,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE technician_services (
  technician_id INT NOT NULL,
  service VARCHAR(50) NOT NULL,
  PRIMARY KEY (technician_id, service),
  FOREIGN KEY (technician_id) REFERENCES technicians(user_id) ON DELETE CASCADE
);

CREATE TABLE history (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL
        REFERENCES clients(user_id)
        ON DELETE CASCADE,
    technician_id INTEGER NOT NULL
        REFERENCES technicians(user_id)
        ON DELETE SET NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
