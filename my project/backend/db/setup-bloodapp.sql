-- Create and seed BLOODAPP schema in Oracle XE (XEPDB1)
-- Requires SYSDBA privileges via OS authentication or provide SYS/SYSTEM credentials when running.

ALTER SESSION SET CONTAINER = XEPDB1;

BEGIN
  EXECUTE IMMEDIATE 'CREATE USER bloodapp IDENTIFIED BY bloodpass DEFAULT TABLESPACE users TEMPORARY TABLESPACE temp QUOTA UNLIMITED ON users';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE = -01920 THEN
      NULL; -- user already exists
    ELSE
      RAISE;
    END IF;
END;
/

GRANT CREATE SESSION TO bloodapp;
GRANT RESOURCE TO bloodapp;
GRANT CREATE TABLE TO bloodapp;
GRANT CREATE SEQUENCE TO bloodapp;
GRANT CREATE VIEW TO bloodapp;
GRANT UNLIMITED TABLESPACE TO bloodapp;

-- Connect as bloodapp and apply schema
CONNECT bloodapp/bloodpass@localhost:1521/XEPDB1
@schema.sql
EXIT;
