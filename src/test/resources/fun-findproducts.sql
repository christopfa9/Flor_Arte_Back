-- Se reemplazan los $$ por '. Ya que en este caso los $$ no son válidos
-- Escapar los '' existentes.

DROP FUNCTION IF EXISTS fun_findproducts;
CREATE OR REPLACE FUNCTION fun_findproducts(param_query IN TEXT, param_category_id IN BIGINT, param_tag_ids IN bigint[])
RETURNS SETOF product
LANGUAGE plpgsql
AS
--$$
'
DECLARE
--
BEGIN
RETURN QUERY(
    SELECT p.*
		FROM product p
        -- Si un parametro es nulo, este no se toma en cuenta (se regresan todos los elementos)
		WHERE (param_query IS NULL OR LOWER(p.name) LIKE CONCAT(''%'',LOWER(param_query),''%''))
			AND (param_category_id IS NULL OR p.category_id = param_category_id)
			AND ((param_tag_ids IS NULL OR array_length(param_tag_ids, 1) = 0)
                    -- <@ : Todos los elementos de arrayA están en arrayB
					OR param_tag_ids <@ (
                        -- array_agg transforma todas las filas regresadas por la consulta en un arreglo.
						SELECT array_agg(pt.tag_id)
						FROM product_tag pt
						WHERE p.id = pt.product_id
					)
			)
	);
END;
';
--$$

-- https://www.postgresql.org/docs/current/sql-createfunction.html
-- https://neon.tech/postgresql/postgresql-plpgsql/postgresql-create-function
-- https://www.postgresql.org/docs/current/xfunc-sql.html#XFUNC-SQL-FUNCTIONS-RETURNING-SET
-- https://www.postgresql.org/docs/current/functions-array.html