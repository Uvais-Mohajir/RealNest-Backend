CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    price DOUBLE NOT NULL,
    type VARCHAR(20) NOT NULL,
    city VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    date_listed TIMESTAMP,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_property_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS property_closures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    price DOUBLE NOT NULL,
    location VARCHAR(255) NOT NULL,
    closure_type VARCHAR(20) NOT NULL,
    closed_at TIMESTAMP,
    CONSTRAINT fk_closure_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_closure_user FOREIGN KEY (user_id) REFERENCES users(id)
);
