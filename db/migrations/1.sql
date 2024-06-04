CREATE TABLE bit_bucket_users (
 id uuid PRIMARY KEY,
 name text NOT NULL,
 account_id text NOT NULL
);

CREATE TABLE bit_bucket_pull_requests
(
    id         SERIAL PRIMARY KEY,
    title      text      NOT NULL,
    state      text      NOT NULL,
    author_id  uuid      REFERENCES bit_bucket_users (id),
    created_at timestamp NOT NULL,
    updated_at timestamp DEFAULT NULL
);

CREATE TABLE bit_bucket_activities
(
    id SERIAL PRIMARY KEY,
    pull_request_id integer REFERENCES bit_bucket_pull_requests (id),
    created_at timestamp NOT NULL,
    activity_type text NOT NULL,
    author_id uuid REFERENCES bit_bucket_users (id)
);
