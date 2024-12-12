CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(40) NOT NULL,
    pfp_url VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    access_token VARCHAR(255),
    refresh_token VARCHAR(255),
    token_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    discord_id VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS demonlist (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT REFERENCES person (id) NOT NULL,
    name VARCHAR(80) NOT NULL DEFAULT 'Demonlist',
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    is_multi BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS demon (
    id BIGSERIAL PRIMARY KEY,
    demonlist_id BIGINT REFERENCES "demonlist" NOT NULL,
    name VARCHAR(32) NOT NULL DEFAULT 'Demon',
    author VARCHAR(32),
    placement INTEGER NOT NULL,
    initial_placement INTEGER,
    difficulty VARCHAR(32),
    date_of_completion DATE,
    gddp_difficulty VARCHAR(32),
    nlw_tier VARCHAR(32),
    gddl_tier VARCHAR(32),
    aredl_placement INTEGER,
    attempts_count INTEGER,
    enjoyment_rating INTEGER
);
