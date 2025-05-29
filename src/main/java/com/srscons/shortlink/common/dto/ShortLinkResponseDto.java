package com.srscons.shortlink.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkResponseDto {

    private String message;
    private int code;
    private Object value;

}
