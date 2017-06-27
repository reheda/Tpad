-- https://stackoverflow.com/questions/899203/how-do-i-find-the-last-time-that-a-postgresql-database-has-been-updated
CREATE TABLE update_log(table_name text PRIMARY KEY, updated timestamp NOT NULL DEFAULT now());
CREATE FUNCTION stamp_update_log() RETURNS TRIGGER LANGUAGE 'plpgsql' AS $$
BEGIN
  INSERT INTO update_log(table_name) VALUES(TG_TABLE_NAME)
  ON CONFLICT (table_name) DO UPDATE 
  	SET updated = DEFAULT;
  RETURN NEW;
END
$$;
-- Repeat for each table you need to track:
CREATE TRIGGER attributes_stamp_update_log
 AFTER INSERT OR UPDATE OR DELETE ON attributes
 FOR EACH STATEMENT EXECUTE PROCEDURE stamp_update_log();