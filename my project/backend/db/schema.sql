-- Oracle schema for Blood Donation app
-- Configure credentials via ORACLE_URL / ORACLE_USER / ORACLE_PASS

CREATE TABLE user_credentials (
    number VARCHAR2(32) PRIMARY KEY,
    password VARCHAR2(128) NOT NULL
);

CREATE TABLE donors (
    id VARCHAR2(64) PRIMARY KEY,
    name VARCHAR2(120) NOT NULL,
    dob VARCHAR2(20),
    blood_type VARCHAR2(8) NOT NULL,
    phone VARCHAR2(32) NOT NULL,
    email VARCHAR2(160) NOT NULL,
    address VARCHAR2(400),
    pincode VARCHAR2(16),
    location VARCHAR2(200),
    center VARCHAR2(160),
    history VARCHAR2(4000),
    surgeries VARCHAR2(4000),
    status VARCHAR2(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE TABLE match_requests (
    id VARCHAR2(64) PRIMARY KEY,
    fullname VARCHAR2(160) NOT NULL,
    email VARCHAR2(160) NOT NULL,
    bloodtype VARCHAR2(8) NOT NULL,
    pincode VARCHAR2(16),
    location VARCHAR2(200),
    note VARCHAR2(400),
    status VARCHAR2(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- Persisted matches between donors and requests, managed by admin actions
CREATE TABLE matches (
    match_id VARCHAR2(64) PRIMARY KEY,
    donor_id VARCHAR2(64),
    donor_name VARCHAR2(160),
    donor_phone VARCHAR2(32),
    donor_email VARCHAR2(160),
    donor_pincode VARCHAR2(16),
    request_id VARCHAR2(64),
    request_name VARCHAR2(160),
    request_email VARCHAR2(160),
    request_pincode VARCHAR2(16),
    blood_type VARCHAR2(8),
    location_match NUMBER(1),
    request_note VARCHAR2(400),
    status VARCHAR2(32),
    priority VARCHAR2(32),
    matched_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    updated_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE TABLE contact_messages (
    id VARCHAR2(64) PRIMARY KEY,
    name VARCHAR2(160) NOT NULL,
    email VARCHAR2(160) NOT NULL,
    message VARCHAR2(4000) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- Sample random-ish seed data (changeable later)
-- User credentials will be inserted when users register via the application

INSERT INTO donors (id, name, dob, blood_type, phone, email, address, pincode, center, history, surgeries)
VALUES ('d1', 'Aarav Singh', '1992-03-12', 'A+', '+91-9876543210', 'aarav@example.com', '12 MG Road, Delhi', '110001', 'Central Donor Center', 'N/A', 'None');
INSERT INTO donors (id, name, dob, blood_type, phone, email, address, pincode, center, history, surgeries)
VALUES ('d2', 'Isha Patel', '1989-07-21', 'O+', '+91-9123456780', 'isha@example.com', '44 Park St, Kolkata', '700016', 'Community Hospital', 'Healthy', 'Appendix 2018');
INSERT INTO donors (id, name, dob, blood_type, phone, email, address, pincode, center, history, surgeries)
VALUES ('d3', 'Rahul Mehta', '1995-11-02', 'B-', '+91-9988776655', 'rahul@example.com', '2 Residency Rd, Mumbai', '400001', 'Mobile Drive - Downtown', 'Allergic rhinitis', 'None');

INSERT INTO match_requests (id, fullname, email, bloodtype, pincode, note)
VALUES ('m1', 'Priya Kumar', 'priya@example.com', 'A+', '110001', 'Urgent for surgery');
INSERT INTO match_requests (id, fullname, email, bloodtype, pincode, note)
VALUES ('m2', 'Sanjay Rao', 'sanjay@example.com', 'O+', '700016', 'Need within 2 days');

INSERT INTO contact_messages (id, name, email, message)
VALUES ('c1', 'Visitor One', 'v1@example.com', 'Looking to volunteer');
INSERT INTO contact_messages (id, name, email, message)
VALUES ('c2', 'Visitor Two', 'v2@example.com', 'Need info about mobile drives');

COMMIT;
