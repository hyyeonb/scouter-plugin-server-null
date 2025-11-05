# Scouter Hash to Text Conversion

## 개요

Scouter는 성능 최적화를 위해 텍스트 데이터를 hash 값으로 저장하고 전송합니다. 실제 텍스트는 **TextPack**을 통해 별도로 전송되어 딕셔너리처럼 관리됩니다.

이 플러그인은 **TextService** 유틸리티를 제공하여 hash 값을 실제 텍스트로 자동 변환합니다.

## 동작 원리

### 1. 텍스트 딕셔너리 수신 (TextPack)
```java
@ServerPlugin(PluginConstants.PLUGIN_SERVER_TEXT)
public void text(TextPack pack){
    // TextPack을 TextService에 저장
    TextService.put(pack);
    // pack.xtype: 텍스트 타입 (SERVICE, SQL, METHOD, ERROR, APICALL 등)
    // pack.hash: hash 값
    // pack.text: 실제 텍스트
}
```

### 2. Hash를 Text로 변환
```java
// 프로파일 파싱 시 자동 변환
if(step instanceof SqlStep) {
    SqlStep ss = (SqlStep) step;
    String sql = TextService.getSql(ss.hash);  // hash -> 실제 SQL 문
    // 출력: hash=12345 sql="SELECT * FROM users WHERE id=?"
}
```

## TextService API

### 기본 사용법

#### 1. TextPack 저장
```java
TextService.put(textPack);
```

#### 2. Hash to Text 변환
```java
// 일반 변환 (TextTypes 상수 사용)
String text = TextService.getText(TextTypes.SQL, hashValue);

// 타입별 편의 메소드
String serviceName = TextService.getServiceName(hash);
String sql = TextService.getSql(hash);
String methodName = TextService.getMethodName(hash);
String errorMsg = TextService.getErrorMessage(hash);
String apiCall = TextService.getApiCallName(hash);
String objectName = TextService.getObjectName(hash);
String message = TextService.getHashMessage(hash);
```

### 지원 텍스트 타입

| TextType | 설명 | 편의 메소드 |
|----------|------|-------------|
| `TextTypes.SERVICE` | 서비스/트랜잭션 이름 | `getServiceName(hash)` |
| `TextTypes.SQL` | SQL 쿼리 문 | `getSql(hash)` |
| `TextTypes.METHOD` | 메소드 이름 | `getMethodName(hash)` |
| `TextTypes.ERROR` | 에러 메시지 | `getErrorMessage(hash)` |
| `TextTypes.APICALL` | API 호출 이름 | `getApiCallName(hash)` |
| `TextTypes.OBJECT` | 모니터링 객체 이름 | `getObjectName(hash)` |
| `TextTypes.HASH_MSG` | 해시 메시지 (thread name 등) | `getHashMessage(hash)` |
| `TextTypes.REFERER` | HTTP Referer | `getText(TextTypes.REFERER, hash)` |
| `TextTypes.USER_AGENT` | User Agent | `getText(TextTypes.USER_AGENT, hash)` |
| `TextTypes.GROUP` | 그룹 이름 | `getText(TextTypes.GROUP, hash)` |
| `TextTypes.CITY` | 도시 이름 | `getText(TextTypes.CITY, hash)` |
| `TextTypes.SQL_TABLES` | SQL 테이블 정보 | `getText(TextTypes.SQL_TABLES, hash)` |
| `TextTypes.LOGIN` | 로그인 정보 | `getText(TextTypes.LOGIN, hash)` |
| `TextTypes.DESC` | 설명 | `getText(TextTypes.DESC, hash)` |
| `TextTypes.WEB` | 웹 정보 | `getText(TextTypes.WEB, hash)` |
| `TextTypes.STACK_ELEMENT` | Stack Element | `getText(TextTypes.STACK_ELEMENT, hash)` |

### 캐시 관리

```java
// 캐시 통계 확인
String stats = TextService.getCacheStats();
System.out.println(stats);
/*
[TextService Cache Stats]
  service: 1234 entries
  sql: 5678 entries
  method: 3456 entries
  ...
*/

// 특정 타입의 캐시 크기
int sqlCacheSize = TextService.getCacheSize(TextTypes.SQL);

// 전체 캐시 초기화
TextService.clearCache();

// 특정 타입만 초기화
TextService.clearCache(TextTypes.SQL);
```

## 실제 사용 예제

### Profile Step 파싱 예제

```java
// Before: Hash 값만 출력
[0] SqlStep (type=2) hash=123456 elapsed=50 error=0

// After: TextService 적용 후
[0] SqlStep (type=2) hash=123456 sql="SELECT * FROM users WHERE id=?" elapsed=50 error=0

// Before: Method 정보
[1] MethodStep (type=1) hash=789012 elapsed=100 cputime=50

// After: 실제 메소드 이름
[1] MethodStep (type=1) hash=789012 method="com.example.UserService.getUser" elapsed=100 cputime=50

// Before: API Call
[2] ApiCallStep (type=6) hash=345678 elapsed=200 error=0

// After: 실제 API 이름
[2] ApiCallStep (type=6) hash=345678 api="http://api.example.com/user" elapsed=200 error=0
```

### XLog 데이터 예제

```java
@ServerPlugin(PluginConstants.PLUGIN_SERVER_XLOG)
public void xlog(XLogPack pack){
    // Service 이름 변환
    String serviceName = TextService.getServiceName(pack.service);

    // Object 이름 변환
    String objectName = TextService.getObjectName(pack.objHash);

    // Thread 이름 변환
    String threadName = TextService.getHashMessage(pack.threadNameHash);

    // Error 메시지 변환 (error가 0이 아닌 경우)
    String errorMsg = "";
    if(pack.error != 0) {
        errorMsg = TextService.getErrorMessage(pack.error);
    }

    // User Agent 변환
    String userAgent = TextService.getText(TextTypes.USER_AGENT, pack.userAgent);

    System.out.println("Transaction: " + serviceName);
    System.out.println("  Object: " + objectName);
    System.out.println("  Thread: " + threadName);
    System.out.println("  Elapsed: " + pack.elapsed + "ms");
    if(!errorMsg.isEmpty()) {
        System.out.println("  Error: " + errorMsg);
    }
}
```

## 구현 세부사항

### Scouter Client 구조 참고

본 구현은 Scouter Client의 **TextProxy** 패턴을 참고했습니다:

```java
// scouter.client.model.TextProxy
public class TextProxy {
    public static TextModel service = new TextModel(TextTypes.SERVICE, 8192);
    public static TextModel sql = new TextModel(TextTypes.SQL, 8192);
    public static TextModel method = new TextModel(TextTypes.METHOD, 4096);
    // ...
}

// 클라이언트 사용 예제
String serviceName = TextProxy.service.getLoadText(date, hash, serverId);
String sql = TextProxy.sql.getLoadText(date, hash, serverId);
```

### 서버 플러그인 구현

서버 플러그인에서는 **TextService**로 단순화하여 제공:

```java
// 간단한 API로 동일한 기능 제공
String serviceName = TextService.getServiceName(hash);
String sql = TextService.getSql(hash);
```

### 메모리 관리

- **ConcurrentHashMap** 사용으로 thread-safe 보장
- 타입별로 분리된 캐시로 효율적인 메모리 사용
- 필요시 캐시 초기화 기능 제공

## 설정

### TextPack 활성화
`scouter.conf`에서 TextPack 수신을 활성화해야 합니다:

```properties
# TextPack 수신 활성화 (기본값: true)
ext_plugin_null_text_enabled=true

# Profile 상세 파싱 활성화 (hash to text 변환 포함)
ext_plugin_null_profile_detail_enabled=true

# 디버그 모드 (변환된 텍스트 출력)
ext_plugin_null_debug=true
```

## 주의사항

### 1. TextPack이 먼저 도착해야 함
- TextPack이 도착하기 전에 profile을 파싱하면 hash 값만 출력됩니다
- 이는 정상 동작이며, TextPack이 도착하면 이후부터 변환됩니다

### 2. 캐시 미스 처리
- 캐시에 없는 hash는 hash 값 자체를 문자열로 반환합니다
```java
// 캐시에 없는 경우
String text = TextService.getSql(999999);
// 결과: "999999" (hash 값 자체)
```

### 3. 메모리 사용
- 장기 실행 시 메모리 사용량을 모니터링하세요
- 필요시 주기적으로 캐시를 초기화하세요

## 디버깅

### TextPack 수신 확인
```
[NullPlugin-text] TextPack{xtype=sql, hash=123456, text=SELECT * FROM users}
```

### 변환 결과 확인
```
[0] SqlStep (type=2) hash=123456 sql="SELECT * FROM users" elapsed=50
```

### 캐시 상태 확인
```java
if(conf.getBoolean("ext_plugin_null_debug", true)) {
    println(TextService.getCacheStats());
}
```

## 참고

- Scouter GitHub: https://github.com/scouter-project/scouter
- TextProxy 구현: `scouter.client/src/scouter/client/model/TextProxy.java`
- TextTypes 상수: `scouter.common/src/main/java/scouter/lang/TextTypes.java`
