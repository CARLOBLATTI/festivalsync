CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    country VARCHAR(100),
    location VARCHAR(100), -- Città
    state VARCHAR(50) DEFAULT 'active', -- Stato
    event_id BIGINT,
    creation_date DATE NOT NULL, -- Data di creazione
    insert_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp di inserimento
    update_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp di aggiornamento
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    location VARCHAR(255),
    country VARCHAR(100), -- Paese dell'evento
    state VARCHAR(50) DEFAULT 'scheduled', -- Stato dell'evento
    artists_number INT,
    creation_date DATE NOT NULL, -- Data di creazione
    insert_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp di inserimento
    update_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp di aggiornamento
);

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT, -- Cambiato a BIGINT
    price DECIMAL(10, 2),
    availability INT,
    state VARCHAR(50) DEFAULT 'available', -- Stato del biglietto
    creation_date DATE NOT NULL, -- Data di creazione
    insert_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp di inserimento
    update_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp di aggiornamento
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE sold_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT, -- Cambiato a BIGINT
    buyer_name VARCHAR(255), -- Nome dell'acquirente
    buyer_email VARCHAR(255), -- Email dell'acquirente
    buyer_phone VARCHAR(20), -- Telefono dell'acquirente
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data di acquisto
    state VARCHAR(50) DEFAULT 'purchased', -- Stato del biglietto
    creation_date DATE NOT NULL, -- Data di creazione
    insert_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Timestamp di inserimento
    update_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Timestamp di aggiornamento
    FOREIGN KEY (ticket_id) REFERENCES tickets(id) ON DELETE CASCADE
);
