CREATE SCHEMA darceo;


-- EJB Timer Service --

CREATE TABLE darceo."EJB__TIMER__TBL" (
  "TIMERID" VARCHAR(255) NOT NULL,
  "APPLICATIONID" BIGINT NOT NULL, 
  "BLOB" BYTEA NOT NULL,
  "CONTAINERID" BIGINT NOT NULL,
  "CREATIONTIMERAW" BIGINT NOT NULL,
  "INITIALEXPIRATIONRAW" BIGINT NOT NULL,
  "INTERVALDURATION" BIGINT NOT NULL,
  "LASTEXPIRATIONRAW" BIGINT NOT NULL,
  "OWNERID" VARCHAR(255) NOT NULL,
  "PKHASHCODE" INTEGER NOT NULL,
  "SCHEDULE" VARCHAR(255) NOT NULL,
  "STATE" INTEGER NOT NULL,
  CONSTRAINT "PK_EJB__TIMER__TBL" PRIMARY KEY ("TIMERID")
);


-- async.AsyncRequest --

CREATE TABLE darceo.com_async_requests
(
  ar_id VARCHAR(63) NOT NULL,
  ar_created_on timestamp NOT NULL,
  ar_in_progress boolean NOT NULL,
  ar_requested_url VARCHAR(4095),
  ar_subtype VARCHAR(63) NOT NULL,
  ar_type VARCHAR(63) NOT NULL,
  CONSTRAINT com_async_requests_pkey PRIMARY KEY (ar_id)
);

CREATE INDEX idx_com_async_requests_ar_requested_url ON darceo.com_async_requests (ar_requested_url);

-- async.AsyncRequestResult --

CREATE TABLE darceo.com_async_request_results
(
  arr_id VARCHAR(63) NOT NULL,
  arr_http_code integer NOT NULL,
  arr_completed_on timestamp NOT NULL,
  arr_content_type VARCHAR(255),
  arr_result_filename VARCHAR(255),
  arr_ar_id VARCHAR(63) NOT NULL,
  CONSTRAINT com_async_request_results_pkey PRIMARY KEY (arr_id),
  CONSTRAINT fk_com_async_request_results_arr_ar_id FOREIGN KEY (arr_ar_id)
      REFERENCES darceo.com_async_requests (ar_id),
  CONSTRAINT com_async_request_results_arr_ar_id_key UNIQUE (arr_ar_id)
);

CREATE INDEX idx_com_async_request_results_arr_completed_on ON darceo.com_async_request_results (arr_completed_on);


-- time.TimestampItem --

CREATE TABLE darceo.com_timestamp
(
  ti_date timestamp
);

INSERT INTO darceo.com_timestamp (ti_date) VALUES (CURRENT_TIMESTAMP);

