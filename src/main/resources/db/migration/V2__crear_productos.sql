CREATE TABLE producto (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    precio_venta NUMERIC(12, 2) NOT NULL CHECK (precio_venta >= 0),
    existencia INTEGER NOT NULL CHECK (existencia >= 0),
    CONSTRAINT ck_producto_codigo CHECK (length(trim(codigo)) > 0),
    CONSTRAINT ck_producto_nombre CHECK (length(trim(nombre)) > 0)
);
