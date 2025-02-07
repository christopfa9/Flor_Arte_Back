-- src/test/resources/import-orders.sql
INSERT INTO tango_order (id, date, user_id, total, order_status_id, delivery_date, additional_information) VALUES (1, '2024-08-31', 101, 15.99, 1, '2024-09-02', 'Entrega a las 3pm en la dirección indicada');
INSERT INTO tango_order (id, date, user_id, total, order_status_id, delivery_date, additional_information) VALUES (2, '2024-08-29', 102, 63.94, 2, '2024-09-01', 'Incluir una tarjeta de felicitaciones');
INSERT INTO tango_order (id, date, user_id, total, order_status_id, delivery_date, additional_information) VALUES (3, '2024-08-28', 103, 12.50, 1, '2024-08-28', 'Entrega urgente');
INSERT INTO tango_order (id, date, user_id, total, order_status_id, delivery_date, additional_information) VALUES (4, '2024-08-30', 104, 68.00, 2, '2024-08-30', 'Entregar 1pm');
INSERT INTO tango_order (id, date, user_id, total, order_status_id, delivery_date, additional_information) VALUES (5, '2024-08-28', 105, 10.99, 2, '2024-08-29', 'Entrega urgente');


ALTER SEQUENCE tango_order_seq RESTART WITH 15;