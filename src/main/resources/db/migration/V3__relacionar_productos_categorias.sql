ALTER TABLE producto ADD COLUMN categoria_id INTEGER NOT NULL;
ALTER TABLE producto ADD CONSTRAINT fk_producto_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria(id);
CREATE INDEX ix_producto_categoria ON producto (categoria_id);
