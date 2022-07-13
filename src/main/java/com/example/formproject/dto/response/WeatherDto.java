package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WeatherDto {
    private List<Long> time;
    private List<String> temp;
    private List<String> pop;
}