# 開発ロードマップ（ブランチ別TODO）

このリポジトリは、就活用ポートフォリオとして「どう作ったか」が追えるように、機能単位でブランチを分けて実装する。

## 進め方ルール

1. ブランチを切る
2. そのブランチの TODO を上から実装
3. 機能ごとにコミット
4. main にマージ

## ブランチ計画

### 1. `feat/backend-foundation`

- [x] Spring Boot プロジェクトの雛形作成
- [x] Docker Compose（frontend / backend / postgres）追加
- [x] Flyway 初期マイグレーションでテーブル作成
- [x] 共通例外ハンドラと基本設定追加

### 2. `feat/member-event-api`

- [x] Member Entity / Repository / Service / Controller
- [x] Event Entity / Repository / Service / Controller
- [x] EventParticipant 追加・削除 API
- [x] バリデーションと DTO の整備

### 3. `feat/expense-repayment-settlement-api`

- [x] Expense / ExpenseTarget CRUD API
- [x] Repayment CRUD API
- [x] SettlementService（差額計算 + 支払い提案）
- [x] Settlement API の実装

### 4. `feat/frontend-vue`

- [x] Vue + Vite フロントエンド初期化
- [x] メンバー一覧・登録 UI
- [x] イベント一覧・詳細 UI
- [x] 支出/返済登録 UI
- [x] 精算結果表示 UI

### 5. `feat/docs-and-sample`

- [x] セットアップ手順の README 整備
- [x] API 使用例の記載
- [x] ポートフォリオ観点の設計意図を追記

## 実装順序

1. backend-foundation
2. member-event-api
3. expense-repayment-settlement-api
4. frontend-vue
5. docs-and-sample
