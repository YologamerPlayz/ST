

USE TL;

-- Εισαγωγή 10 χρηστών
INSERT INTO users (name, password, email, phone) VALUES
('Giorgos Papadopoulos', '1234567', 'giorgos@example.com', '6900000001'),
('Maria Ioannou','2345678', 'maria@example.com', '6900000002'),
('Kostas Nikolaou','3456789', 'kostas@example.com', '6900000003'),
('Eleni Papadaki','4567891', 'eleni@example.com', '6900000004'),
('Nikos Dimitriou','5678912', 'nikos@example.com', '6900000005'),
('Anna Georgiou','6789123', 'anna@example.com', '6900000006'),
('Petros Sagkanis','7891234', 'petros@example.com', '6900000007'),
('Sofia Papagianni','8912345', 'sofia@example.com', '6900000008'),
('Dimitris Vlahos','9123456', 'dimitris@example.com', '6900000009'),
('Ioanna Stathopoulou','123456789', 'ioanna@example.com', '6900000010');

-- Εισαγωγή 10 clients (ίδια user_ids με τους παραπάνω)
INSERT INTO clients (user_id, address, history) VALUES
(1, 'Athens, Greece', 'Καμία ιστορικότητα'),
(2, 'Thessaloniki, Greece', 'Παλαιός πελάτης'),
(3, 'Patra, Greece', 'Νέος πελάτης'),
(4, 'Heraklion, Greece', ''),
(5, 'Larisa, Greece', 'Επαναλαμβανόμενος πελάτης'),
(6, 'Volos, Greece', ''),
(7, 'Ioannina, Greece', ''),
(8, 'Kavala, Greece', 'Συχνή επικοινωνία'),
(9, 'Rhodes, Greece', ''),
(10, 'Chania, Greece', 'Προτιμά μετρητά');
