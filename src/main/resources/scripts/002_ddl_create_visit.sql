CREATE TABLE IF NOT EXISTS visits (
    visitor_id bigint NOT NULL,
    visited_id bigint NOT NULL,
    visit_date timestamp without time zone NOT NULL,
    CONSTRAINT visits_composite_pkey PRIMARY KEY (visitor_id, visited_id),
    CONSTRAINT visits_visitor_id_fkey FOREIGN KEY (visitor_id) REFERENCES users(user_id),
    CONSTRAINT visits_visited_id_fkey FOREIGN KEY (visited_id) REFERENCES users(user_id)
);