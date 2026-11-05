
package com.lab.concurrencypalallelism.marketInformation.infra.rest.controller;

import com.lab.concurrencypalallelism.marketInformation.application.command.GetMarketInformationCommand;
import com.lab.concurrencypalallelism.marketInformation.application.port.in.MarketInformation;
import com.lab.concurrencypalallelism.marketInformation.application.result.MarketInformationResult;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.GetMarketInformationRequest;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.dto.MarketInformationResponse;
import com.lab.concurrencypalallelism.marketInformation.infra.rest.mapper.MarketInformationMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/market-information")
public class MarketInformationController {

  private final MarketInformation marketInformation;
  private final MarketInformationMapper mapper;

  public MarketInformationController(
      MarketInformation marketInformation,
      MarketInformationMapper mapper) {
    this.marketInformation = marketInformation;
    this.mapper = mapper;
  }

  @GetMapping
  public ResponseEntity<MarketInformationResponse> getMarketInformation(
      @Valid @ModelAttribute GetMarketInformationRequest request) {

    GetMarketInformationCommand command = mapper.toCommand(request);

    MarketInformationResult result = marketInformation.getMarketInformation(command);

    MarketInformationResponse response = mapper.toResponse(result);

    return ResponseEntity.ok(response);
  }
}
