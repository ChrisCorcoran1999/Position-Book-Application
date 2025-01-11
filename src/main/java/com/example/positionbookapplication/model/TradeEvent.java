package com.example.positionbookapplication.model;

public class TradeEvent {

    private Long id;

    private EventType eventType;

    private String accountRef;

    private String securityRef;

    private Long positionValue;

    public TradeEvent(Long id, EventType eventType, String accountRef, String securityRef, Long positionValue) {
        this.id = id;
        this.eventType = eventType;
        this.accountRef = accountRef;
        this.securityRef = securityRef;
        this.positionValue = positionValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getAccountRef() {
        return accountRef;
    }

    public void setAccountRef(String accountRef) {
        this.accountRef = accountRef;
    }

    public String getSecurityRef() {
        return securityRef;
    }

    public void setSecurityRef(String securityRef) {
        this.securityRef = securityRef;
    }

    public Long getPositionValue() {
        return positionValue;
    }

    public void setPositionValue(Long positionValue) {
        this.positionValue = positionValue;
    }
}
