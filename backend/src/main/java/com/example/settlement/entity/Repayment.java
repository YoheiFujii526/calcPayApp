package com.example.settlement.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "repayments")
public class Repayment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id")
    private Member toMember;

    @Column(nullable = false)
    private int amount;

    @Column(name = "repaid_at", nullable = false)
    private LocalDate repaidAt;

    @Column(columnDefinition = "TEXT")
    private String memo;

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Member getFromMember() {
        return fromMember;
    }

    public void setFromMember(Member fromMember) {
        this.fromMember = fromMember;
    }

    public Member getToMember() {
        return toMember;
    }

    public void setToMember(Member toMember) {
        this.toMember = toMember;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDate getRepaidAt() {
        return repaidAt;
    }

    public void setRepaidAt(LocalDate repaidAt) {
        this.repaidAt = repaidAt;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
