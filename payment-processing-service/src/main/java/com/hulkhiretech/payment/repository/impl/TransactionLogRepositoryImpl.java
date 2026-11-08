
package com.hulkhiretech.payment.repository.impl;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.hulkhiretech.payment.entity.TransactionLogEntity;
import com.hulkhiretech.payment.repository.interfaces.TransactionLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionLogRepositoryImpl implements TransactionLogRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public int insertTxnLog(TransactionLogEntity entity) {

        if (entity == null) {
            log.warn("insertTxnLog - received null entity");
            return -1;
        }

        log.info(
                "insertTxnLog - start. transactionId={}, fromStatus={}, toStatus={}",
                entity.getTransactionId(),
                entity.getTxnFromStatus(),
                entity.getTxnToStatus()
        );

        final String sql =
                "INSERT INTO payments.`transaction_log` " +
                "(transaction_id, txn_from_status, txn_to_status) " +
                "VALUES (:transactionId, :txnFromStatus, :txnToStatus)";

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
                        "insertTxnLog - generated key not returned. transactionId={}",
                        entity.getTransactionId()
                );
                return -1;
            }

            int generatedId = key.intValue();

            log.info(
                    "insertTxnLog - success. generatedId={}, transactionId={}",
                    generatedId,
                    entity.getTransactionId()
            );

            return generatedId;

        } catch (Exception ex) {

            log.error(
                    "insertTxnLog - error inserting transaction log. " +
                    "transactionId={}, fromStatus={}, toStatus={}, Exception={}",
                    entity.getTransactionId(),
                    entity.getTxnFromStatus(),
                    entity.getTxnToStatus(),
                    ex.getMessage(),
                    ex
            );

            return -1;
        }
    }
}

