

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


-- Εισαγωγή τεχνικών στον πίνακα users
INSERT INTO users (name, password, email, phone) VALUES
('Νίκος Ηλεκτρολόγος', 'pass123', 'nikos@tech.gr', '6900000001'),
('Γιώργος Ψυκτικός', 'pass123', 'giorgos@tech.gr', '6900000002'),
('Μαρία Υδραυλικός', 'pass123', 'maria@tech.gr', '6900000003'),
('Σπύρος Ηλεκτρολόγος', 'pass123', 'spyros@tech.gr', '6900000004'),
('Άννα Ψυκτικός', 'pass123', 'anna@tech.gr', '6900000005'),
('Δημήτρης Υδραυλικός', 'pass123', 'dimitris@tech.gr', '6900000006'),
('Κατερίνα Ηλεκτρολόγος', 'pass123', 'katerina@tech.gr', '6900000007'),
('Πέτρος Ψυκτικός', 'pass123', 'petros@tech.gr', '6900000008'),
('Ελένη Υδραυλικός', 'pass123', 'eleni@tech.gr', '6900000009'),
('Μιχάλης Ηλεκτρολόγος', 'pass123', 'michalis@tech.gr', '6900000010');

-- Εισαγωγή στον πίνακα technicians
-- Σημείωση: Υποθέτουμε ότι τα ids είναι από 1 έως 10
INSERT INTO technicians (user_id, specialty, rating) VALUES
(1, 'Ηλεκτρολόγος', 4.5),
(2, 'Ψυκτικός', 4.2),
(3, 'Υδραυλικός', 4.8),
(4, 'Ηλεκτρολόγος', 3.9),
(5, 'Ψυκτικός', 4.0),
(6, 'Υδραυλικός', 4.1),
(7, 'Ηλεκτρολόγος', 4.3),
(8, 'Ψυκτικός', 3.8),
(9, 'Υδραυλικός', 4.7),
(10, 'Ηλεκτρολόγος', 4.6);
