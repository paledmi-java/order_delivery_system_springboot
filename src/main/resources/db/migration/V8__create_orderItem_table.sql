CREATE TABLE order_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,

    price_snapshot INT NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT fk_orderItems_orders
                        FOREIGN KEY (order_id)
                            REFERENCES orders(id)
                            ON DELETE CASCADE,

    CONSTRAINT fk_orderItems_items
                        FOREIGN KEY (item_id)
                            REFERENCES items(id),

    INDEX idx_order_id (order_id),
    INDEX idx_item_id (item_id)
);