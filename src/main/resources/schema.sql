CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(40) NOT NULL,
    pfp_url VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    discord_id VARCHAR(255) UNIQUE,
    google_id VARCHAR(255) UNIQUE
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
    difficulty VARCHAR(32) CHECK (difficulty IN ('AUTO', 'EASY', 'NORMAL', 'HARD', 'HARDER', 'INSANE', 'EASY_DEMON',
                                                 'MEDIUM_DEMON', 'HARD_DEMON', 'INSANE_DEMON', 'EXTREME_DEMON',
                                                 'OBSIDIAN_DEMON', 'AZURITE_DEMON', 'AMETHYST_DEMON', 'ONYX_DEMON',
                                                 'PEARL_DEMON', 'DIAMOND_DEMON','RUBY_DEMON','EMERALD_DEMON',
                                                 'JADE_DEMON', 'SAPPHIRE_DEMON', 'PLATINUM_DEMON',
                                                 'AMBER_DEMON', 'GOLD_DEMON', 'SILVER_DEMON', 'BRONZE_DEMON',
                                                 'BEGINNER_DEMON')),
    date_of_completion DATE,
    worst_fail SMALLINT,
    nlw_tier VARCHAR(32),
    gddl_tier INTEGER,
    aredl_placement INTEGER,
    attempts_count INTEGER,
    enjoyment_rating INTEGER
);
