package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    /*
    * 엔티티 직접 외부노출 호출하면
    * 엔티티 수정시 api 스펙이 변경되어 장애발생
    * */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
    
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

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

    /*
    * 수정은 제한적이라 별도의 request, response 가져가는것이 좋음
    * */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        //커맨드와 쿼리 철저하게 분리. 변경성 메소드로 조회는 맞지않음. 업데이트는 void나 id값 반환 정도로 함
        memberService.update(id, request.getName());
        //재조회 확인
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    @Data
    static class UpdateMemberRequest {
        private String name;

    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
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
