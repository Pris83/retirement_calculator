--CREATE TABLE lifestyle_deposits (
--    id SERIAL PRIMARY KEY,
--    lifestyleType character varying(20)[] NOT NULL,
--    monthlyDeposit numeric(10,2)[] NOT NULL,
--);


--CREATE TABLE IF NOT EXISTS retirement_staging.lifestyle_deposits
--(
--    id integer NOT NULL DEFAULT nextval('retirement_staging.lifestyle_deposits_id_seq'::regclass),
--    "lifestyleType" character varying(20) COLLATE pg_catalog."default" NOT NULL,
--    "monthlyDeposits" numeric(10,2)[] NOT NULL,
--    CONSTRAINT lifestyle_deposits_pkey PRIMARY KEY (id)
--)