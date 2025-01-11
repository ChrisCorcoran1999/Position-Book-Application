package com.example.positionbookapplication.controller;

import com.example.positionbookapplication.model.EventType;
import com.example.positionbookapplication.model.TradeEvent;
import com.example.positionbookapplication.service.PositionBookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PositionBookControllerTest {

    @Mock
    private PositionBookService positionBookService;

    @InjectMocks
    private PositionBookController positionBookController;

    private static final String ACCOUNT = "ACC01";

    private static final String SECURITY = "SEC01";


    @Test
    public void testProcessTradeEvent() {
        List<TradeEvent> tradeEvents = new ArrayList<>();
        TradeEvent buyEvent = new TradeEvent(1L, EventType.BUY, ACCOUNT, SECURITY, 50L);
        TradeEvent sellEvent = new TradeEvent(2L, EventType.SELL, ACCOUNT, SECURITY, 50L);
        tradeEvents.add(buyEvent);
        tradeEvents.add(sellEvent);

        Map<String, List<TradeEvent>> responceMap = new HashMap<>();

        String key = ACCOUNT + " " + SECURITY + " " + "0";
        responceMap.put(key, tradeEvents);

        when(positionBookService.processTradeEvents(tradeEvents)).thenReturn(responceMap);

        ResponseEntity<Map<String, List<TradeEvent>>> validResponse = ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responceMap);
        assertThat(positionBookController.processTradeEvent(tradeEvents)).isEqualTo(validResponse);
    }

}
