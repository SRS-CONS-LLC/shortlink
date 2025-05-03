package com.srscons.shortlink.service;

import com.srscons.shortlink.dto.SmartlinkRequestDto;
import com.srscons.shortlink.dto.SmartlinkResponseDto;
import com.srscons.shortlink.model.Smartlink;

import java.util.List;

public interface SmartlinkService {
    List<SmartlinkResponseDto> getAllSmartlinks();
    List<SmartlinkResponseDto> getDraftSmartlinks();
    SmartlinkResponseDto create(SmartlinkRequestDto smartlinkDto);
}

