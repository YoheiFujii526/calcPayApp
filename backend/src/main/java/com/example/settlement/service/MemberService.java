package com.example.settlement.service;

import com.example.settlement.dto.member.MemberRequest;
import com.example.settlement.dto.member.MemberResponse;
import com.example.settlement.entity.Member;
import com.example.settlement.exception.ResourceNotFoundException;
import com.example.settlement.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MemberResponse create(MemberRequest request) {
        Member member = new Member();
        apply(member, request);
        return toResponse(memberRepository.save(member));
    }

    public MemberResponse update(Long id, MemberRequest request) {
        Member member = getMemberOrThrow(id);
        apply(member, request);
        return toResponse(memberRepository.save(member));
    }

    public void delete(Long id) {
        Member member = getMemberOrThrow(id);
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public Member getMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("member not found: " + id));
    }

    private void apply(Member member, MemberRequest request) {
        member.setName(request.name());
        member.setDisplayName(request.displayName());
        member.setGrade(request.grade());
        member.setActive(request.isActive() == null || request.isActive());
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getDisplayName(),
                member.getGrade(),
                member.isActive(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
