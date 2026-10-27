CREATE TABLE etiqueta (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT ck_etiqueta_nombre CHECK (length(trim(nombre)) > 0)
);
CREATE UNIQUE INDEX ux_etiqueta_nombre ON etiqueta (lower(nombre));
CREATE TABLE producto_etiqueta (
    producto_id INTEGER NOT NULL,
    etiqueta_id INTEGER NOT NULL,
    PRIMARY KEY (producto_id, etiqueta_id),
    CONSTRAINT fk_producto_etiqueta_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_producto_etiqueta_etiqueta
        FOREIGN KEY (etiqueta_id) REFERENCES etiqueta(id)
);
CREATE INDEX ix_producto_etiqueta_etiqueta ON producto_etiqueta (etiqueta_id);
