INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (1,1, 10, 1, 15.99,15.99);
INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (2,2, 10, 4, 15.99,63.96);
INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (3,3, 11, 1, 12.50,12.50);
INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (4,4, 12, 1, 18.00,18.00);
INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (5,4, 13, 2, 25.00,50.00);
INSERT INTO order_detail (id, order_id, product_id, quantity, price, total) VALUES (6,5, 14, 1, 10.99,10.99);

ALTER SEQUENCE order_detail_seq RESTART WITH 15;