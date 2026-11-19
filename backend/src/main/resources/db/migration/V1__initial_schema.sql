-- ============================================================
-- JKServices
-- V1: Initial database schema
-- Database: MySQL 8.0.16+
-- Engine: InnoDB
-- Charset: utf8mb4
--
-- Architecture:
-- Organization -> Businesses -> Business-scoped data
-- Global users -> Organization memberships -> Business memberships
--
-- Domains:
-- 1. Farming Services
-- 2. Event Management
-- 3. Shared customer and financial infrastructure
-- ============================================================

SET NAMES utf8mb4;

-- ============================================================
-- 1. ORGANIZATIONS
-- Tenant / account boundary
-- ============================================================

CREATE TABLE organizations
(
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    organization_name VARCHAR(150) NOT NULL,
    invite_code       VARCHAR(50)  NOT NULL,
    active            BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_organizations_invite_code (invite_code),
    UNIQUE KEY uk_organizations_id (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 2. APP USERS
-- Global identity. A user can belong to multiple organizations.
-- ============================================================

CREATE TABLE app_users
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_app_users_email (email),
    UNIQUE KEY uk_app_users_id (id),
    KEY           idx_app_users_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 3. ORGANIZATION MEMBERS
-- Connects global users to organizations.
-- ============================================================

CREATE TABLE organization_members
(
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    organization_id   BIGINT UNSIGNED NOT NULL,
    user_id           BIGINT UNSIGNED NOT NULL,

    role              VARCHAR(30) NOT NULL,
    membership_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    joined_at         TIMESTAMP NULL DEFAULT NULL,
    created_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_org_member_user (
        organization_id,
        user_id
        ),

    UNIQUE KEY uk_org_member_id_org (
        id,
        organization_id
        ),

    KEY               idx_org_members_user (user_id),
    KEY               idx_org_members_status (
        organization_id,
        membership_status
    ),

    CONSTRAINT fk_org_members_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (id),

    CONSTRAINT fk_org_members_user
        FOREIGN KEY (user_id)
            REFERENCES app_users (id),

    CONSTRAINT chk_org_member_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),

    CONSTRAINT chk_org_member_status
        CHECK (
            membership_status IN (
                                  'ACTIVE',
                                  'INVITED',
                                  'SUSPENDED'
                )
            )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 4. BUSINESSES
-- One organization can operate multiple businesses.
-- Each business has its own financial account boundary.
-- ============================================================

CREATE TABLE businesses
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    organization_id BIGINT UNSIGNED NOT NULL,

    business_name   VARCHAR(150) NOT NULL,
    business_type   VARCHAR(40)  NOT NULL,
    business_code   VARCHAR(50)  NOT NULL,

    contact_number  VARCHAR(20) NULL,
    email           VARCHAR(255) NULL,
    address         TEXT NULL,

    currency_code   CHAR(3)      NOT NULL DEFAULT 'INR',
    active          BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_business_code (
        organization_id,
        business_code
        ),

    UNIQUE KEY uk_business_id_org (
        id,
        organization_id
        ),

    KEY             idx_businesses_organization (
        organization_id,
        active
    ),

    CONSTRAINT fk_businesses_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (id),

    CONSTRAINT chk_business_type
        CHECK (
            business_type IN (
                              'FARMING_SERVICES',
                              'EVENT_MANAGEMENT'
                )
            ),

    CONSTRAINT chk_business_currency
        CHECK (currency_code REGEXP '^[A-Z]{3}$'
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 5. BUSINESS MEMBERS
-- Assigns organization members to individual businesses.
-- A shared employee can work in multiple businesses.
-- ============================================================


CREATE TABLE business_members
(
    id                     BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    organization_id        BIGINT UNSIGNED NOT NULL,
    business_id            BIGINT UNSIGNED NOT NULL,
    organization_member_id BIGINT UNSIGNED NOT NULL,

    role                   VARCHAR(30) NOT NULL,
    membership_status      VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',

    created_at             TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_business_member (
        business_id,
        organization_member_id
        ),

    KEY                    idx_business_members_org (
        organization_id
    ),

    KEY                    idx_business_members_status (
        business_id,
        membership_status
    ),

    -- Composite FK: business_id + organization_id
    KEY                    idx_business_members_business_org (
        business_id,
        organization_id
    ),

    -- Composite FK: organization_member_id + organization_id
    KEY                    idx_business_members_org_member_org (
        organization_member_id,
        organization_id
    ),

    CONSTRAINT fk_business_members_business
        FOREIGN KEY (business_id, organization_id)
            REFERENCES businesses (id, organization_id),

    CONSTRAINT fk_business_members_org_member
        FOREIGN KEY (organization_member_id, organization_id)
            REFERENCES organization_members (id, organization_id),

    CONSTRAINT chk_business_member_role
        CHECK (
            role IN (
                     'BUSINESS_ADMIN',
                     'MANAGER',
                     'OPERATOR',
                     'VIEWER'
                )
            ),

    CONSTRAINT chk_business_member_status
        CHECK (
            membership_status IN (
                                  'ACTIVE',
                                  'INVITED',
                                  'SUSPENDED'
                )
            )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- 6. CUSTOMERS
-- Common customer model, scoped to a business.
-- ============================================================

CREATE TABLE customers
(
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id   BIGINT UNSIGNED NOT NULL,

    customer_code VARCHAR(50)  NOT NULL,
    name          VARCHAR(150) NOT NULL,

    phone         VARCHAR(20) NULL,
    email         VARCHAR(255) NULL,
    address       TEXT NULL,

    active        BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_customer_code (
        business_id,
        customer_code
        ),

    UNIQUE KEY uk_customer_id_business (
        id,
        business_id
        ),

    KEY           idx_customers_business_name (
        business_id,
        name
    ),

    KEY           idx_customers_business_phone (
        business_id,
        phone
    ),

    CONSTRAINT fk_customers_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 7. FARMERS
-- Farming-specific extension of customers.
-- ============================================================


CREATE TABLE farmers
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,

    farmer_code VARCHAR(50) NOT NULL,
    village     VARCHAR(120) NULL,

    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_farmer_customer (
        customer_id
        ),

    UNIQUE KEY uk_farmer_code (
        business_id,
        farmer_code
        ),

    UNIQUE KEY uk_farmer_id_business_customer (
        id,
        business_id,
        customer_id
        ),

    KEY         idx_farmers_customer_business (
        customer_id,
        business_id
    ),

    CONSTRAINT fk_farmers_customer
        FOREIGN KEY (customer_id, business_id)
            REFERENCES customers (id, business_id),

    CONSTRAINT fk_farmers_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- 8. WORK TYPES
-- Farming Services domain only.
-- ============================================================

CREATE TABLE work_types
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id BIGINT UNSIGNED NOT NULL,

    name        VARCHAR(100) NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_work_type_name (
        business_id,
        name
        ),

    UNIQUE KEY uk_work_type_id_business (
        id,
        business_id
        ),

    KEY         idx_work_types_business_active (
        business_id,
        active
    ),

    CONSTRAINT fk_work_types_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 9. WORK ENTRIES
-- Farming work performed for a farmer.
-- customer_id is retained here to enforce customer consistency
-- through composite foreign keys.
-- ============================================================

CREATE TABLE work_entries
(
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id          BIGINT UNSIGNED NOT NULL,

    farmer_id            BIGINT UNSIGNED NOT NULL,
    customer_id          BIGINT UNSIGNED NOT NULL,
    work_type_id         BIGINT UNSIGNED NULL,

    work_date            DATE           NOT NULL,
    location             VARCHAR(255) NULL,
    quantity_description VARCHAR(255) NULL,

    total_amount         DECIMAL(14, 2) NOT NULL DEFAULT 0.00,

    notes                TEXT NULL,

    billing_status       VARCHAR(20)    NOT NULL DEFAULT 'UNBILLED',
    verification_status  VARCHAR(20)    NOT NULL DEFAULT 'PENDING',

    created_by           BIGINT UNSIGNED NOT NULL,

    created_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    version              BIGINT UNSIGNED NOT NULL DEFAULT 0,

    PRIMARY KEY (id),

    UNIQUE KEY uk_work_entry_id_business_customer (
        id,
        business_id,
        customer_id
        ),

    KEY                  idx_work_entries_business_date (
        business_id,
        work_date
    ),

    KEY                  idx_work_entries_farmer_date (
        farmer_id,
        work_date
    ),

    KEY                  idx_work_entries_farmer_business_customer (
        farmer_id,
        business_id,
        customer_id
    ),

    KEY                  idx_work_entries_billing (
        business_id,
        billing_status,
        work_date
    ),

    KEY                  idx_work_entries_verification (
        business_id,
        verification_status
    ),

    KEY                  idx_work_entries_work_type (
        work_type_id,
        business_id
    ),

    KEY                  idx_work_entries_creator (
        created_by
    ),

    CONSTRAINT fk_work_entries_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id),

    CONSTRAINT fk_work_entries_farmer
        FOREIGN KEY (farmer_id, business_id, customer_id)
            REFERENCES farmers (id, business_id, customer_id),

    CONSTRAINT fk_work_entries_work_type
        FOREIGN KEY (work_type_id, business_id)
            REFERENCES work_types (id, business_id),

    CONSTRAINT fk_work_entries_creator
        FOREIGN KEY (created_by)
            REFERENCES app_users (id),

    CONSTRAINT chk_work_entries_amount
        CHECK (total_amount >= 0),

    CONSTRAINT chk_work_entries_billing
        CHECK (
            billing_status IN (
                               'UNBILLED',
                               'BILLED'
                )
            ),

    CONSTRAINT chk_work_entries_verification
        CHECK (
            verification_status IN (
                                    'PENDING',
                                    'VERIFIED',
                                    'REJECTED'
                )
            )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- 10. EVENTS
-- Event Management domain only.
-- ============================================================

CREATE TABLE events
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id     BIGINT UNSIGNED NOT NULL,

    event_code      VARCHAR(50)  NOT NULL,
    event_name      VARCHAR(180) NOT NULL,
    event_type      VARCHAR(100) NULL,

    event_date      DATE         NOT NULL,
    end_date        DATE NULL,

    venue           VARCHAR(255) NULL,
    expected_guests INT UNSIGNED NULL,

    status          VARCHAR(30)  NOT NULL DEFAULT 'PLANNED',
    notes           TEXT NULL,

    created_by      BIGINT UNSIGNED NOT NULL,

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_event_code (
        business_id,
        event_code
        ),

    UNIQUE KEY uk_event_id_business (
        id,
        business_id
        ),

    KEY             idx_events_business_date (
        business_id,
        event_date
    ),

    KEY             idx_events_business_status (
        business_id,
        status
    ),

    KEY             idx_events_creator (created_by),

    CONSTRAINT fk_events_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id),

    CONSTRAINT fk_events_creator
        FOREIGN KEY (created_by)
            REFERENCES app_users (id),

    CONSTRAINT chk_events_dates
        CHECK (
            end_date IS NULL OR end_date >= event_date
            ),

    CONSTRAINT chk_events_status
        CHECK (
            status IN (
                       'PLANNED',
                       'CONFIRMED',
                       'IN_PROGRESS',
                       'COMPLETED',
                       'CANCELLED'
                )
            )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 11. EVENT BOOKINGS
-- Customer bookings for event services.
-- ============================================================


CREATE TABLE event_bookings
(
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id         BIGINT UNSIGNED NOT NULL,

    customer_id         BIGINT UNSIGNED NOT NULL,
    event_id            BIGINT UNSIGNED NULL,

    booking_number      VARCHAR(50)    NOT NULL,
    booking_date        DATE           NOT NULL,

    service_description TEXT           NOT NULL,
    agreed_amount       DECIMAL(14, 2) NOT NULL DEFAULT 0.00,

    status              VARCHAR(30)    NOT NULL DEFAULT 'PENDING',
    notes               TEXT NULL,

    created_by          BIGINT UNSIGNED NOT NULL,

    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_booking_number (
        business_id,
        booking_number
        ),

    UNIQUE KEY uk_booking_id_business_customer (
        id,
        business_id,
        customer_id
        ),

    KEY                 idx_bookings_business_date (
        business_id,
        booking_date
    ),

    KEY                 idx_bookings_customer_business_date (
        customer_id,
        business_id,
        booking_date
    ),

    KEY                 idx_bookings_event (
        event_id,
        business_id
    ),

    KEY                 idx_bookings_status (
        business_id,
        status
    ),

    KEY                 idx_bookings_creator (
        created_by
    ),

    CONSTRAINT fk_bookings_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id),

    CONSTRAINT fk_bookings_customer
        FOREIGN KEY (customer_id, business_id)
            REFERENCES customers (id, business_id),

    CONSTRAINT fk_bookings_event
        FOREIGN KEY (event_id, business_id)
            REFERENCES events (id, business_id),

    CONSTRAINT fk_bookings_creator
        FOREIGN KEY (created_by)
            REFERENCES app_users (id),

    CONSTRAINT chk_bookings_amount
        CHECK (agreed_amount >= 0),

    CONSTRAINT chk_bookings_status
        CHECK (
            status IN (
                       'PENDING',
                       'CONFIRMED',
                       'COMPLETED',
                       'CANCELLED'
                )
            )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 12. INVOICES
-- Financial records are scoped to a single business.
-- ============================================================


CREATE TABLE invoices
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    business_id    BIGINT UNSIGNED NOT NULL,
    customer_id    BIGINT UNSIGNED NOT NULL,

    invoice_number VARCHAR(50)    NOT NULL,

    invoice_date   DATE           NOT NULL,
    due_date       DATE NULL,

    total_amount   DECIMAL(14, 2) NOT NULL DEFAULT 0.00,

    status         VARCHAR(30)    NOT NULL DEFAULT 'DRAFT',
    notes          TEXT NULL,

    created_by     BIGINT UNSIGNED NOT NULL,

    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_invoice_number (
        business_id,
        invoice_number
        ),

    UNIQUE KEY uk_invoice_id_business_customer (
        id,
        business_id,
        customer_id
        ),

    KEY            idx_invoices_business_date (
        business_id,
        invoice_date
    ),

    KEY            idx_invoices_customer_business_date (
        customer_id,
        business_id,
        invoice_date
    ),

    KEY            idx_invoices_business_status (
        business_id,
        status
    ),

    KEY            idx_invoices_due_date (
        business_id,
        due_date,
        status
    ),

    KEY            idx_invoices_creator (created_by),

    CONSTRAINT fk_invoices_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id),

    CONSTRAINT fk_invoices_customer
        FOREIGN KEY (customer_id, business_id)
            REFERENCES customers (id, business_id),

    CONSTRAINT fk_invoices_creator
        FOREIGN KEY (created_by)
            REFERENCES app_users (id),

    CONSTRAINT chk_invoices_amount
        CHECK (total_amount >= 0),

    CONSTRAINT chk_invoices_dates
        CHECK (
            due_date IS NULL OR due_date >= invoice_date
            ),

    CONSTRAINT chk_invoices_status
        CHECK (
            status IN (
                       'DRAFT',
                       'ISSUED',
                       'PARTIALLY_PAID',
                       'PAID',
                       'CANCELLED'
                )
            )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- 13. INVOICE ITEMS
--
-- An item can refer to:
-- 1. A farming work entry
-- 2. An event booking
-- 3. A manual invoice line
--
-- At most one source may be specified.
-- A source can only be invoiced once.
-- Composite FKs enforce business and customer consistency.
-- ============================================================

CREATE TABLE invoice_items
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    business_id      BIGINT UNSIGNED NOT NULL,
    customer_id      BIGINT UNSIGNED NOT NULL,
    invoice_id       BIGINT UNSIGNED NOT NULL,

    work_entry_id    BIGINT UNSIGNED NULL,
    event_booking_id BIGINT UNSIGNED NULL,

    description      VARCHAR(500)   NOT NULL,

    quantity         DECIMAL(12, 3) NOT NULL DEFAULT 1.000,
    unit_price       DECIMAL(14, 2) NOT NULL DEFAULT 0.00,
    amount           DECIMAL(14, 2) NOT NULL DEFAULT 0.00,

    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_invoice_item_work_entry (
        work_entry_id
        ),

    UNIQUE KEY uk_invoice_item_event_booking (
        event_booking_id
        ),

    KEY              idx_invoice_items_invoice (
        invoice_id,
        business_id,
        customer_id
    ),

    KEY              idx_invoice_items_work (
        work_entry_id,
        business_id,
        customer_id
    ),

    KEY              idx_invoice_items_booking (
        event_booking_id,
        business_id,
        customer_id
    ),

    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (invoice_id, business_id, customer_id)
            REFERENCES invoices (id, business_id, customer_id),

    CONSTRAINT fk_invoice_items_work_entry
        FOREIGN KEY (work_entry_id, business_id, customer_id)
            REFERENCES work_entries (id, business_id, customer_id),

    CONSTRAINT fk_invoice_items_event_booking
        FOREIGN KEY (event_booking_id, business_id, customer_id)
            REFERENCES event_bookings (id, business_id, customer_id),

    CONSTRAINT chk_invoice_item_source
        CHECK (
            NOT (
                work_entry_id IS NOT NULL
                    AND event_booking_id IS NOT NULL
                )
            ),

    CONSTRAINT chk_invoice_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_invoice_item_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_invoice_item_amount
        CHECK (amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 14. PAYMENTS
-- Each payment belongs to one customer and one business.
-- ============================================================


CREATE TABLE payments
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    business_id      BIGINT UNSIGNED NOT NULL,
    customer_id      BIGINT UNSIGNED NOT NULL,

    payment_date     DATE           NOT NULL,
    amount           DECIMAL(14, 2) NOT NULL,

    payment_mode     VARCHAR(30)    NOT NULL,
    reference_number VARCHAR(100) NULL,

    idempotency_key  VARCHAR(100) NULL,

    notes            TEXT NULL,

    created_by       BIGINT UNSIGNED NOT NULL,

    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_payment_id_business_customer (
        id,
        business_id,
        customer_id
        ),

    UNIQUE KEY uk_payment_idempotency (
        business_id,
        idempotency_key
        ),

    KEY              idx_payments_customer_business_date (
        customer_id,
        business_id,
        payment_date
    ),

    KEY              idx_payments_business_date (
        business_id,
        payment_date
    ),

    KEY              idx_payments_reference (
        business_id,
        reference_number
    ),

    KEY              idx_payments_creator (created_by),

    CONSTRAINT fk_payments_business
        FOREIGN KEY (business_id)
            REFERENCES businesses (id),

    CONSTRAINT fk_payments_customer
        FOREIGN KEY (customer_id, business_id)
            REFERENCES customers (id, business_id),

    CONSTRAINT fk_payments_creator
        FOREIGN KEY (created_by)
            REFERENCES app_users (id),

    CONSTRAINT chk_payments_amount
        CHECK (amount > 0),

    CONSTRAINT chk_payments_mode
        CHECK (
            payment_mode IN (
                             'CASH',
                             'UPI',
                             'BANK_TRANSFER',
                             'CHEQUE',
                             'CARD',
                             'OTHER'
                )
            )

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- 15. PAYMENT ALLOCATIONS
-- Connects customer payments to customer invoices.
-- Composite FKs prevent cross-business and cross-customer
-- allocations.
-- ============================================================

CREATE TABLE payment_allocations
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    business_id      BIGINT UNSIGNED NOT NULL,
    customer_id      BIGINT UNSIGNED NOT NULL,

    payment_id       BIGINT UNSIGNED NOT NULL,
    invoice_id       BIGINT UNSIGNED NOT NULL,

    allocated_amount DECIMAL(14, 2) NOT NULL,

    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_payment_invoice_allocation (
        payment_id,
        invoice_id
        ),

    KEY              idx_payment_allocations_invoice (
        invoice_id,
        business_id,
        customer_id
    ),

    KEY              idx_payment_allocations_payment (
        payment_id,
        business_id,
        customer_id
    ),

    CONSTRAINT fk_payment_allocations_payment
        FOREIGN KEY (payment_id, business_id, customer_id)
            REFERENCES payments (id, business_id, customer_id),

    CONSTRAINT fk_payment_allocations_invoice
        FOREIGN KEY (invoice_id, business_id, customer_id)
            REFERENCES invoices (id, business_id, customer_id),

    CONSTRAINT chk_payment_allocation_amount
        CHECK (allocated_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- ============================================================
-- 16. AUDIT LOGS
-- Organization-level and optional business-level audit trail.
-- ============================================================


CREATE TABLE audit_logs
(
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    organization_id BIGINT UNSIGNED NOT NULL,
    business_id     BIGINT UNSIGNED NULL,

    actor_user_id   BIGINT UNSIGNED NULL,

    action          VARCHAR(100) NOT NULL,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       VARCHAR(100) NULL,

    details         JSON NULL,

    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    KEY             idx_audit_org_time (
        organization_id,
        created_at
    ),

    KEY             idx_audit_business_time (
        business_id,
        created_at
    ),

    KEY             idx_audit_business_org (
        business_id,
        organization_id
    ),

    KEY             idx_audit_entity (
        entity_type,
        entity_id
    ),

    KEY             idx_audit_actor (
        actor_user_id,
        created_at
    ),

    CONSTRAINT fk_audit_organization
        FOREIGN KEY (organization_id)
            REFERENCES organizations (id),

    CONSTRAINT fk_audit_business
        FOREIGN KEY (business_id, organization_id)
            REFERENCES businesses (id, organization_id),

    CONSTRAINT fk_audit_actor
        FOREIGN KEY (actor_user_id)
            REFERENCES app_users (id)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;



-- ============================================================
-- END OF JKServices V1 INITIAL SCHEMA
-- ============================================================
