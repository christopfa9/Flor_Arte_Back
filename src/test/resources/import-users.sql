INSERT INTO tango_user (id, create_date, email, enabled, first_name, last_name, password, token_expired, address)
VALUES (101, '2024-08-02', 'admin@mail.com', TRUE, 'Admin', 'Admin', '$2a$10$cJ50MNIMmi/PP.0aLonXC.y8tK3iGZXwbbLT2OGKcbjJdNjrA9JLy', FALSE, '123 Admin');

INSERT INTO tango_user (id, create_date, email, enabled, first_name, last_name, password, token_expired, address)
VALUES (102, '2024-08-03', 'staff@mail.com', TRUE, 'Staff', 'Staff', '$2a$10$cJ50MNIMmi/PP.0aLonXC.y8tK3iGZXwbbLT2OGKcbjJdNjrA9JLy', FALSE, '456 Oak Avenue');

INSERT INTO tango_user (id, create_date, email, enabled, first_name, last_name, password, token_expired, address)
VALUES (103, '2024-08-04', 'user@mail.com', FALSE, 'User', 'User', '$2a$10$cJ50MNIMmi/PP.0aLonXC.y8tK3iGZXwbbLT2OGKcbjJdNjrA9JLy', FALSE, '789 Pine Lane');

INSERT INTO tango_user (id, create_date, email, enabled, first_name, last_name, password, token_expired, address)
VALUES (104, '2024-08-05', 'emma.brown@example.com', TRUE, 'Emma', 'Brown', '$2a$10$cJ50MNIMmi/PP.0aLonXC.y8tK3iGZXwbbLT2OGKcbjJdNjrA9JLy', FALSE, '321 Birch Road');

INSERT INTO tango_user (id, create_date, email, enabled, first_name, last_name, password, token_expired, address)
VALUES (105, '2024-08-06', 'liam.davis@example.com', TRUE, 'Liam', 'Davis', '$2a$10$cJ50MNIMmi/PP.0aLonXC.y8tK3iGZXwbbLT2OGKcbjJdNjrA9JLy', FALSE, '654 Cedar Street');

ALTER SEQUENCE category_seq RESTART WITH 110;

/** User-Role **/
INSERT INTO public.user_role (user_id, role_id) VALUES (101, 1);
INSERT INTO public.user_role (user_id, role_id) VALUES (102, 2);
INSERT INTO public.user_role (user_id, role_id) VALUES (103, 3);
INSERT INTO public.user_role (user_id, role_id) VALUES (104, 2);
INSERT INTO public.user_role (user_id, role_id) VALUES (105, 3);