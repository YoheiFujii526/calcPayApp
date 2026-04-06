# 開発ロードマップ（ブランチ別TODO）

このリポジトリは、就活用ポートフォリオとして「どう作ったか」が追えるように、機能単位でブランチを分けて実装する。

## 進め方ルール

1. ブランチを切る
2. そのブランチの TODO を上から実装
3. 機能ごとにコミット
4. main にマージ

## ブランチ計画

### 1. `feat/backend-foundation`

- [ ] Spring Boot プロジェクトの雛形作成
- [ ] Docker Compose（frontend / backend / postgres）追加
- [ ] Flyway 初期マイグレーションでテーブル作成
- [ ] 共通例外ハンドラと基本設定追加

### 2. `feat/member-event-api`

- [ ] Member Entity / Repository / Service / Controller
- [ ] Event Entity / Repository / Service / Controller
- [ ] EventParticipant 追加・削除 API
- [ ] バリデーションと DTO の整備

### 3. `feat/expense-repayment-settlement-api`

- [ ] Expense / ExpenseTarget CRUD API
- [ ] Repayment CRUD API
- [ ] SettlementService（差額計算 + 支払い提案）
- [ ] Settlement API の実装

### 4. `feat/frontend-vue`

- [ ] Vue + Vite フロントエンド初期化
- [ ] メンバー一覧・登録 UI
- [ ] イベント一覧・詳細 UI
- [ ] 支出/返済登録 UI
- [ ] 精算結果表示 UI

### 5. `feat/docs-and-sample`

- [ ] セットアップ手順の README 整備
- [ ] API 使用例の記載
- [ ] ポートフォリオ観点の設計意図を追記

## 実装順序

1. backend-foundation
2. member-event-api
3. expense-repayment-settlement-api
4. frontend-vue
5. docs-and-sample
