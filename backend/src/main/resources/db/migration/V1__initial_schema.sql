CREATE TABLE families
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_name VARCHAR(120) NOT NULL,
    invite_code VARCHAR(32)  NOT NULL UNIQUE,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE app_users
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id         BIGINT       NOT NULL,
    full_name         VARCHAR(120) NOT NULL,
    email             VARCHAR(190) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    role              VARCHAR(20)  NOT NULL,
    membership_status VARCHAR(20)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_family FOREIGN KEY (family_id) REFERENCES families (id),
    INDEX             idx_users_family(family_id)
) ENGINE=InnoDB;
CREATE TABLE farmers
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id   BIGINT       NOT NULL,
    farmer_code VARCHAR(30)  NOT NULL,
    name        VARCHAR(150) NOT NULL,
    phone       VARCHAR(20),
    village     VARCHAR(100),
    address     TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (id),
    UNIQUE KEY uq_farmer_code_family(family_id,farmer_code),
    INDEX       idx_farmers_family_name(family_id,name)
) ENGINE=InnoDB;
CREATE TABLE work_types
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id BIGINT       NOT NULL,
    name      VARCHAR(100) NOT NULL,
    active    BOOLEAN      NOT NULL DEFAULT TRUE,
    FOREIGN KEY (family_id) REFERENCES families (id),
    UNIQUE KEY uq_work_type_family(family_id, name)
) ENGINE=InnoDB;
CREATE TABLE work_entries
(
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id            BIGINT         NOT NULL,
    farmer_id            BIGINT         NOT NULL,
    work_type_id         BIGINT NULL,
    work_date            DATE           NOT NULL,
    location             VARCHAR(200),
    quantity_description VARCHAR(200),
    total_amount         DECIMAL(12, 2) NOT NULL DEFAULT 0,
    notes                TEXT,
    billing_status       VARCHAR(20)    NOT NULL DEFAULT 'UNBILLED',
    verification_status  VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_by           BIGINT         NOT NULL,
    created_at           TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version              BIGINT         NOT NULL DEFAULT 0,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (farmer_id) REFERENCES farmers (id),
    FOREIGN KEY (work_type_id) REFERENCES work_types (id),
    FOREIGN KEY (created_by) REFERENCES app_users (id),
    CHECK (total_amount >= 0),
    INDEX                idx_work_family_date(family_id,work_date),
    INDEX                idx_work_farmer_date(farmer_id,work_date),
    INDEX                idx_work_billing(family_id,billing_status,work_date)
) ENGINE=InnoDB;
CREATE TABLE invoices
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id      BIGINT         NOT NULL,
    farmer_id      BIGINT         NOT NULL,
    invoice_number VARCHAR(40)    NOT NULL,
    invoice_date   DATE           NOT NULL,
    due_date       DATE           NOT NULL,
    total_amount   DECIMAL(12, 2) NOT NULL,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ISSUED',
    notes          TEXT,
    created_by     BIGINT         NOT NULL,
    created_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (farmer_id) REFERENCES farmers (id),
    FOREIGN KEY (created_by) REFERENCES app_users (id),
    UNIQUE KEY uq_invoice_number_family(family_id,invoice_number),
    CHECK (total_amount >= 0),
    INDEX          idx_invoice_due(family_id,due_date,status),
    INDEX          idx_invoice_farmer_date(farmer_id,invoice_date)
) ENGINE=InnoDB;
CREATE TABLE invoice_items
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id     BIGINT         NOT NULL,
    invoice_id    BIGINT         NOT NULL,
    work_entry_id BIGINT         NOT NULL,
    description   VARCHAR(255),
    amount        DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    FOREIGN KEY (work_entry_id) REFERENCES work_entries (id),
    UNIQUE KEY uq_invoice_work(invoice_id,work_entry_id),
    UNIQUE KEY uq_work_billed(work_entry_id),
    CHECK (amount >= 0)
) ENGINE=InnoDB;
CREATE TABLE payments
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id        BIGINT         NOT NULL,
    farmer_id        BIGINT         NOT NULL,
    payment_date     DATE           NOT NULL,
    amount           DECIMAL(12, 2) NOT NULL,
    payment_mode     VARCHAR(30)    NOT NULL,
    reference_number VARCHAR(100),
    idempotency_key  VARCHAR(80)    NOT NULL,
    notes            TEXT,
    created_by       BIGINT         NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (farmer_id) REFERENCES farmers (id),
    FOREIGN KEY (created_by) REFERENCES app_users (id),
    UNIQUE KEY uq_payment_idempotency(family_id,idempotency_key),
    CHECK (amount > 0),
    INDEX            idx_payment_farmer_date(family_id,farmer_id,payment_date)
) ENGINE=InnoDB;
CREATE TABLE payment_allocations
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id        BIGINT         NOT NULL,
    payment_id       BIGINT         NOT NULL,
    invoice_id       BIGINT         NOT NULL,
    allocated_amount DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (payment_id) REFERENCES payments (id),
    FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    CHECK (allocated_amount > 0),
    INDEX            idx_alloc_invoice(invoice_id),
    INDEX            idx_alloc_payment(payment_id)
) ENGINE=InnoDB;
CREATE TABLE audit_logs
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id     BIGINT      NOT NULL,
    actor_user_id BIGINT NULL,
    action        VARCHAR(40) NOT NULL,
    entity_type   VARCHAR(60) NOT NULL,
    entity_id     BIGINT NULL,
    details       JSON,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (family_id) REFERENCES families (id),
    FOREIGN KEY (actor_user_id) REFERENCES app_users (id),
    INDEX         idx_audit_family_time(family_id,created_at)
) ENGINE=InnoDB;
