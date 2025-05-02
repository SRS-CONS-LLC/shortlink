package com.srscons.shortlink.service;

import com.srscons.shortlink.model.Smartlink;

import java.util.List;

public interface SmartlinkService {
    List<Smartlink> getAllSmartlinks();
    List<Smartlink> getDraftSmartlinks();
    Smartlink create(Smartlink smartlink);
}
