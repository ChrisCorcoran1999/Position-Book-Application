package com.example.positionbookapplication.controller.validator;

import ch.qos.logback.core.util.StringUtil;
import com.example.positionbookapplication.model.TradeEvent;
import com.example.positionbookapplication.service.PositionBookService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator class for trade event rest inputs
 */
public class TradeEventInputValidator {

    public void validateTradeEvents(List<TradeEvent> tradeEvents, PositionBookService positionBookService) throws ValidationException {

        Map<String, List<TradeEvent>> currentPositions = positionBookService.getPositionInfoMap();

        List<TradeEvent> allExistingTradeEvents = new ArrayList<>();
        if (!CollectionUtils.isEmpty(currentPositions)) {
            allExistingTradeEvents.addAll(currentPositions.values().stream().flatMap(List::stream).toList());
        }
        allExistingTradeEvents.addAll(tradeEvents);

        for (TradeEvent tradeEvent : tradeEvents) {
            allExistingTradeEvents.remove(tradeEvent);

            if (tradeEvent.getEventType() == null) {
                throw new ValidationException("Trade event type is a mandatory field");
            }
            switch (tradeEvent.getEventType()) {
                case SELL:
                case BUY:
                    validateBuySellEvent(tradeEvent, allExistingTradeEvents);
                    break;
                case CANCEL:
                    validateCancelEvent(tradeEvent,allExistingTradeEvents);
                    break;
            }

            allExistingTradeEvents.add(tradeEvent);
        }

    }

    private void validateBuySellEvent(TradeEvent tradeEvent, List<TradeEvent> allExistingTradeEvents) throws ValidationException {
        validateEvent(tradeEvent);

        if (allExistingTradeEvents.stream().map(TradeEvent::getId).anyMatch(tradeEvent.getId()::equals)) {
            throw new ValidationException("Trade event id already exists");
        }
        if (tradeEvent.getPositionValue() == null || tradeEvent.getPositionValue() < 0) {
            throw new ValidationException("Trade event position value is a mandatory field that must be positive");
        }

    }

    private void validateCancelEvent(TradeEvent tradeEvent, List<TradeEvent> allExistingTradeEvents) throws ValidationException {
        validateEvent(tradeEvent);

        if (allExistingTradeEvents.stream().map(TradeEvent::getId).noneMatch(tradeEvent.getId()::equals)) {
            throw new ValidationException("Cancel event Id must match an existing Id");
        }
    }

    private void validateEvent(TradeEvent tradeEvent) throws ValidationException {
        if (tradeEvent.getId() == null) {
            throw new ValidationException("Trade event id is a mandatory field");
        }
        if (StringUtil.isNullOrEmpty(tradeEvent.getSecurityRef())) {
            throw new ValidationException("Trade event security ref is a mandatory field");
        }
        if (StringUtil.isNullOrEmpty(tradeEvent.getAccountRef())) {
            throw new ValidationException("Trade event account ref is a mandatory field");
        }
    }

}
