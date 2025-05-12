CREATE SEQUENCE IF NOT EXISTS user_id_seq INCREMENT BY 32 MINVALUE 2333;

CREATE TABLE IF NOT EXISTS users (
    user_id bigint NOT NULL DEFAULT nextval('user_id_seq'),
    name text NOT NULL,
    age integer NOT NULL,
    created_at timestamp without time zone NOT NULL,
    status text NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);