CREATE TABLE roles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE client_roles(
    client_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (client_id, role_id),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE ,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

INSERT INTO roles(name)
VALUES ('ROLE_USER'),
('ROLE_ADMIN');