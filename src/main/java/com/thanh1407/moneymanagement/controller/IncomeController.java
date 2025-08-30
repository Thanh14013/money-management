package com.thanh1407.moneymanagement.controller;

import com.thanh1407.moneymanagement.dto.IncomeDTO;
import com.thanh1407.moneymanagement.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService expenseService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto) {
        IncomeDTO saved = expenseService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
