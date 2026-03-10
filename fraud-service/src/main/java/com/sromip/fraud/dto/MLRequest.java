package com.sromip.fraud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MLRequest {

    private String userEmail;
    private double amount;
    private String ipAddress;
    private String deviceId;

    @JsonProperty("round_off")
    private double roundOff;

    @JsonProperty("transactions_last_1hr")
    private int transactionsLast1Hr;

    @JsonProperty("transactions_last_24hr")
    private int transactionsLast24Hr;

    @JsonProperty("avg_amount_7d")
    private double avgAmount7d;

    @JsonProperty("amount_deviation")
    private double amountDeviation;

    @JsonProperty("hour_of_day")
    private int hourOfDay;

    @JsonProperty("is_new_user")
    private int isNewUser;

    // -------- GETTERS & SETTERS --------

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public double getRoundOff() { return roundOff; }
    public void setRoundOff(double roundOff) { this.roundOff = roundOff; }

    public int getTransactionsLast1Hr() { return transactionsLast1Hr; }
    public void setTransactionsLast1Hr(int transactionsLast1Hr) { this.transactionsLast1Hr = transactionsLast1Hr; }

    public int getTransactionsLast24Hr() { return transactionsLast24Hr; }
    public void setTransactionsLast24Hr(int transactionsLast24Hr) { this.transactionsLast24Hr = transactionsLast24Hr; }

    public double getAvgAmount7d() { return avgAmount7d; }
    public void setAvgAmount7d(double avgAmount7d) { this.avgAmount7d = avgAmount7d; }

    public double getAmountDeviation() { return amountDeviation; }
    public void setAmountDeviation(double amountDeviation) { this.amountDeviation = amountDeviation; }

    public int getHourOfDay() { return hourOfDay; }
    public void setHourOfDay(int hourOfDay) { this.hourOfDay = hourOfDay; }

    public int getIsNewUser() { return isNewUser; }
    public void setIsNewUser(int isNewUser) { this.isNewUser = isNewUser; }
}