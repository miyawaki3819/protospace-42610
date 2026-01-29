-- Create users table for testing
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    encrypted_password VARCHAR(255),
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    profile TEXT,
    occupation TEXT,
    position TEXT
);
