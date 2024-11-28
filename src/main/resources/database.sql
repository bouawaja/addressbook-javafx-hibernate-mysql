CREATE DATABASE IF NOT EXISTS addressbook;
USE addressbook;

CREATE TABLE IF NOT EXISTS contact (
    id BIGINT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(255) NOT NULL,
    email_address VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
);

CREATE TABLE facture (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         contact_id BIGINT NOT NULL,
                         facture_number VARCHAR(255) NOT NULL UNIQUE,
                         description TEXT,
                         total DOUBLE NOT NULL,
                         is_paid BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (contact_id) REFERENCES contact(id) ON DELETE CASCADE
);

CREATE TABLE client_acompte (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Identifiant de l'acompte
                                facture_id BIGINT NOT NULL,  -- Référence à la facture concernée
                                amount DOUBLE NOT NULL,  -- Montant de l'acompte
                                paid_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- Date du paiement
                                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- Date de création de l'acompte
                                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (facture_id) REFERENCES facture(id) ON DELETE CASCADE  -- Clé étrangère vers la table facture
);

INSERT INTO contact(last_name, first_name, address, mobile_number, email_address) VALUES
('Rizal', 'Jose', 'Calamba, Laguna', '09161234568', 'joserizal@gmail.com'),
('Bonifacio', 'Andress', 'Tondo, Manila', '09161234569', 'andresbonifacio@gmail.com');

INSERT INTO facture ( contact_id, facture_number, description, total)
VALUES (1,'F001', 'Achat de matériel', 100.500);