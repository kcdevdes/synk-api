package com.kcdevdes.synk.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger auditLog = LoggerFactory.getLogger("com.synk.audit");

    public void logTransactionCreated(Long transactionId, Long merchantId, String details) {
        auditLog.info("TRANSACTION_CREATED | transactionId={} | merchantId={} | details={}",
                transactionId, merchantId, details);
    }

    public void logTransactionUpdated(Long transactionId, Long merchantId, String changedFields) {
        auditLog.info("TRANSACTION_UPDATED | transactionId={} | merchantId={} | changedFields={}",
                transactionId, merchantId, changedFields);
    }

    public void logTransactionDeleted(Long transactionId, Long merchantId) {
        auditLog.info("TRANSACTION_DELETED | transactionId={} | merchantId={}",
                transactionId, merchantId);
    }

    public void logSearchOperation(Long merchantId, String criteria, int resultCount) {
        auditLog.info("SEARCH_EXECUTED | merchantId={} | criteria={} | resultCount={}",
                merchantId, criteria, resultCount);
    }
}
