package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PriceInfoDto {
    @Schema(type = "String",example = "쌀")
    private String crop;
    @Schema(type = "String",example = "백미")
    private String type;
    @Schema(type = "String",example = "kg")
    private String unit;
    @Schema(type = "String",example = "서울")
    private String country;
    @Schema(type = "String",example = "소매")
    private String wholeSale;
    @Schema(type = "List<String>",example = "[2021-07,2021-09,2021-11,2022-01,2022-03,2022-05,2022-07]")
    private List<String> dateList;
    @Schema(type = "List<String>",example = "[1,500,1,600,1,400,1,500,1,700,1,700,1,550]")
    private List<String> priceList;
}
