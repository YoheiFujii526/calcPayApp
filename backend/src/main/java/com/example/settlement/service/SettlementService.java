package com.example.settlement.service;

import com.example.settlement.dto.settlement.SettlementMemberBalance;
import com.example.settlement.dto.settlement.SettlementResponse;
import com.example.settlement.dto.settlement.SettlementTransfer;
import com.example.settlement.entity.Expense;
import com.example.settlement.entity.ExpenseTarget;
import com.example.settlement.entity.Member;
import com.example.settlement.entity.Repayment;
import com.example.settlement.repository.EventParticipantRepository;
import com.example.settlement.repository.ExpenseRepository;
import com.example.settlement.repository.ExpenseTargetRepository;
import com.example.settlement.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SettlementService {

    private final EventService eventService;
    private final EventParticipantRepository eventParticipantRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseTargetRepository expenseTargetRepository;
    private final RepaymentRepository repaymentRepository;

    public SettlementService(
            EventService eventService,
            EventParticipantRepository eventParticipantRepository,
            ExpenseRepository expenseRepository,
            ExpenseTargetRepository expenseTargetRepository,
            RepaymentRepository repaymentRepository
    ) {
        this.eventService = eventService;
        this.eventParticipantRepository = eventParticipantRepository;
        this.expenseRepository = expenseRepository;
        this.expenseTargetRepository = expenseTargetRepository;
        this.repaymentRepository = repaymentRepository;
    }

    public SettlementResponse calculate(Long eventId) {
        eventService.getEventOrThrow(eventId);

        List<Member> participants = eventParticipantRepository.findByEventId(eventId).stream()
                .map(ep -> ep.getMember())
                .toList();

        Map<Long, MutableBalance> balances = new HashMap<>();
        participants.forEach(member -> balances.put(member.getId(), new MutableBalance(member)));

        List<Expense> expenses = expenseRepository.findByEventId(eventId);
        for (Expense expense : expenses) {
            balances.get(expense.getPaidByMember().getId()).totalPaid += expense.getAmount();

            List<ExpenseTarget> targets = expenseTargetRepository.findByExpenseId(expense.getId());
            if (targets.isEmpty()) {
                continue;
            }

            int perHead = expense.getAmount() / targets.size();
            int remainder = expense.getAmount() % targets.size();
            for (int i = 0; i < targets.size(); i++) {
                Long memberId = targets.get(i).getMember().getId();
                int share = perHead + (i < remainder ? 1 : 0);
                balances.get(memberId).fairShare += share;
            }
        }

        List<Repayment> repayments = repaymentRepository.findByEventId(eventId);
        for (Repayment repayment : repayments) {
            balances.get(repayment.getFromMember().getId()).sentRepayment += repayment.getAmount();
            balances.get(repayment.getToMember().getId()).receivedRepayment += repayment.getAmount();
        }

        List<SettlementMemberBalance> memberBalances = balances.values().stream()
                .map(item -> new SettlementMemberBalance(
                        item.member.getId(),
                        displayName(item.member),
                        item.totalPaid,
                        item.fairShare,
                        item.receivedRepayment,
                        item.sentRepayment,
                        item.totalPaid - item.fairShare + item.receivedRepayment - item.sentRepayment
                ))
                .sorted(Comparator.comparing(SettlementMemberBalance::memberId))
                .toList();

        List<Creditor> creditors = new ArrayList<>();
        List<Debtor> debtors = new ArrayList<>();
        for (SettlementMemberBalance mb : memberBalances) {
            if (mb.balance() > 0) {
                creditors.add(new Creditor(mb.memberId(), mb.balance()));
            } else if (mb.balance() < 0) {
                debtors.add(new Debtor(mb.memberId(), -mb.balance()));
            }
        }

        creditors.sort((a, b) -> Integer.compare(b.amount, a.amount));
        debtors.sort((a, b) -> Integer.compare(b.amount, a.amount));

        List<SettlementTransfer> transfers = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            Debtor debtor = debtors.get(i);
            Creditor creditor = creditors.get(j);
            int amount = Math.min(debtor.amount, creditor.amount);
            transfers.add(new SettlementTransfer(debtor.memberId, creditor.memberId, amount));
            debtor.amount -= amount;
            creditor.amount -= amount;
            if (debtor.amount == 0) {
                i++;
            }
            if (creditor.amount == 0) {
                j++;
            }
        }

        return new SettlementResponse(eventId, memberBalances, transfers);
    }

    private String displayName(Member member) {
        return member.getDisplayName() != null ? member.getDisplayName() : member.getName();
    }

    private static class MutableBalance {
        private final Member member;
        private int totalPaid;
        private int fairShare;
        private int receivedRepayment;
        private int sentRepayment;

        private MutableBalance(Member member) {
            this.member = member;
        }
    }

    private static class Creditor {
        private final Long memberId;
        private int amount;

        private Creditor(Long memberId, int amount) {
            this.memberId = memberId;
            this.amount = amount;
        }
    }

    private static class Debtor {
        private final Long memberId;
        private int amount;

        private Debtor(Long memberId, int amount) {
            this.memberId = memberId;
            this.amount = amount;
        }
    }
}
