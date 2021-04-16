# spring-data-jpa

# 공통 인터페이스 설정
- spring boot 생략가능
- Spring
```java
    @Configuration
    @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
    public class AppConfig {}
 
```

## 쿼리 메소드 이름으로 생성
- findByUsername 가능

## 페이징과 sort
- Page<Member> page = totalCount 쿼리가 나감
- Slice<Member> slice = totalCount 쿼리가 안나감 -> 다음 번에 있는지만 확인 가능.
- List<Member> 물론 list로 받을 수 있음 , 추가 카운트 쿼리따위없다
- Page는 제로베이스다.

## 벌크성 수정쿼리
- 모든 직원의 연봉 인상  한번에 하는 쿼리가 좋다
- spring data jpa -> @Modifying을 달아줘야한다.
- 벌크연산 때 조심해야할 것 + jdbc 템플릿 등이랑 같이 쓸때도 조심하자.
    - 영속성 컨텍스트에 반영되는게 아니고 바로 디비에 반영되는 거임
    - 영속성 컨텍스트에 있는 클래스와 디비와 값이 다른 문제가 새긴다.
    - 영속성 컨텍스트를 em.clear() or @Modifying(clearAutomatically = true) 해준다

## 새로운 엔티티를 구별하는 방법
- 식별자가 객체일때 null로 판단
- 식별자가 자바 기본타입이면 0으로 판단


## 명세(Specifications)
- 참 또는 거짓 평가
- And Or 같은 연산자로 조합해 다양한 검색조건을 쉽게 생성
- JPA는 Criteria를 활용한다.
    - 실무에서 쓰기가 어렵다.
    - 유지보수가 너무 힘들다
## query By Example
- probe -> 실제 도메인 객체를 가지고 그대로 검색조건을 만듬
- example.of(new Member("m1"))
- left join 해결이 불가
- ExampleMatcher
- 장점
    - 동적 쿼리를 그나마 편하게 만들 수 있음
    - 도매인 객체를 그대로 사용한다
    - 데이터베이스 변경이랑 상관 없이 추상화 되어있음
- 단점
    - inner join 제외 해결 불가
    - 중첩제약 불가
        - firstname = ?0 or (firstname = ?1 and lastname = ?2)
    - eq정도만 가능
## Projections
- 엔티티 대신에 DTO를 편리하게 조회할때 사용
- projection 대상이 root가 아니면 모든 필드를 조회해서 계산한다.
- 실무의 복잡한 쿼리를 해결할 수 없다.
## jpa native query
- 정말 최후의 수단으로 사용하자
- 반환타입이 지원되지 않음
- 동적쿼리불가
- 네이티브가 필요하면 jdbctemplate이나 mybatis를 쓰자