package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    // 1+N 번 쿼리 실행
    public List<OrderQueryDto> findOrderQueryDtos() {
        //ToOne 가져오기
        List<OrderQueryDto> result = findOrders(); //Query 1번 -> N개

        //ToMany 데이터 채우기
        result.forEach(o -> { //Query N번
            List<OrderItemQueryDto> orderItems = finOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    //쿼리 2번 실행으로 최적화
    public List<OrderQueryDto> findAllByDto_optimization() {
        //루트 다 조회
        List<OrderQueryDto> result = findOrders();

        //주문 데이터만큼 한번에 메모리 올리기
        List<Long> orderIds = toOrderIds(result);
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        //모자란 컬렉션 데이터 채우기
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        //in 사용해서 한번에 담아주기
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id ,i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        //map으로 변경. 메모리에 담아주기
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        //매칭하여 값 세팅
        return orderItemMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    private List<OrderItemQueryDto> finOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id ,i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                    " from Order o" +
                    " join o.member m" +
                    " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                    " from Order o" +
                    " join o.member m" +
                    " join o.delivery d" +
                    " join o.orderItems oi" +
                    " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
