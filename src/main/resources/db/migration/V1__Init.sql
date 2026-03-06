CREATE TABLE credentials(
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            login VARCHAR(50) NOT NULL,
                            hashed_password VARCHAR(255) NOT NULL
);

CREATE TABLE items(
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      item_name VARCHAR(50) NOT NULL,
                      type_of_item VARCHAR(50) NOT NULL,
                      ingredients VARCHAR(200) NOT NULL,
                      amount_of_pieces INT NOT NULL,
                      price INT NOT NULL,
                      description VARCHAR(500) NOT NULL,
                      mass INT NOT NULL,
                      kcal INT NOT NULL,
                      image_url VARCHAR(300) NOT NULL,
                      has_multiple_components BOOLEAN NOT NULL,
                      is_changeable BOOLEAN NOT NULL,
                      is_available BOOLEAN NOT NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL
);

CREATE TABLE clients(
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        is_active BOOLEAN NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        login VARCHAR(50) NOT NULL,
                        password VARCHAR(100) NOT NULL,
                        is_authorised BOOLEAN NOT NULL,
                        date_of_birth DATE NOT NULL,
                        phone_number VARCHAR(20) NOT NULL,
                        email VARCHAR(40) NOT NULL,
                        is_advertisable BOOLEAN NOT NULL,
                        is_profile_complete BOOLEAN NOT NULL,
                        is_online_check_on BOOLEAN NOT NULL,
                        bonuses_amount INT NOT NULL DEFAULT 0,
                        credentials_id BIGINT NOT NULL,
                        CONSTRAINT fk_client_credentials
                            FOREIGN KEY (credentials_id) REFERENCES credentials(id)
);

CREATE TABLE client_addresses(
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 is_default BOOLEAN NOT NULL,
                                 city VARCHAR(50) NOT NULL,
                                 street VARCHAR(50) NOT NULL,
                                 house_number VARCHAR(50) NOT NULL,
                                 apartment VARCHAR(50) NOT NULL,
                                 postal_code VARCHAR(50) NOT NULL,
                                 calculated_delivery_time DATETIME NOT NULL,
                                 client_id BIGINT NOT NULL,
                                 CONSTRAINT fk_client_addresses
                                     FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE favourite_items(
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                added_at DATETIME NOT NULL,
                                last_viewed_at DATETIME NOT NULL,
                                removed_at DATETIME NULL,
                                priority INT NOT NULL,
                                times_ordered INT NOT NULL,
                                item_id BIGINT NOT NULL,
                                client_id BIGINT NOT NULL,
                                CONSTRAINT fk_favourite_items_items
                                    FOREIGN KEY (item_id) REFERENCES items(id),
                                CONSTRAINT fk_favourite_items_clients
                                    FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE orders(
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       price INT NOT NULL,
                       is_delivery_free BOOLEAN NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       commentary VARCHAR(300) NULL,
                       created_at DATETIME NOT NULL,
                       delivered_at DATETIME NOT NULL,
                       estimated_delivery_time BIGINT NULL,
                       are_bonuses_used BOOLEAN NOT NULL,
                       is_promo_code_used BOOLEAN NOT NULL,
                       client_id BIGINT NOT NULL,
                       client_address_id BIGINT NOT NULL,
                       CONSTRAINT fk_items_client
                           FOREIGN KEY (client_id) REFERENCES clients(id),
                       CONSTRAINT fk_items_addresses
                           FOREIGN KEY (client_address_id) REFERENCES client_addresses(id)
);



