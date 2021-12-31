### Kotlin SpringBoot DistributedLock

* Write/Update에 대한 동시성 처리 이슈를 어떻게 해결할 수 있을까?
* DBMS 레벨에서 Lock을 관리할 수도 있지만 I/O 자체의 비용이 매우 크다고 생각. 이에 WAS 레벨 Lock을 이용하게끔 구성
* 예제의 LockSynchronizer 구현체는 Redis 이지만, 변경 가능한 구조로 설계하였음
* distributedLock Annotation 에 대한 Lock Aspect 구현
* 추후 http request time-out, 분산락 획득 실패 시 retry 등 고려 필요
