# 実装ログ（就活向け）

このログは「どう作ったか」を追えるように、ブランチ戦略とコミット粒度を明示するための記録です。

## ブランチ構成

1. `chore/roadmap`
2. `feat/backend-foundation`
3. `feat/member-event-api`
4. `feat/expense-repayment-settlement-api`
5. `feat/frontend-vue`
6. `feat/docs-and-sample`

## 機能単位コミット例

### backend-foundation

- `feat(backend): scaffold spring boot application`
- `feat(infra): add docker compose for frontend backend postgres`
- `feat(db): add flyway initial schema migration`
- `feat(backend): add global exception handling and cors config`

### member-event-api

- `feat(member): add member management api`
- `feat(event): add event management api`
- `feat(event): add participant management api`

### expense-repayment-settlement-api

- `feat(expense): add expense and target management api`
- `feat(repayment): add repayment management api`
- `feat(settlement): add settlement calculation api`

### frontend-vue

- `feat(frontend): scaffold vue vite application`
- `feat(frontend): add member management ui`
- `feat(frontend): add event detail expense repayment settlement screens`
- `chore(frontend): add lockfile and gitignore`

## 設計上のポイント

- Entity を API へ直接返さず DTO 経由に統一
- バリデーションを request DTO に寄せ、`@RestControllerAdvice` で共通エラー処理
- 精算ロジックを `SettlementService` に集約
- フロントは API クライアントを `src/api.js` に分離
