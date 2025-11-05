# Scouter Plugin Server - Data Structure Documentation

## 개요
이 플러그인은 Scouter에서 수집할 수 있는 모든 데이터 타입을 파싱하고 저장하기 위해 확장되었습니다.

## 주요 기능

### 1. 모든 Pack 타입 지원
- **XLogPack**: 트랜잭션 로그 정보
- **AlertPack**: 알림 정보
- **PerfCounterPack**: 성능 카운터 데이터
- **ObjectPack**: 모니터링 대상 객체 정보
- **SummaryPack**: 요약 데이터
- **TextPack**: 텍스트 딕셔너리
- **XLogProfilePack**: 프로파일 데이터

### 2. Profile Message 파싱
XLogProfilePack의 profile 데이터를 상세하게 파싱하여 다음 Step 타입들을 지원합니다:
- **MethodStep/MethodStep2**: 메소드 실행 정보
- **SqlStep/SqlStep2/SqlStep3**: SQL 쿼리 실행 정보
- **ApiCallStep/ApiCallStep2**: API 호출 정보
- **MessageStep/HashedMessageStep/ParameterizedMessageStep**: 메시지 정보
- **SocketStep**: 소켓 통신 정보
- **SpanStep/SpanCallStep**: 분산 추적 정보
- **ThreadSubmitStep/ThreadCallPossibleStep**: 쓰레드 정보
- **DispatchStep**: 디스패치 정보
- **DumpStep**: 덤프 정보
- **Summary Steps**: MethodSum, SqlSum, MessageSum, ApiCallSum, SocketSum

### 3. DTO 클래스 (Lombok Builder 패턴)
모든 데이터 타입에 대해 DTO 클래스가 제공되며, Lombok의 @Builder 패턴을 사용합니다.

#### Pack DTO 예제
```java
XLogPackDTO xlogDto = XLogPackDTO.builder()
    .txid(pack.txid)
    .service(pack.service)
    .elapsed(pack.elapsed)
    .error(pack.error)
    .cpu(pack.cpu)
    .build();
```

#### Step DTO 예제
```java
SqlStepDTO sqlDto = SqlStepDTO.builder()
    .stepType(step.getStepType())
    .stepTypeName(step.getStepTypeName())
    .hash(step.hash)
    .elapsed(step.elapsed)
    .error(step.error)
    .param(step.param)
    .build();
```

## 데이터베이스 스키마

### 주요 테이블

#### scouter_xlog
트랜잭션 로그를 저장합니다.
- 필드: end_time, obj_hash, service, txid, elapsed, error, cpu, sql_count, sql_time 등
- 인덱스: txid, end_time, obj_hash, service

#### scouter_xlog_profile
프로파일 메타데이터를 저장합니다.
- 필드: time, obj_hash, service, txid, elapsed
- 인덱스: txid, time, obj_hash

#### scouter_profile_step
프로파일의 개별 스텝을 저장합니다.
- 필드: profile_id, step_index, step_type, step_type_name
- 인덱스: profile_id, step_type

#### scouter_step_method
메소드 실행 스텝을 저장합니다.
- 필드: step_id, hash, elapsed, cputime
- 인덱스: step_id, hash

#### scouter_step_sql
SQL 실행 스텝을 저장합니다.
- 필드: step_id, hash, elapsed, cputime, param, error
- 인덱스: step_id, hash

#### scouter_step_apicall
API 호출 스텝을 저장합니다.
- 필드: step_id, txid, hash, elapsed, cputime, error, address
- 인덱스: step_id, txid, hash

### 집계 테이블

#### scouter_xlog_hourly / scouter_xlog_daily
시간별/일별 트랜잭션 집계 데이터
- 필드: txn_count, error_count, total_elapsed, avg_elapsed, max_elapsed, min_elapsed 등

#### scouter_perf_counter_hourly
시간별 성능 카운터 집계 데이터
- 필드: avg_value, max_value, min_value, count

## 설정

### 플러그인 활성화
`scouter.conf` 파일에 다음 설정을 추가하세요:

```properties
# 기본 Pack 타입 활성화
ext_plugin_null_alert_enabled=true
ext_plugin_null_counter_enabled=true
ext_plugin_null_object_enabled=true
ext_plugin_null_summary_enabled=true
ext_plugin_null_xlog_enabled=true
ext_plugin_null_profile_enabled=true
ext_plugin_null_text_enabled=true

# 추가 Pack 타입 활성화 (선택사항)
ext_plugin_null_map_enabled=false
ext_plugin_null_batch_enabled=false
ext_plugin_null_dropped_xlog_enabled=false
ext_plugin_null_interaction_counter_enabled=false
ext_plugin_null_span_container_enabled=false
ext_plugin_null_span_enabled=false
ext_plugin_null_stack_enabled=false
ext_plugin_null_status_enabled=false

# 프로파일 상세 파싱 활성화
ext_plugin_null_profile_detail_enabled=true

# Pack 상세 정보 출력 (선택사항)
ext_plugin_null_detail_enabled=false

# 디버그 모드
ext_plugin_null_debug=true
```

## 빌드 및 설치

### 빌드
```bash
mvn clean package
```

### 설치
1. 빌드된 JAR 파일을 Scouter 서버의 `plugin` 디렉토리에 복사
2. Scouter 서버 재시작

## 데이터베이스 설정

### 스키마 생성
```bash
mysql -u [username] -p [database_name] < schema.sql
```

## 사용 예제

### Profile 데이터 파싱 예제
플러그인은 XLogProfilePack을 받으면 자동으로 profile 바이트 배열을 파싱하여 각 Step의 상세 정보를 출력합니다:

```
[NullPlugin-profile] XLogProfilePack{...}
  [Profile Steps] Parsing 1234 bytes
  [Profile Steps] Found 15 steps
    [0] MethodStep (type=1) hash=12345 elapsed=100 cputime=50
    [1] SqlStep (type=2) hash=67890 elapsed=50 error=0
    [2] MessageStep (type=3) hash=11111 time=75 value=0
    ...
```

## DTO 사용 가능 필드

### XLogPackDTO
모든 트랜잭션 정보를 포함:
- 기본: endTime, objHash, service, txid, elapsed, error, cpu
- SQL: sqlCount, sqlTime
- API: apicallCount, apicallTime
- 네트워크: ipaddr, countryCode, city
- 큐잉: queuingHostHash, queuingTime, queuing2ndHostHash, queuing2ndTime
- 기타: text1~5, webHash, webTime, hasDump, profileCount, profileSize

### AlertPackDTO
알림 정보:
- time, objType, objHash, level, title, message, tags

### PerfCounterPackDTO
성능 카운터:
- time, objName, timetype, data (JSON)

### ObjectPackDTO
모니터링 객체 정보:
- objType, objHash, objName, address, version, alive, wakeup, tags

### SummaryPackDTO
요약 데이터:
- time, objHash, objType, stype, table (JSON)

### TextPackDTO
텍스트 딕셔너리:
- xtype, hash, text

### Step DTO 필드

#### MethodStepDTO
- stepType, stepTypeName, hash, elapsed, cputime

#### SqlStepDTO
- stepType, stepTypeName, hash, elapsed, cputime, param, error

#### ApiCallStepDTO
- stepType, stepTypeName, txid, hash, elapsed, cputime, error, address

#### MessageStepDTO
- stepType, stepTypeName, hash, time, value

## 라이센스
Apache License 2.0

## 참고
- Scouter GitHub: https://github.com/scouter-project/scouter
- Lombok: https://projectlombok.org/
