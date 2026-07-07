package com.oasis.nexuslibrary.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> getAdminDashboardStats();
    Map<String, Object> getMemberDashboardStats(Long memberId);
}
