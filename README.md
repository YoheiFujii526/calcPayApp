# calcPayApp

研究室イベント向けの精算管理アプリです。  
「メンバー管理」「イベント管理」「支出/返済登録」「最終精算計算」を行います。

## 技術スタック

- Frontend: Vue 3 + Vite
- Backend: Java 21 + Spring Boot + Spring Data JPA + Validation
- DB: PostgreSQL
- Migration: Flyway
- Local: Docker Compose

## 画面/機能

- メンバー一覧・追加・編集・削除
- イベント一覧・追加・編集・削除
- イベント参加者追加・削除
- 支出登録/一覧/削除
- 返済登録/一覧/削除
- 精算結果表示（差額と支払い提案）

## API エンドポイント（主要）

- `GET/POST /api/members`
- `PUT/DELETE /api/members/{id}`
- `GET/POST /api/events`
- `GET/PUT/DELETE /api/events/{id}`
- `POST /api/events/{id}/participants`
- `DELETE /api/events/{id}/participants/{memberId}`
- `GET/POST /api/events/{id}/expenses`
- `PUT/DELETE /api/expenses/{id}`
- `GET/POST /api/events/{id}/repayments`
- `PUT/DELETE /api/repayments/{id}`
- `GET /api/events/{id}/settlement`

## ローカル起動

1. `.env.example` を `.env` としてコピー
2. 起動

```bash
docker compose up --build
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Postgres: `localhost:5432`

## トラブルシュート

### 画面に `初期データ取得に失敗: Failed to fetch` と表示される

- 原因例: backend コンテナが起動失敗している
- 本リポジトリで発生した事例: Flyway が PostgreSQL 16 を認識できず起動失敗
- 対応: `backend/build.gradle` に `org.flywaydb:flyway-database-postgresql` を追加済み

確認コマンド:

```bash
docker compose ps
docker compose logs backend --tail=100
```

## ポートフォリオ向けの開発方針

このリポジトリでは、実装過程が追えるように以下を徹底しています。

1. ブランチを機能単位で分割
2. ブランチごとに TODO を定義
3. 機能を 1 つ実装するごとにコミット
4. 完了後に `main` へマージ

詳細は [docs/BRANCH_TODO.md](docs/BRANCH_TODO.md) と [docs/IMPLEMENTATION_LOG.md](docs/IMPLEMENTATION_LOG.md) を参照してください。
