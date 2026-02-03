package com.kcdevdes.synk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuditLogger {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT");

    private AuditLogger() {
    }

    public static void transactionCreated(Long transactionId, Long accountId, Long userId, String type) {
        AUDIT_LOGGER.info(
                "event=transaction_created transactionId={} accountId={} userId={} type={}",
                safeId(transactionId), safeId(accountId), safeId(userId), safeValue(type)
        );
    }

    public static void transactionUpdated(Long transactionId, Long accountId, Long userId, String type) {
        AUDIT_LOGGER.info(
                "event=transaction_updated transactionId={} accountId={} userId={} type={}",
                safeId(transactionId), safeId(accountId), safeId(userId), safeValue(type)
        );
    }

    public static void transactionDeleted(Long transactionId, Long accountId, Long userId) {
        AUDIT_LOGGER.info(
                "event=transaction_deleted transactionId={} accountId={} userId={}",
                safeId(transactionId), safeId(accountId), safeId(userId)
        );
    }

    public static void transactionSearchByMerchant(int queryLength, int resultCount) {
        AUDIT_LOGGER.info(
                "event=transaction_search_by_merchant queryLength={} resultCount={}",
                queryLength, resultCount
        );
    }

    public static void transactionFilterByType(String type, int resultCount) {
        AUDIT_LOGGER.info(
                "event=transaction_filter_by_type type={} resultCount={}",
                safeValue(type), resultCount
        );
    }

    private static String safeId(Long id) {
        return id == null ? "null" : id.toString();
    }

    private static String safeValue(String value) {
        return value == null ? "null" : value;
    }
}
