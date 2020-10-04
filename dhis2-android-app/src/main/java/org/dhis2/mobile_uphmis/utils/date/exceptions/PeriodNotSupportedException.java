package org.dhis2.mobile_uphmis.utils.date.exceptions;

public class PeriodNotSupportedException extends Exception {
    private String periodType;

    public PeriodNotSupportedException(String message, String periodType) {
        super(message);
        this.periodType = periodType;
    }

    public String getPeriodType() {
        return periodType;
    }
}
