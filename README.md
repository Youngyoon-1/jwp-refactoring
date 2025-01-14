# 키친포스

## 요구 사항
### 상품
- 상품 한개를 등록 한다.
  - 상품 가격은 0원 이상이여야 한다.
- 상품 전체를 조회 한다.

### 메뉴
- 메뉴는 메뉴 그룹과 메뉴 상품에 의존한다.
  - 메뉴 상품은 상품, 메뉴에 의존한다.
- 메뉴 한개를 등록 한다.
  - 메뉴의 가격은 0원 이상이어야 한다.
  - 메뉴 그룹이 없는 메뉴는 등록할 수 없다.
  - 메뉴의 가격은 상품들의 가격 합보다 커서는 안된다.
  - 메뉴 상품에 메뉴 ID 가 추가되어 등록된다.
- 메뉴 전체를 조회 한다.

### 메뉴 그룹
- 메뉴 그룹을 한개 등록한다.
- 메뉴 그룹 전체를 조회힌다.

### 테이블
- 테이블 그룹에 의존한다.
- 주문 테이블을 한개 등록한다.
  - 테이블 그룹이 지정되지 않은 상태에서 주문 테이블을 등록한다.
- 주문 테이블 전체를 조회한다.
- 한개의 주문 테이블을 빈 테이블로 수정한다.
  - 테이블 그룹이 지정된 주문 테이블은 빈 테이블로 수정할 수 없다.
  - 주문 상태가 조리, 식사 인 경우 빈 테이블로 수정할 수 없다.
    - OrderDAO 에 의존한다.
- 한개의 주문 테이블의 방문한 손님 수를 수정한다.
  - 손님 수는 0 이상이어야 한다.
  - 비어있는 상태의 주문 테이블은 손님 수를 수정할 수 없다.

### 테이블 그룹
- N 개의 주문 테이블에 의존한다.
- 테이블 그룹 제거시 주문에 의존한다.
- 테이블 그룹을 한개 등록한다.
  - 주문 테이블의 개수가 2개 미만인 경우 테이블 그룹을 등록할 수 없다.
  - 비어있지 않은 주문 테이블, 이미 테이블 그룹으로 지정된 주문 테이블이 한개라도 있다면 테이블 그룹을 등록할 수 없다.
  - 주문 테이블이 테이블 그룹 ID, 채워져있는 상태 로 수정되어 저장된다. 
- 테이블 그룹을 제거한다.
  - OrderDAO 에 의존한다.
  - 주문 테이블의 주문 상태가 조리, 식사 인 경우가 한개라도 있으면 테이블 그룹을 제거할 수 없다.
  - 주문 테이블이 테이블 그룹 ID 가 null, 비어있는 상태로 수정된다.
### 주문
- 주문 테이블, 주문 항목에 의존한다.
  - 주문 항목은 주문, 메뉴에 의존한다.
- 주문을 한개 등록한다.
  - 주문 항목에 등록되어 있지 않은 메뉴가 한개라도 있다면 주문을 등록할 수 없다.
  - 주문 테이블이 등록되어 있지 않은 경우 주문을 등록할 수 없다.
  - 주문 테이블이 비어 있는 경우 주문을 등록할 수 없다.
  - 주문 상태가 조리로 등록된다. 
  - 주문 항목에 주문 ID 가 추가되어 저장된다.
  - 주문 항목과 함께 반환한다.
- 주문 전체를 조회한다.
  - 주문 항목과 함께 조회한다.
- 주문 상태를 수정한다.
  - 주문 상태가 완료된 경우 주문 상태를 수정할 수 없다.
    - 주문 항목과 함께 반환한다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |

## 레거시 코드에 멀티모듈 적용하기
1. 코드 분석, 요구사항 정리