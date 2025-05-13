CREATE TABLE IF NOT EXISTS lifestyle_deposits (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    deposit_amount DECIMAL(10,2) NOT NULL,
    deposit_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);
