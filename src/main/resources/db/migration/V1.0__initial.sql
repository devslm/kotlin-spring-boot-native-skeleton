CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(128) NOT NULL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    date_add TIMESTAMP
) WITHOUT ROWID;

CREATE INDEX IF NOT EXISTS devices_name_idx ON devices(name);
