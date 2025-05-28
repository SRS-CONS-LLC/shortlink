package com.srscons.shortlink.analytics;

import com.srscons.shortlink.shortener.repository.entity.MetaDataEntity;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public interface AnalyticsMapper {
    AnalyticsDTO toAnalytics(List<MetaDataEntity> metaDataList);
}