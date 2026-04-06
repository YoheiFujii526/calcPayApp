package com.example.settlement.repository;

import com.example.settlement.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByEventId(Long eventId);

    Optional<EventParticipant> findByEventIdAndMemberId(Long eventId, Long memberId);
}
