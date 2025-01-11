package com.example.positionbookapplication.controller.validator;

import com.example.positionbookapplication.model.EventType;
import com.example.positionbookapplication.model.TradeEvent;
import com.example.positionbookapplication.service.PositionBookService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TradeEventInputValidatorTest {

    private static final String ACCOUNT = "ACC01";

    private static final String SECURITY = "SEC01";

    private static final TradeEvent BUY_EVENT = new TradeEvent(1L, EventType.BUY, ACCOUNT, SECURITY, 50L);

    private static final TradeEvent SELL_EVENT = new TradeEvent(2L, EventType.SELL, ACCOUNT, SECURITY, 50L);

    private static final TradeEvent CANCEL_EVENT = new TradeEvent(3L, EventType.CANCEL, ACCOUNT, SECURITY, 50L);

    PositionBookService positionBookService = new PositionBookService();

    TradeEventInputValidator tradeEventInputValidator = new TradeEventInputValidator();

    @Test
    public void testValidateNoEventType() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setEventType(null);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event type is a mandatory field");
    }

    @Test
    public void testValidateBuySellEvent_matchingId() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        tradeEvents.add(BUY_EVENT);

        List<TradeEvent> existingTradeEvents = new ArrayList<>();
        existingTradeEvents.add(BUY_EVENT);

        Map<String, List<TradeEvent>> positionBookMap = new HashMap<>();
        positionBookMap.put("anyKey", existingTradeEvents);
        positionBookService.setPositionInfoMap(positionBookMap);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("Adding a buy/sell trade event that matches and existing trade event Id should throw an error")
                .hasMessage("Trade event id already exists");

    }

    @Test
    public void testValidateCancelEvent_noMatchingId() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        tradeEvents.add(CANCEL_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("Adding a buy/sell trade event that matches and existing trade event Id should throw an error")
                .hasMessage("Cancel event Id must match an existing Id");

    }

    @Test
    public void testValidateBuySellEvent_noPositionValue() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setPositionValue(null);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event position value is a mandatory field that must be positive");
    }

    @Test
    public void testValidateBuySellEvent_negativePositionValue() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setPositionValue(-1L);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event position value is a mandatory field that must be positive");
    }

    @Test
    public void testValidateEvent_noId() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setId(null);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event id is a mandatory field");
    }

    @Test
    public void testValidateEvent_noSecurityRef() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setSecurityRef("");
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event security ref is a mandatory field");
    }

    @Test
    public void testValidateEvent_nullSecurityRef() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setSecurityRef(null);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event security ref is a mandatory field");
    }

    @Test
    public void testValidateEvent_noAccountRef() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setSecurityRef("");
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event account ref is a mandatory field");
    }

    @Test
    public void testValidateEvent_nullAccountRef() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        BUY_EVENT.setSecurityRef(null);
        tradeEvents.add(BUY_EVENT);

        assertThatThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService))
                .as("A trade event without an event should throw an error")
                .hasMessage("Trade event account ref is a mandatory field");
    }

    @Test
    public void testValidateBuySellEvent_validEventDoesntError() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        tradeEvents.add(SELL_EVENT);
        tradeEvents.add(BUY_EVENT);

        assertThatNoException()
                .as("Valid events should not throw validation exceptions")
                .isThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService));

    }

    @Test
    public void testValidateCancelEvent_validEventDoesntError() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        tradeEvents.add(CANCEL_EVENT);

        List<TradeEvent> existingTradeEvents = new ArrayList<>();
        BUY_EVENT.setId(CANCEL_EVENT.getId());
        existingTradeEvents.add(BUY_EVENT);

        Map<String, List<TradeEvent>> positionBookMap = new HashMap<>();
        positionBookMap.put("anyKey", existingTradeEvents);
        positionBookService.setPositionInfoMap(positionBookMap);

        assertThatNoException()
                .as("Valid events should not throw validation exceptions")
                .isThrownBy(() -> tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService));

    }

}
