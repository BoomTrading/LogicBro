package com.example.LogicBro.dto;

import lombok.Data;

@Data
public class VariationRequestDTO {
    private String[] chordProgression;
    private String scale;
    private double amount;
}
