-- time.TimestampItem --

DROP TABLE darceo.com_timestamp;


-- async.AsyncRequestResult
-- async.AsyncRequest

--DROP INDEX idx_com_async_request_results_arr_completed_on; 

ALTER TABLE darceo.com_async_request_results DROP CONSTRAINT com_async_request_results_arr_ar_id_key;
ALTER TABLE darceo.com_async_request_results DROP CONSTRAINT fk_com_async_request_results_arr_ar_id;
ALTER TABLE darceo.com_async_request_results DROP CONSTRAINT com_async_request_results_pkey;
DROP TABLE darceo.com_async_request_results;


--DROP INDEX idx_com_async_requests_ar_requested_url;

ALTER TABLE darceo.com_async_requests DROP CONSTRAINT com_async_requests_pkey;
DROP TABLE darceo.com_async_requests;



-- EJB Timer Service --

DROP TABLE darceo."EJB__TIMER__TBL";


DROP SCHEMA darceo RESTRICT;