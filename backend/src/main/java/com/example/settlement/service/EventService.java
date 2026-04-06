package com.example.settlement.service;

import com.example.settlement.dto.event.EventRequest;
import com.example.settlement.dto.event.EventResponse;
import com.example.settlement.dto.member.MemberResponse;
import com.example.settlement.entity.Event;
import com.example.settlement.entity.EventParticipant;
import com.example.settlement.entity.EventStatus;
import com.example.settlement.entity.Member;
import com.example.settlement.exception.ResourceNotFoundException;
import com.example.settlement.repository.EventParticipantRepository;
import com.example.settlement.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final MemberService memberService;

    public EventService(
            EventRepository eventRepository,
            EventParticipantRepository eventParticipantRepository,
            MemberService memberService
    ) {
        this.eventRepository = eventRepository;
        this.eventParticipantRepository = eventParticipantRepository;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        return toResponse(getEventOrThrow(id));
    }

    public EventResponse create(EventRequest request) {
        Event event = new Event();
        apply(event, request);
        return toResponse(eventRepository.save(event));
    }

    public EventResponse update(Long id, EventRequest request) {
        Event event = getEventOrThrow(id);
        apply(event, request);
        return toResponse(eventRepository.save(event));
    }

    public void delete(Long id) {
        Event event = getEventOrThrow(id);
        eventRepository.delete(event);
    }

    public void addParticipants(Long eventId, List<Long> memberIds) {
        Event event = getEventOrThrow(eventId);
        memberIds.stream().distinct().forEach(memberId -> {
            if (eventParticipantRepository.findByEventIdAndMemberId(eventId, memberId).isPresent()) {
                return;
            }
            Member member = memberService.getMemberOrThrow(memberId);
            EventParticipant participant = new EventParticipant();
            participant.setEvent(event);
            participant.setMember(member);
            eventParticipantRepository.save(participant);
        });
    }

    public void removeParticipant(Long eventId, Long memberId) {
        EventParticipant participant = eventParticipantRepository.findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("event participant not found: event=" + eventId + ", member=" + memberId));
        eventParticipantRepository.delete(participant);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getParticipants(Long eventId) {
        getEventOrThrow(eventId);
        return eventParticipantRepository.findByEventId(eventId).stream()
                .map(EventParticipant::getMember)
                .map(member -> new MemberResponse(
                        member.getId(),
                        member.getName(),
                        member.getDisplayName(),
                        member.getGrade(),
                        member.isActive(),
                        member.getCreatedAt(),
                        member.getUpdatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("event not found: " + id));
    }

    private void apply(Event event, EventRequest request) {
        event.setTitle(request.title());
        event.setEventDate(request.eventDate());
        event.setDescription(request.description());
        event.setStatus(request.status() == null ? EventStatus.UNSETTLED : request.status());
    }

    private EventResponse toResponse(Event event) {
        int participantCount = eventParticipantRepository.findByEventId(event.getId()).size();
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getDescription(),
                event.getStatus(),
                participantCount,
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
