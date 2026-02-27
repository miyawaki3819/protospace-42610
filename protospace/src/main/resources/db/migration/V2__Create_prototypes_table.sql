CREATE TABLE prototypes (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    catch_copy TEXT NOT NULL,
    concept TEXT NOT NULL,
    image_name VARCHAR(255) NOT NULL,
    image_type VARCHAR(100) NOT NULL,
    image_data BYTEA NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);
