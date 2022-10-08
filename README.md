# JDBC 라이브러리 구현하기

## 1단계 - JDBC 라이브러리 구현하기

- **기능 요구사항**
    - 개발자는 SQL 쿼리 작성, 쿼리에 전달한 인자, SELECT 구문일 경우 조회 결과를 추출하는 것만 집중할 수 있도록 라이브러리를 만들자.
- **체크리스트**
    - [x] UserDaoTest의 모든 테스트 케이스가 통과한다.
    - [x] UserDao가 아닌 JdbcTemplate 클래스에서 JDBC와 관련된 처리를 담당하고 있다.

### JdbcTemplate 구현

|                       | statement 값 지정 | resultSet 필요 | return type  |
|----------------------|:--------------:|:------------:|:------------:|
|      insert(User)     |       ✅        |      ❌       |    `void`    |
|      update(User)     |      ✅      |      ❌      |    `void`     |
|       findAll()       |      ❌     |      ✅       | `List<User>` |
|     findById(Long)    |       ✅     |      ✅       |    `User`    |
| findByAccount(String) |      ✅      |      ✅       |    `User`    |

- **메서드 공통**
    - `DataSource`를 이용하여 `Connection`을 연결한다.
    - `Connection`을 이용하여 `Statement`를 얻는다.
    - 특징에 맞게 세부사항을 구현한다.
    - 작업을 마치면 리소스들을 `close` 한다.

**구현**

- [x] `execute`를 구현한다(insert, update) : param 값은 지정할 수 있지만 resultSet이 따로 필요없는 경우
    - param을 설정할 수 있어야한다.
        - [x] Map을 활용한 방식으로 구현해본다.
    - return으로는 뭘 줄까? -> 일단 return type이 필요없으므로 `void`.. 더 고민해보자
- [x] `query`를 구현한다.(findById, findByAccount) : param 값을 지정받고, 조회 결과를 반환하는 경우 ( 단건조회 )
    - param을 설정할 수 있어야한다.
        - [x] execute와 마찬가지로 구현해본다. ( Map 버전 )
    - `resultSet`은 statement가 close되면 null로 바뀌게 된다.
        - 이를 해결하기 위해서는?
            - 내부에서 resultSet에 대한 처리를 마친 후에 반환한다.
            - resultSet을 내부적으로 처리해서 반환한다.
    - [x] Functional interface를 활용한다.
- [x] `queryForList`를 구현한다.(findAll) : 조회 결과로 리스트를 반환하는 경우
    - param을 설정할 수 있어야하지만 findAll에서는 필요없다.
        - [x] 일단 Map 버전으로 구현해본다.
    - [x] List 형식으로 반환하도록 구현한다.

**리팩토링**

- [x] 함수형 인터페이스를 바로 쓰면 예외처리를 해야하는 문제를 해결한다.
- [x] param이 필요없는 경우 map에 빈 맵을 주는 것을 개선한다.
    - 가변인자를 활용하는 방식으로 수정하여 개선한다.
- [x] 예외를 처리한다.
    - [x] `executeQuery` 의 결과가 없는 경우
    - [x] `executeQuery` 의 결과가 둘 이상인 경우

- [x] 부족한 테스트를 추가한다.
    - JdbcTemplate 테스트로 QueryExecutor를 간접 테스트한다.
    - [x] `executeQuery` 의 결과가 없는 경우
    - [x] `executeQuery` 의 결과가 둘 이상인 경우

## 3단계 - Transaction 적용하기

**체크 리스트**

- [x] 트랜잭션 롤백이 적용되어 UserServiceTest 클래스의 testTransactionRollback() 테스트 케이스가 통과한다.
- [ ] 트랜잭션 서비스와 애플리케이션 서비스가 분리되었다.
