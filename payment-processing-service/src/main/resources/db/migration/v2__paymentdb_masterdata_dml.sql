INSERT INTO payments.`payment_method` (`id`, `name`, `status`)
VALUES (1, 'APM', 1);

INSERT INTO payments.`payment_type` (`id`, `type`, `status`)
VALUES (1, 'SALE', 1);

INSERT INTO payments.`provider` (`id`, `provider_name`, `status`)
VALUES (1, 'PAYPAL', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (1, 'CREATED', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (2, 'INITIATED', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (3, 'PENDING', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (4, 'APPROVED', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (5, 'SUCCESS', 1);

INSERT INTO payments.`transaction_status` (`id`, `name`, `status`)
VALUES (6, 'FAILED', 1);