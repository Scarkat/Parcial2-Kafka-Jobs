-- 1. Habilitar la extensión para generar UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS public.products_retry_jobs CASCADE;
DROP TABLE IF EXISTS public.product_retry_jobs CASCADE;
DROP TABLE IF EXISTS public.order_retry_jobs CASCADE;
DROP TABLE IF EXISTS public.payments_retry_jobs CASCADE;

-- ==========================================
-- TABLA DE PAGOS
-- ==========================================
CREATE TABLE public.payments_retry_jobs (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    entity_id varchar NOT NULL,
    payload jsonb NOT NULL,
    "action" varchar NOT NULL,
    attempt int4 DEFAULT 0 NOT NULL,
    global_status varchar DEFAULT 'SCHEDULED' NOT NULL,
    next_run_at timestamptz DEFAULT now() NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_Id_paymentsretryjobs" PRIMARY KEY (id)
);

CREATE INDEX idx_payments_retry_entity_id ON public.payments_retry_jobs USING btree (entity_id);
CREATE INDEX idx_payments_retry_run_status ON public.payments_retry_jobs USING btree (next_run_at, global_status) WHERE (global_status = 'SCHEDULED');
CREATE INDEX idx_payments_retry_status ON public.payments_retry_jobs USING btree (global_status);
CREATE INDEX idx_payments_retry_unique_action ON public.payments_retry_jobs USING btree (entity_id, action);

-- ==========================================
-- TABLA DE ÓRDENES
-- ==========================================
CREATE TABLE public.order_retry_jobs (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    entity_id varchar NOT NULL,
    payload jsonb NOT NULL,
    "action" varchar NOT NULL,
    attempt int4 DEFAULT 0 NOT NULL,
    global_status varchar DEFAULT 'SCHEDULED' NOT NULL,
    next_run_at timestamptz DEFAULT now() NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_Id_orderretryjobs" PRIMARY KEY (id)
);

CREATE INDEX idx_order_retry_entity_id ON public.order_retry_jobs USING btree (entity_id);
CREATE INDEX idx_order_retry_run_status ON public.order_retry_jobs USING btree (next_run_at, global_status) WHERE (global_status = 'SCHEDULED');
CREATE INDEX idx_order_retry_status ON public.order_retry_jobs USING btree (global_status);
CREATE INDEX idx_order_retry_unique_action ON public.order_retry_jobs USING btree (entity_id, action);

-- ==========================================
-- TABLA DE PRODUCTOS
-- ==========================================
CREATE TABLE public.product_retry_jobs (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    entity_id varchar NOT NULL,
    payload jsonb NOT NULL,
    "action" varchar NOT NULL,
    attempt int4 DEFAULT 0 NOT NULL,
    global_status varchar DEFAULT 'SCHEDULED' NOT NULL,
    next_run_at timestamptz DEFAULT now() NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_Id_productretryjobs" PRIMARY KEY (id)
);

CREATE INDEX idx_product_retry_entity_id ON public.product_retry_jobs USING btree (entity_id);
CREATE INDEX idx_product_retry_run_status ON public.product_retry_jobs USING btree (next_run_at, global_status) WHERE (global_status = 'SCHEDULED');
CREATE INDEX idx_product_retry_status ON public.product_retry_jobs USING btree (global_status);
CREATE INDEX idx_product_retry_unique_action ON public.product_retry_jobs USING btree (entity_id, action);