package com.example.positionbookapplication.service;

import com.example.positionbookapplication.model.EventType;
import com.example.positionbookapplication.model.TradeEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Position book service layer that handles the logic behind position calculation
 */
@Service
public class PositionBookService {

    private Map<String, List<TradeEvent>> positionInfoMap = new HashMap<>();

    public Map<String, List<TradeEvent>> processTradeEvents(List<TradeEvent> newTradeEvents){

        Map<String, List<TradeEvent>> responseMap = new HashMap<>();

        newTradeEvents.forEach(newTradeEvent -> {
            String positionKey = buildPositionKeyFromTradeEvent(newTradeEvent);

            positionInfoMap.compute(positionKey, (key, val) -> {
                if (val == null) {
                    val = new ArrayList<>();
                }
                val.add(newTradeEvent);
                return val;
            });
        });

        positionInfoMap.keySet().forEach(positionKey -> {
            List<TradeEvent> tradeEvents = positionInfoMap.get(positionKey);

            responseMap.compute(positionKey + " " + calculatePosition(tradeEvents),
                    (key,val) -> tradeEvents);
        });


        return responseMap;
    }

    public String getPosition(String positionKey) {

        return positionKey + " " + calculatePosition(positionInfoMap.get(positionKey));
    }

    /**
     * Takes a trade event and turns it into a key to be added to the positionMap
     * @param tradeEvent
     * @return String {accountRef} + {securityRef}
     */
    private String buildPositionKeyFromTradeEvent(TradeEvent tradeEvent) {
        return tradeEvent.getAccountRef() + " " + tradeEvent.getSecurityRef();
    }

    private String calculatePosition(List<TradeEvent> tradeEvents) {

        if (tradeEvents == null) {
            return "0";
        }

        List<Long> cancelEventIds = tradeEvents
                .stream()
                .filter(event -> EventType.CANCEL.equals(event.getEventType()))
                .map(TradeEvent::getId)
                .toList();

        Map<EventType, List<TradeEvent>> splitBuySellTradeEvents = tradeEvents.stream()
                .filter(event -> {
                    if (CollectionUtils.isEmpty(cancelEventIds)) {
                        return true; //if there are no cancel events, always return all the elements
                    } else {
                        return !cancelEventIds.contains(event.getId()); //if there are cancel events don't return those (although this is dealt with also) or any with matching ids
                    }
                })
                .collect(Collectors.groupingBy(TradeEvent::getEventType));

        long buyPosition = CollectionUtils.isEmpty(splitBuySellTradeEvents.get(EventType.BUY)) ? 0L
                : splitBuySellTradeEvents.get(EventType.BUY).stream().mapToLong(TradeEvent::getPositionValue).sum();

        long sellPosition = CollectionUtils.isEmpty(splitBuySellTradeEvents.get(EventType.SELL)) ? 0L
                : splitBuySellTradeEvents.get(EventType.SELL).stream().mapToLong(TradeEvent::getPositionValue).sum();

        long finalPosition = buyPosition - sellPosition;

        return finalPosition < 0 ? "0" : String.valueOf(finalPosition);
    }

    public Map<String, List<TradeEvent>> getPositionInfoMap() {
        return positionInfoMap;
    }

    public void setPositionInfoMap(Map<String, List<TradeEvent>> positionInfoMap) {
        this.positionInfoMap = positionInfoMap;
    }
}
