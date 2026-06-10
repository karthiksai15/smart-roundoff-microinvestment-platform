package com.sromip.dashboard.dto;

public class PipelineView {

    private String paymentId;

    private String payment;
    private String fraud;
    private String investment;
    private String notification;

    public PipelineView(String paymentId, String payment, String fraud,
                        String investment, String notification) {
        this.paymentId = paymentId;
        this.payment = payment;
        this.fraud = fraud;
        this.investment = investment;
        this.notification = notification;
    }

    public String getPaymentId() { return paymentId; }
    public String getPayment() { return payment; }
    public String getFraud() { return fraud; }
    public String getInvestment() { return investment; }
    public String getNotification() { return notification; }
}