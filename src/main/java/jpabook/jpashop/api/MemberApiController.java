package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /*
    - RequestBody : json 데이터를 객체로 받기
    - API에서는 엔티티를 외부노출 또는 파라미터로 받지말것 -> 이름 변경시 대형장애 -> 별도의 DTO 사용 v2
    - API 스펙문서를 까보지 않으면 entity만 보고 파라미터 어디까지 넘어오는지 알 수 없음
    * */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*
    * 별도의 DTO 사용
    * Member 사용시 파라미터 뭐가 넘어올 지 모름
    * API에서는 엔티티를 파라미터로 받지말것 -> 이름 변경시 대형장애 -> DTO 생성 -> 엔티티 이름 변경되면 컴파일 오류나서 수정가능. api오류 막을 수 있음
    * API 스펙 확인 가능
    * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest {
        //DTO에서 @Valid 체크 세팅
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}