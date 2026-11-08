package com.hulkhiretech.payment.repository.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payment.entity.TransactionEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionRepositoryImpl implements TransactionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    // ============================================================
    // CREATE TRANSACTION
    // ============================================================

    @Override
    public int createTransaction(TransactionEntity entity) {

        log.info(
                "createTransaction - start. txnReference={}, merchantTxnRef={}, userId={}",
                entity == null ? null : entity.getTxnReference(),
                entity == null ? null : entity.getMerchantTransactionReference(),
                entity == null ? null : entity.getUserId()
        );

        if (entity == null) {

            log.warn("createTransaction - received null entity");

            return -1;
        }

        final String sql =
                "INSERT INTO payments.`transaction` " +
                "(user_id, payment_method_id, provider_id, payment_type_id, " +
                "txn_status_id, amount, currency, " +
                "merchant_transaction_reference, txn_reference) " +
                "VALUES " +
                "(:userId, :paymentMethodId, :providerId, :paymentTypeId, " +
                ":txnStatusId, :amount, :currency, " +
                ":merchantTransactionReference, :txnReference)";

        BeanPropertySqlParameterSource params =
                new BeanPropertySqlParameterSource(entity);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {

            namedParameterJdbcTemplate.update(
                    sql,
                    params,
                    keyHolder
            );

            Number key = keyHolder.getKey();

            if (key == null) {

                log.warn(
                        "createTransaction - generated key not returned. txnReference={}",
                        entity.getTxnReference()
                );

                return -1;
            }

            int generatedId = key.intValue();

            log.info(
                    "createTransaction - success. generatedId={}, txnReference={}",
                    generatedId,
                    entity.getTxnReference()
            );

            return generatedId;

        } catch (Exception ex) {

            log.error(
                    "createTransaction - error inserting transaction. " +
                    "txnReference={}, merchantTxnRef={}, userId={}",
                    entity.getTxnReference(),
                    entity.getMerchantTransactionReference(),
                    entity.getUserId(),
                    ex
            );

            return -1;
        }
    }


    // ============================================================
    // GET TRANSACTION BY TRANSACTION REFERENCE
    // ============================================================

    @Override
    public TransactionEntity getTxnByTxnReference(String txnReference) {

        log.info(
                "getTxnByTxnReference - start. txnReference={}",
                txnReference
        );

        final String sql =
                "SELECT * " +
                "FROM payments.`transaction` " +
                "WHERE txn_reference = :txnReference";

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue("txnReference", txnReference);

        try {

            TransactionEntity entity =
                    namedParameterJdbcTemplate.queryForObject(
                            sql,
                            params,
                            new BeanPropertyRowMapper<>(TransactionEntity.class)
                    );

            log.info(
                    "getTxnByTxnReference - transaction found. txnReference={}",
                    txnReference
            );

            return entity;

        } catch (EmptyResultDataAccessException ex) {

            /*
             * queryForObject() expects exactly one row.
             *
             * If no transaction exists, Spring throws
             * EmptyResultDataAccessException.
             *
             * In this case we simply return null.
             */

            log.warn(
                    "getTxnByTxnReference - transaction not found. txnReference={}",
                    txnReference
            );

            return null;

        } catch (Exception ex) {

            log.error(
                    "getTxnByTxnReference - error fetching transaction. txnReference={}",
                    txnReference,
                    ex
            );

            return null;
        }
    }


    // ============================================================
    // UPDATE TRANSACTION DETAILS
    // ============================================================

    @Override
    public boolean updateTxnDetails(TransactionEntity entity) {

        if (entity == null || entity.getId() == null) {

            log.warn(
                    "updateTxnDetails - null entity or null id"
            );

            return false;
        }

        log.info(
                "updateTxnDetails - start. id={}, txnReference={}",
                entity.getId(),
                entity.getTxnReference()
        );

        final String sql =
                "UPDATE payments.`transaction` SET " +
                "txn_status_id = :txnStatusId, " +
                "provider_reference = :providerReference, " +
                "error_code = :errorCode, " +
                "error_message = :errorMessage, " +
                "retry_count = :retryCount " +
                "WHERE id = :id";

        BeanPropertySqlParameterSource params =
                new BeanPropertySqlParameterSource(entity);

        try {

            int rows =
                    namedParameterJdbcTemplate.update(
                            sql,
                            params
                    );

            /*
             * rows tells us how many records were affected.
             *
             * 1 = transaction updated successfully
             * 0 = no transaction found with this id
             */

            if (rows == 1) {

                log.info(
                        "updateTxnDetails - success. id={}, rows={}",
                        entity.getId(),
                        rows
                );

                return true;
            }

            log.warn(
                    "updateTxnDetails - no transaction updated. id={}, rows={}",
                    entity.getId(),
                    rows
            );

            return false;

        } catch (Exception ex) {

            log.error(
                    "updateTxnDetails - error updating transaction. " +
                    "id={}, txnReference={}",
                    entity.getId(),
                    entity.getTxnReference(),
                    ex
            );

            return false;
        }
    }
}