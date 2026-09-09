package com.enviro.assessment.junior.murunwamudzhadzhi.enums;

/**
 * Status of a withdrawal notice. All validation happens synchronously at
 * creation time, so a notice is either PROCESSED immediately or rejected
 * (in which case it is never persisted - the caller receives a 400 error).
 */
public enum WithdrawalStatus {
    PROCESSED,
    REJECTED
}
