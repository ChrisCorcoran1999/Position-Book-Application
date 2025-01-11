package com.example.positionbookapplication.controller;

import com.example.positionbookapplication.controller.validator.TradeEventInputValidator;
import com.example.positionbookapplication.controller.validator.ValidationException;
import com.example.positionbookapplication.model.TradeEvent;
import com.example.positionbookapplication.service.PositionBookService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;
import java.util.Map;

/**
 * Rest controller for processing and retrieving trade events for or from the position book
 */
@RestController
@RequestMapping("/api")
public class PositionBookController {

    @Resource
    private PositionBookService positionBookService;

    private final TradeEventInputValidator tradeEventInputValidator = new TradeEventInputValidator();

    @PostMapping("/processTradeEvent")
    public ResponseEntity<?> processTradeEvent(@RequestBody final List<TradeEvent> tradeEvents) {

        try {
            tradeEventInputValidator.validateTradeEvents(tradeEvents, positionBookService);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }

        Map<String,List<TradeEvent>> response = positionBookService.processTradeEvents(tradeEvents);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/getPosition/{accountRef}/{securityRef}")
    public String getPosition(@PathVariable final String accountRef, @PathVariable final String securityRef) {

        return positionBookService.getPosition(accountRef + " " + securityRef);
    }

}
