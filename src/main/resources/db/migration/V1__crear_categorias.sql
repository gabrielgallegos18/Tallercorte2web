CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT ck_categoria_nombre CHECK (length(trim(nombre)) > 0)
);
CREATE UNIQUE INDEX ux_categoria_nombre ON categoria (lower(nombre));
