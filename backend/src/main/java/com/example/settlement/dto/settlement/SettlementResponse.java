package com.example.settlement.dto.settlement;

import java.util.List;

public record SettlementResponse(
        Long eventId,
        List<SettlementMemberBalance> members,
        List<SettlementTransfer> transfers
) {
}
