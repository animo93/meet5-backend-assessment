CREATE TABLE IF NOT EXISTS likes (
    liked_id bigint NOT NULL,
    liker_id bigint NOT NULL,
    liked_date timestamp without time zone NOT NULL,
    CONSTRAINT likes_composite_pkey PRIMARY KEY (liker_id, liked_id),
    CONSTRAINT likes_liker_id_fkey FOREIGN KEY (liker_id) REFERENCES users(user_id),
    CONSTRAINT likes_liked_id_fkey FOREIGN KEY (liked_id) REFERENCES users(user_id)
);