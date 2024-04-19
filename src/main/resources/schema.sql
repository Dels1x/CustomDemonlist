CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(40) UNIQUE NOT NULL,
    "password" VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    oath_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS demonlist (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user" (id) NOT NULL,
    name VARCHAR(80) NOT NULL DEFAULT 'Demonlist',
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    is_multi BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS demon (
    id BIGSERIAL PRIMARY KEY,
    demonlist_id BIGINT REFERENCES "demonlist" NOT NULL,
    name VARCHAR(32) NOT NULL DEFAULT 'Demon',
    author VARCHAR(32),
    attempts_count INTEGER,
    enjoyment_rating INTEGER,
    order_index INTEGER NOT NULL
);