-- NOTA: Esta migración la ejecute por primera vez vacía.
-- Posteriormente incorporé su contenido, por lo que la
-- primera ejecución no produjo modificaciones en la base de datos.
-- Dejo aca constancia por si el flyway dice que hice una modificacion.

ALTER TABLE listas
ADD COLUMN usuario_id BIGINT;

ALTER TABLE listas
ADD CONSTRAINT fk_listas_usuario
FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

CREATE INDEX idx_listas_usuario_id
ON listas(usuario_id);