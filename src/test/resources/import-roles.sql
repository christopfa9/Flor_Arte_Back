/** Role **/
INSERT INTO public.role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.role (id, name) VALUES (2, 'ROLE_STAFF');
INSERT INTO public.role (id, name) VALUES (3, 'ROLE_USER');

ALTER SEQUENCE role_seq RESTART WITH 5;

/** Role-Privilege **/
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (1, 1);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (1, 2);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (2, 1);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (2, 2);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (3, 1);
INSERT INTO public.role_privilege (role_id, privilege_id) VALUES (3, 2);