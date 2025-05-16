CREATE TABLE poi_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poi_id VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    description TEXT,
    tags JSON,
    photos JSON,
    website TEXT,
    rating DOUBLE,
    price DOUBLE,
    open_time VARCHAR(255),
    facility VARCHAR(255),
    address VARCHAR(500),
    type VARCHAR(255)
);