-- Borra toda la información de la base de datos antes de ejecutar las pruebas.

DELETE FROM public.order_detail;
DELETE FROM public.tango_order;
DELETE FROM public.order_status;
DELETE FROM public.role_privilege;

DELETE FROM public.product_tag;
DELETE FROM public.product;
DELETE FROM public.tag;
DELETE FROM public.category;

DELETE FROM public.role_privilege;
DELETE FROM public.user_role;

DELETE FROM public.role;
DELETE FROM public.privilege;
DELETE FROM public.tango_user;