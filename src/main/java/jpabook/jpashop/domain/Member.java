package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

//    @NotEmpty //@Valid 체크
    private String name;
    // 엔티티 그대로 파라미터로 사용시 이렇게 이름이 바뀌면 장애남.
//    private String username;

    @Embedded
    private Address address;

    @JsonIgnore //JsonIgnore : 엔티티에서 사용 최악. 화면에 종속적
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
