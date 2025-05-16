CREATE SCHEMA IF NOT EXISTS retirement_staging;

CREATE TABLE lifestyle_deposits (
    id SERIAL PRIMARY KEY,
    lifestyleType character varying(20)[] NOT NULL,
    monthlyDeposit numeric(10,2)[] NOT NULL,
);