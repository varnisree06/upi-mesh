package com.demo.upi_offline_mesh.controller;

import com.demo.upi_offline_mesh.model.PaymentRequest;
import com.demo.upi_offline_mesh.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<String> transfer(
            @RequestBody PaymentRequest request) {

        paymentService.transfer(request);

        return ResponseEntity.ok("Payment successful");
    }
}