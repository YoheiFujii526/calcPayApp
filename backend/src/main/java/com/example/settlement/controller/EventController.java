package com.example.settlement.controller;

import com.example.settlement.dto.event.EventRequest;
import com.example.settlement.dto.event.EventResponse;
import com.example.settlement.dto.event.ParticipantAddRequest;
import com.example.settlement.dto.member.MemberResponse;
import com.example.settlement.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventResponse> getEvents() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.create(request);
    }

    @PutMapping("/{id}")
    public EventResponse updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

    @GetMapping("/{id}/participants")
    public List<MemberResponse> getParticipants(@PathVariable Long id) {
        return eventService.getParticipants(id);
    }

    @PostMapping("/{id}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addParticipants(@PathVariable Long id, @Valid @RequestBody ParticipantAddRequest request) {
        eventService.addParticipants(id, request.memberIds());
    }

    @DeleteMapping("/{id}/participants/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(@PathVariable Long id, @PathVariable Long memberId) {
        eventService.removeParticipant(id, memberId);
    }
}
