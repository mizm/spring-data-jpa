# spring-data-jpa

# 공통 인터페이스 설정
- spring boot 생략가능
- Spring
```java
    @Configuration
    @EnableJpaRepositories(basePackages = "jpabook.jpashop.repository")
    public class AppConfig {}
 
```