package com.example.settlement.service;

import com.example.settlement.dto.expense.ExpenseRequest;
import com.example.settlement.dto.expense.ExpenseResponse;
import com.example.settlement.entity.*;
import com.example.settlement.exception.BadRequestException;
import com.example.settlement.exception.ResourceNotFoundException;
import com.example.settlement.repository.EventParticipantRepository;
import com.example.settlement.repository.ExpenseRepository;
import com.example.settlement.repository.ExpenseTargetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseTargetRepository expenseTargetRepository;
    private final EventService eventService;
    private final MemberService memberService;
    private final EventParticipantRepository eventParticipantRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            ExpenseTargetRepository expenseTargetRepository,
            EventService eventService,
            MemberService memberService,
            EventParticipantRepository eventParticipantRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseTargetRepository = expenseTargetRepository;
        this.eventService = eventService;
        this.memberService = memberService;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findByEventId(Long eventId) {
        eventService.getEventOrThrow(eventId);
        return expenseRepository.findByEventId(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse create(Long eventId, ExpenseRequest request) {
        Event event = eventService.getEventOrThrow(eventId);
        validateMembersInEvent(eventId, request.paidByMemberId(), request.targetMemberIds());

        Expense expense = new Expense();
        expense.setEvent(event);
        apply(expense, request);
        Expense saved = expenseRepository.save(expense);
        replaceTargets(saved, request.targetMemberIds());
        return toResponse(saved);
    }

    public ExpenseResponse update(Long expenseId, ExpenseRequest request) {
        Expense expense = getExpenseOrThrow(expenseId);
        Long eventId = expense.getEvent().getId();
        validateMembersInEvent(eventId, request.paidByMemberId(), request.targetMemberIds());

        apply(expense, request);
        Expense saved = expenseRepository.save(expense);
        replaceTargets(saved, request.targetMemberIds());
        return toResponse(saved);
    }

    public void delete(Long expenseId) {
        Expense expense = getExpenseOrThrow(expenseId);
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public Expense getExpenseOrThrow(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("expense not found: " + id));
    }

    private void apply(Expense expense, ExpenseRequest request) {
        Member payer = memberService.getMemberOrThrow(request.paidByMemberId());
        expense.setPaidByMember(payer);
        expense.setTitle(request.title());
        expense.setAmount(request.amount());
        expense.setPaidAt(request.paidAt());
        expense.setMemo(request.memo());
    }

    private void replaceTargets(Expense expense, List<Long> targetMemberIds) {
        expenseTargetRepository.deleteByExpenseId(expense.getId());
        targetMemberIds.stream().distinct().forEach(memberId -> {
            Member member = memberService.getMemberOrThrow(memberId);
            ExpenseTarget target = new ExpenseTarget();
            target.setExpense(expense);
            target.setMember(member);
            expenseTargetRepository.save(target);
        });
    }

    private void validateMembersInEvent(Long eventId, Long paidByMemberId, List<Long> targetMemberIds) {
        Set<Long> participantIds = eventParticipantRepository.findByEventId(eventId).stream()
                .map(ep -> ep.getMember().getId())
                .collect(java.util.stream.Collectors.toSet());

        if (!participantIds.contains(paidByMemberId)) {
            throw new BadRequestException("支払者はイベント参加者である必要があります");
        }

        boolean hasOutsider = targetMemberIds.stream().anyMatch(id -> !participantIds.contains(id));
        if (hasOutsider) {
            throw new BadRequestException("支出対象者はすべてイベント参加者である必要があります");
        }
    }

    private ExpenseResponse toResponse(Expense expense) {
        List<Long> targetIds = expenseTargetRepository.findByExpenseId(expense.getId()).stream()
                .map(target -> target.getMember().getId())
                .toList();
        return new ExpenseResponse(
                expense.getId(),
                expense.getEvent().getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getPaidByMember().getId(),
                expense.getPaidByMember().getDisplayName() != null ? expense.getPaidByMember().getDisplayName() : expense.getPaidByMember().getName(),
                expense.getPaidAt(),
                expense.getMemo(),
                targetIds,
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}
