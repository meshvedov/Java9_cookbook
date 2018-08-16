CREATE TABLE enums (
id integer PRIMARY KEY,
type VARCHAR NOT NULL,
value VARCHAR NOT NULL
);

ALTER TABLE enums ADD CONSTRAINT enums_unique_type_value UNIQUE (type, value);

CREATE TABLE person (
name VARCHAR NOT NULL,
address VARCHAR NOT NULL,
dob date NOT NULL,
ord integer DEFAULT 1 NOT NULL,
PRIMARY KEY (name,address,dob,ord)
);

CREATE TABLE traffic_unit (
id SERIAL PRIMARY KEY,
vehicle_type integer REFERENCES enums (id),
horse_power integer NOT NULL,
weight_pounds integer NOT NULL,
payload_pounds integer NOT NULL,
passengers_count integer NOT NULL,
speed_limit_mph double precision NOT NULL,
traction double precision NOT NULL,
road_condition integer REFERENCES enums (id),
tire_condition integer REFERENCES enums (id),
temperature integer NOT NULL
);

CREATE INDEX idx_traffic_unit_vehicle_type_passengers_count ON traffic_unit USING btree (vehicle_type,passengers_count);

create table test (name varchar not null );