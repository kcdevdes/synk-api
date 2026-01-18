package com.kcdevdes.synk.entity.type;

public enum TransactionStatus {
    PENDING,      // 대기중 (결제 전)
    COMPLETED,    // 완료
    CANCELLED,    // 취소됨
    FAILED,       // 실패
    REFUNDED      // 환불됨
}
