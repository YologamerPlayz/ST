USE TL;

-- Εισαγωγή 10 χρηστών
INSERT INTO users (name, password, email, phone) VALUES
('Giorgos Tsarou', '1234567', 'giorgos@example.com', '6900000001'),
('Maria Ioannou','2345678', 'maria@example.com', '6900000002'),
('Kostas Nikolaou','3456789', 'kostas@example.com', '6900000003'),
('Eleni Papadaki','4567891', 'eleni@example.com', '6900000004'),
('Takis Hokan','5678912', 'pappous@example.com', '6900000005'),
('Aris Mougkopetros','6789123', 'aris@example.com', '6900000006'),
('Petros Sagkanis','7891234', 'petros@example.com', '6900000007'),
('Sofia Papagianni','8912345', 'sofia@example.com', '6900000008'),
('Thodoris Mamoutas','9123456', 'thodoris@example.com', '6900000009'),
('Ioanna Stathopoulou','123456789', 'ioanna@example.com', '6900000010');

-- Εισαγωγή 10 clients (ίδια user_ids με τους παραπάνω)
INSERT INTO clients (user_id, address) VALUES
(1, 'Vrina, Greece'),
(2, 'Thessaloniki, Greece'),
(3, 'Patra, Greece'),
(4, 'Heraklion, Greece'),
(5, 'Petri, Greece'),
(6, 'Volos, Greece'),
(7, 'Ioannina, Greece'),
(8, 'Kavala, Greece'),
(9, 'Rhodes, Greece'),
(10, 'Chania, Greece');


-- Εισαγωγή 10 τεχνικών (users)
INSERT INTO users (name, password, email, phone) VALUES
('Stelios Kalogeropoulos', 'elektro1', 'stelios@tech.com', '6900000101'),
('Niki Antoniou', 'coolair2', 'niki@tech.com', '6900000102'),
('Vasilis Raptis', 'hydra3', 'vasilis@tech.com', '6900000103'),
('Eirini Theodorou', 'elektro4', 'eirini@tech.com', '6900000104'),
('Giannis Laskaris', 'coolair5', 'giannis@tech.com', '6900000105'),
('Maria Konstantinou', 'hydra6', 'maria.k@tech.com', '6900000106'),
('Christos Mpouzis', 'elektro7', 'christos@tech.com', '6900000107'),
('Eleni Pappa', 'coolair8', 'eleni.p@tech.com', '6900000108'),
('Thanasis Karalis', 'hydra9', 'thanasis@tech.com', '6900000109'),
('Georgia Spiliou', 'elektro10', 'georgia@tech.com', '6900000110');

-- Εισαγωγή 10 technicians (ίδια user_ids με τους παραπάνω)
INSERT INTO technicians (user_id, specialty, rating) VALUES
(11, 'Ηλεκτρολόγος', 0),
(12, 'Ψυκτικός', 0),
(13, 'Υδραυλικός', 0),
(14, 'Ηλεκτρολόγος', 0),
(15, 'Ψυκτικός', 0),
(16, 'Υδραυλικός', 0),
(17, 'Ηλεκτρολόγος', 0),
(18, 'Ψυκτικός', 0),
(19, 'Υδραυλικός', 0),
(20, 'Ηλεκτρολόγος', 0);

INSERT INTO history (client_id, technician_id, date)
VALUES (1, 11, '2024-12-10 14:30:00'),
       (1, 11, '2025-01-15 10:00:00'),
       (1, 11, '2025-03-05 09:45:00');
