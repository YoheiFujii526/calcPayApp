package com.example.settlement.service;

import com.example.settlement.dto.event.EventRequest;
import com.example.settlement.dto.event.EventResponse;
import com.example.settlement.entity.Event;
import com.example.settlement.entity.EventStatus;
import com.example.settlement.exception.ResourceNotFoundException;
import com.example.settlement.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    public EventService(
            EventRepository eventRepository
    ) {
        this.eventRepository = eventRepository;
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
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getEventDate(),
                event.getDescription(),
                event.getStatus(),
                0,
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
