INSERT INTO order_status (id, name) VALUES (1, 'Pendiente');
INSERT INTO order_status (id, name) VALUES (2, 'Completo');
INSERT INTO order_status (id, name) VALUES (3, 'Cancelado');

ALTER SEQUENCE order_status_seq RESTART WITH 15;