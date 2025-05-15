CREATE TABLE poi_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poi_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    description TEXT,
    tags JSON,          -- 存储标签数组，例如 ["历史", "文化"]
    photos JSON,        -- 存储图片URL数组，例如 ["url1", "url2"]
    website TEXT,
    rating DOUBLE,
    price DOUBLE,
    open_time VARCHAR(255),
    facility VARCHAR(255),
    address VARCHAR(255),
    type VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
