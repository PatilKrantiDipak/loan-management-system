package com.example.loanmanagement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class EmiService {

    public BigDecimal calculateEmi(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            Integer tenureMonths) {

        // Monthly interest rate
        BigDecimal monthlyRate =
                annualInterestRate
                        .divide(BigDecimal.valueOf(12 * 100), 10,
                                RoundingMode.HALF_UP);

        // If interest rate is 0
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {

            return principal
                    .divide(
                        BigDecimal.valueOf(tenureMonths),
                        2,
                        RoundingMode.HALF_UP
                    );
        }

        // (1 + R)^N
        double power = Math.pow(
                1 + monthlyRate.doubleValue(),
                tenureMonths
        );

        // EMI formula
        double emi =
                principal.doubleValue()
                * monthlyRate.doubleValue()
                * power
                / (power - 1);

        return BigDecimal
                .valueOf(emi)
                .setScale(2, RoundingMode.HALF_UP);
    }
}