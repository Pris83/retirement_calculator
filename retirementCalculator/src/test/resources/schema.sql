CREATE SCHEMA IF NOT EXISTS retirement_staging;

CREATE TABLE IF NOT EXISTS retirement_staging.lifestyle_deposits (
    id SERIAL PRIMARY KEY,
    lifestyle_type VARCHAR(20) NOT NULL,
    monthly_deposit NUMERIC(10,2) NOT NULL
);
