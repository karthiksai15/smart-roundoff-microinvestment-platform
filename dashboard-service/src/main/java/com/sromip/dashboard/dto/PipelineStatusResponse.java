package com.sromip.dashboard.dto;

public class PipelineStatusResponse {

    private String payment;
    private String fraudStatus;
    private String investmentStatus;
    private String notificationStatus;

    public PipelineStatusResponse() {}

    public PipelineStatusResponse(String payment, String fraudStatus,
                                  String investmentStatus, String notificationStatus) {
        this.payment = payment;
        this.fraudStatus = fraudStatus;
        this.investmentStatus = investmentStatus;
        this.notificationStatus = notificationStatus;
    }

    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }

    public String getFraudStatus() { return fraudStatus; }
    public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }

    public String getInvestmentStatus() { return investmentStatus; }
    public void setInvestmentStatus(String investmentStatus) { this.investmentStatus = investmentStatus; }

    public String getNotificationStatus() { return notificationStatus; }
    public void setNotificationStatus(String notificationStatus) { this.notificationStatus = notificationStatus; }
}
