package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HourlyDto {
    private String time;
    private String temp;
    private String pop;
}