-- Un esquema por servicio: ningún servicio hace joins con las tablas de otro.
CREATE DATABASE IF NOT EXISTS identity_db   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS catalog_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS booking_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS backoffice_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON identity_db.*   TO 'app'@'%';
GRANT ALL PRIVILEGES ON catalog_db.*    TO 'app'@'%';
GRANT ALL PRIVILEGES ON booking_db.*    TO 'app'@'%';
GRANT ALL PRIVILEGES ON backoffice_db.* TO 'app'@'%';
FLUSH PRIVILEGES;
