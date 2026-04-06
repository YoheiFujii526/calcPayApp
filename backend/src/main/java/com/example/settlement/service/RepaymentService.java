package com.example.settlement.service;

import com.example.settlement.dto.repayment.RepaymentRequest;
import com.example.settlement.dto.repayment.RepaymentResponse;
import com.example.settlement.entity.Event;
import com.example.settlement.entity.Member;
import com.example.settlement.entity.Repayment;
import com.example.settlement.exception.BadRequestException;
import com.example.settlement.exception.ResourceNotFoundException;
import com.example.settlement.repository.EventParticipantRepository;
import com.example.settlement.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final EventService eventService;
    private final MemberService memberService;
    private final EventParticipantRepository eventParticipantRepository;

    public RepaymentService(
            RepaymentRepository repaymentRepository,
            EventService eventService,
            MemberService memberService,
            EventParticipantRepository eventParticipantRepository
    ) {
        this.repaymentRepository = repaymentRepository;
        this.eventService = eventService;
        this.memberService = memberService;
        this.eventParticipantRepository = eventParticipantRepository;
    }

    @Transactional(readOnly = true)
    public List<RepaymentResponse> findByEventId(Long eventId) {
        eventService.getEventOrThrow(eventId);
        return repaymentRepository.findByEventId(eventId).stream()
                .map(this::toResponse)
                .toList();
    }

    public RepaymentResponse create(Long eventId, RepaymentRequest request) {
        if (request.fromMemberId().equals(request.toMemberId())) {
            throw new BadRequestException("返済者と受取者は同一にできません");
        }

        Event event = eventService.getEventOrThrow(eventId);
        validateMembersInEvent(eventId, request.fromMemberId(), request.toMemberId());

        Repayment repayment = new Repayment();
        repayment.setEvent(event);
        apply(repayment, request);
        return toResponse(repaymentRepository.save(repayment));
    }

    public RepaymentResponse update(Long repaymentId, RepaymentRequest request) {
        if (request.fromMemberId().equals(request.toMemberId())) {
            throw new BadRequestException("返済者と受取者は同一にできません");
        }

        Repayment repayment = getRepaymentOrThrow(repaymentId);
        validateMembersInEvent(repayment.getEvent().getId(), request.fromMemberId(), request.toMemberId());

        apply(repayment, request);
        return toResponse(repaymentRepository.save(repayment));
    }

    public void delete(Long repaymentId) {
        Repayment repayment = getRepaymentOrThrow(repaymentId);
        repaymentRepository.delete(repayment);
    }

    @Transactional(readOnly = true)
    public Repayment getRepaymentOrThrow(Long id) {
        return repaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("repayment not found: " + id));
    }

    private void apply(Repayment repayment, RepaymentRequest request) {
        Member fromMember = memberService.getMemberOrThrow(request.fromMemberId());
        Member toMember = memberService.getMemberOrThrow(request.toMemberId());
        repayment.setFromMember(fromMember);
        repayment.setToMember(toMember);
        repayment.setAmount(request.amount());
        repayment.setRepaidAt(request.repaidAt());
        repayment.setMemo(request.memo());
    }

    private void validateMembersInEvent(Long eventId, Long fromMemberId, Long toMemberId) {
        Set<Long> participantIds = eventParticipantRepository.findByEventId(eventId).stream()
                .map(ep -> ep.getMember().getId())
                .collect(Collectors.toSet());

        if (!participantIds.contains(fromMemberId) || !participantIds.contains(toMemberId)) {
            throw new BadRequestException("返済者・受取者はイベント参加者である必要があります");
        }
    }

    private RepaymentResponse toResponse(Repayment repayment) {
        return new RepaymentResponse(
                repayment.getId(),
                repayment.getEvent().getId(),
                repayment.getFromMember().getId(),
                repayment.getFromMember().getDisplayName() != null ? repayment.getFromMember().getDisplayName() : repayment.getFromMember().getName(),
                repayment.getToMember().getId(),
                repayment.getToMember().getDisplayName() != null ? repayment.getToMember().getDisplayName() : repayment.getToMember().getName(),
                repayment.getAmount(),
                repayment.getRepaidAt(),
                repayment.getMemo(),
                repayment.getCreatedAt(),
                repayment.getUpdatedAt()
        );
    }
}
