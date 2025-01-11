package com.example.positionbookapplication.service;

import com.example.positionbookapplication.model.EventType;
import com.example.positionbookapplication.model.TradeEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class PositionBookServiceTest {

    private final PositionBookService positionBookService = new PositionBookService();

    private static final String ACCOUNT = "ACC01";

    private static final String SECURITY = "SEC01";

    private static final String ALT_SECURITY = "SEC02";

    @Test
    public void testGetPosition_existingPosition() {
        String positionKey = ACCOUNT + " " + SECURITY;

        List<TradeEvent> tradeEvents = new ArrayList<>();
        TradeEvent buyEvent = new TradeEvent(1L, EventType.BUY, ACCOUNT, SECURITY, 50L);
        TradeEvent sellEvent = new TradeEvent(2L, EventType.SELL, ACCOUNT, SECURITY, 25L);
        tradeEvents.add(buyEvent);
        tradeEvents.add(sellEvent);

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        positionBookService.setPositionInfoMap(positionMap);

        String response = positionKey + " " + "25";

        assertThat(positionBookService.getPosition(positionKey))
                .as("a key that has an exising mapping returns the correct string")
                .isEqualTo(response);
    }

    @Test
    public void testGetPosition_nonExistingPosition() {
        String positionKey = ACCOUNT + " " + SECURITY;

        String response = positionKey + " " + "0";

        assertThat(positionBookService.getPosition(positionKey))
                .as("a key that doesnt have an exising mapping returns a string of the " +
                        "account ref and security ref and a position value of 0")
                .isEqualTo(response);
    }

    @Test
    public void testProcessTradeEvents_buySecurity(){
        List<TradeEvent> tradeEvents = new ArrayList<>();
        TradeEvent buyEvent = new TradeEvent(1L, EventType.BUY, ACCOUNT, SECURITY, 50L);
        tradeEvents.add(buyEvent);

        String positionKey = ACCOUNT + " " + SECURITY + " " + "50";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("Multiple buy events should return a map with the key having the position value" +
                        "and a list of the events as values")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_buyMultiple_oneSecurity(){
        List<TradeEvent> tradeEvents = new ArrayList<>();

        tradeEvents.add(new TradeEvent(2L, EventType.BUY, ACCOUNT, SECURITY, 25L));
        tradeEvents.add(new TradeEvent(1L, EventType.BUY, ACCOUNT, SECURITY, 50L));

        String positionKey = ACCOUNT + " " + SECURITY + " " + "75";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("Multiple buy events should return a map with the key having the summed position value" +
                        "and a list of the events as values")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_buyMultiple_manySecurities(){
        Map<String, List<TradeEvent>> positionMap = new HashMap<>();

        List<TradeEvent> tradeEvents = new ArrayList<>();
        TradeEvent tradeEventSecurity = new TradeEvent(2L, EventType.BUY, ACCOUNT, SECURITY, 25L);
        TradeEvent tradeEventAltSecurity = new TradeEvent(2L, EventType.BUY, ACCOUNT, ALT_SECURITY, 75L);
        tradeEvents.add(tradeEventSecurity);
        tradeEvents.add(tradeEventAltSecurity);

        List<TradeEvent> tradeEventsSecurity = new ArrayList<>();
        tradeEventsSecurity.add(tradeEventSecurity);
        String positionKeySecurity = ACCOUNT + " " + SECURITY + " " + "25";
        positionMap.put(positionKeySecurity, tradeEventsSecurity);

        List<TradeEvent> tradeEventsAltSecurity = new ArrayList<>();
        tradeEventsAltSecurity.add(tradeEventAltSecurity);
        String positionKeyAltSecurity = ACCOUNT + " " + ALT_SECURITY + " " + "75";
        positionMap.put(positionKeyAltSecurity, tradeEventsAltSecurity);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("Multiple buy events should return a map with the key having the summed position value" +
                        "and a list of the events as values")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_sellSecurities_moreThanCurrentPosition(){
        List<TradeEvent> tradeEvents = new ArrayList<>();

        tradeEvents.add(new TradeEvent(1L, EventType.SELL, ACCOUNT, SECURITY, 50L));

        String positionKey = ACCOUNT + " " + SECURITY + " " + "0";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("Sell events that would cause a negative position should return a map " +
                        "with the key having the position value of 0 and a list of the events as values")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_sellSecurities_lessThanCurrentPosition() {
        List<TradeEvent> tradeEvents = new ArrayList<>();

        tradeEvents.add(new TradeEvent(1L, EventType.SELL, ACCOUNT, SECURITY, 50L));
        tradeEvents.add(new TradeEvent(2L, EventType.BUY, ACCOUNT, SECURITY, 75L));

        String positionKey = ACCOUNT + " " + SECURITY + " " + "25";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("Sell events that would cause a positive position should return a map " +
                        "with the key having the summed position value and a list of the events as values")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_cancelEvent(){
        List<TradeEvent> tradeEvents = new ArrayList<>();

        tradeEvents.add(new TradeEvent(1L, EventType.SELL, ACCOUNT, SECURITY, 50L));
        tradeEvents.add(new TradeEvent(2L, EventType.BUY, ACCOUNT, SECURITY, 75L));
        tradeEvents.add(new TradeEvent(1L, EventType.CANCEL, ACCOUNT, SECURITY, 0L));

        String positionKey = ACCOUNT + " " + SECURITY + " " + "75";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        positionMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("should return with the key having the summed position values (without the cancelled " +
                        "events value) and a list of the events as values (including the cancel event)")
                .isEqualTo(positionMap);
    }

    @Test
    public void testProcessTradeEvents_onlyAccountSecurityRefInEventReturned(){
        List<TradeEvent> tradeEvents = new ArrayList<>();

        tradeEvents.add(new TradeEvent(1L, EventType.SELL, ACCOUNT, SECURITY, 50L));
        tradeEvents.add(new TradeEvent(2L, EventType.BUY, ACCOUNT, SECURITY, 75L));
        tradeEvents.add(new TradeEvent(1L, EventType.CANCEL, ACCOUNT, SECURITY, 0L));

        String positionKey = ACCOUNT + " " + SECURITY + " " + "75";

        Map<String, List<TradeEvent>> positionMap = new HashMap<>();
        List<TradeEvent> existingTradeEvents = new ArrayList<>(tradeEvents);
        existingTradeEvents.add(new TradeEvent(3L, EventType.SELL, ACCOUNT, ALT_SECURITY, 50L));
        positionMap.put(positionKey, existingTradeEvents);
        positionBookService.setPositionInfoMap(positionMap);

        Map<String, List<TradeEvent>> responseMap = new HashMap<>();
        responseMap.put(positionKey, tradeEvents);

        assertThat(positionBookService.processTradeEvents(tradeEvents))
                .as("The list returned in the response should only be related to those that came in the response")
                .isEqualTo(responseMap);
    }

}
