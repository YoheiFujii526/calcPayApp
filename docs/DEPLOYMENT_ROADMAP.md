# calcPayApp デプロイロードマップ

**作成日:** 2026年4月6日  
**対象バージョン:** v1.0  
**本番環境:** Vercel (Frontend) + Render (Backend) + Supabase Postgres (Database)

---

## 📋 ロードマップ全体図

```
┌─────────────────────────────────────────────────────────────┐
│ Phase 1: 本番環境アカウント・リソース準備 (2h)            │
│ ├─ Supabase PostgreSQL プロジェクト作成                    │
│ ├─ Render アカウント準備                                   │
│ └─ Vercel アカウント・GitHub 連携                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Phase 2: バックエンド本番化 (1.5h)                         │
│ ├─ application-prod.yml 作成                               │
│ ├─ Dockerfile 最適化                                       │
│ ├─ WebConfig CORS 設定                                     │
│ └─ Gradle ビルド確認                                       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Phase 3: Render へのバックエンドデプロイ (1h)             │
│ ├─ Render プロジェクト作成                                 │
│ ├─ GitHub 連携設定                                         │
│ ├─ 環境変数登録                                            │
│ └─ 初回デプロイ・ログ確認                                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Phase 4: フロントエンド本番化 (1h)                         │
│ ├─ src/api.js 環境変数切り分け                             │
│ ├─ vite.config.js 最適化                                   │
│ └─ package.json build 確認                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Phase 5: Vercel へのフロントエンドデプロイ (45min)        │
│ ├─ Vercel プロジェクト作成                                 │
│ ├─ GitHub 連携・自動デプロイ設定                           │
│ ├─ 環境変数登録                                            │
│ └─ デプロイ確認                                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ Phase 6: 統合テスト・最適化 (1.5h)                         │
│ ├─ エンドツーエンド接続テスト                               │
│ ├─ 機能別テスト                                            │
│ ├─ パフォーマンス確認                                      │
│ └─ ログ・監視設定                                          │
└─────────────────────────────────────────────────────────────┘
```

**⏱️ 総所要時間:** 約 8h

---

## 🎯 Phase 1: 本番環境アカウント・リソース準備

### 1-1 Supabase PostgreSQL セットアップ

**目的:** 本番データベース構築

**実行内容:**
1. https://supabase.com にアクセス
2. GitHub アカウントでサインアップ
3. 新規 Organization 作成: `calcpay-prod`
4. 新規プロジェクト作成
   - Project Name: `calcpay-db`
   - Region: `Tokyo`
   - Database Password: **強力なパスワード** (保存)
5. プロジェクト作成完了を待つ（約 3 分）
6. 左サイドバーの **Database** セクションで接続情報を確認
   - ダッシュボード左側のメニューから **Database** をクリック
   - ページ上部の **Connection string** または接続情報エリアで以下をコピー：
     - **Host URL:** `db.xxxxxx.supabase.co`
     - **Port:** `5432`
     - または **Connection String** をそのままコピー

**出力物:**
```
DB_URL=jdbc:postgresql://db.xxxxxx.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=[設定したパスワード]
```

---

### 1-2 Render アカウント準備

**目的:** バックエンド本番ホスティング

**実行内容:**
1. https://render.com にアクセス
2. GitHub アカウントでサインアップ
3. ダッシュボード → Create → Web Service を確認（後で使用）

**準備完了の確認:**
- Render ダッシュボードにアクセス可能
- GitHub 連携が確認できる

---

### 1-3 Vercel アカウント・GitHub 連携

**目的:** フロントエンド本番ホスティング

**実行内容:**
1. https://vercel.com にアクセス
2. GitHub アカウントでサインアップ
3. GitHub App インストール時、calcPayApp リポジトリへのアクセスを許可
4. Vercel ダッシュボードにアクセス可能か確認

**準備完了の確認:**
- Vercel ダッシュボードからリポジトリが見える
- GitHub との連携が確認できる

---

## 🔧 Phase 2: バックエンド本番化

### 2-1 application-prod.yml 作成

**ファイル:** `backend/src/main/resources/application-prod.yml`

**内容:**
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /

logging:
  level:
    root: WARN
    com.example.settlement: INFO
```

**ポイント:**
- `ddl-auto: validate` → マイグレーションは Flyway で管理
- `maximum-pool-size: 10` → 接続プール設定
- `logging` → 本番ではWARN以上のみ出力

---

### 2-2 Dockerfile 最適化

**ファイル:** `backend/Dockerfile`

**内容:**
```dockerfile
# ビルドステージ
FROM gradle:8.5-jdk21 as builder
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# 実行ステージ
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache dumb-init
ENTRYPOINT ["dumb-init", "java", "-jar", "app.jar"]
COPY --from=builder /app/build/libs/settlement-*.jar app.jar
EXPOSE 8080
```

**ポイント:**
- マルチステージビルド → 最終イメージサイズ最小化
- 不要なテストをスキップ (`-x test`)
- Alpine ベース → 軽量

---

### 2-3 WebConfig CORS 設定

**ファイル:** `backend/src/main/java/com/example/settlement/config/WebConfig.java`

**確認・修正:**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String frontendUrl = System.getenv("FRONTEND_URL");
        
        registry.addMapping("/**")
                .allowedOrigins(
                    frontendUrl,                    // 本番
                    "http://localhost:5173"         // ローカル
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
```

**環境変数:** `FRONTEND_URL=https://<vercel-project>.vercel.app`

---

### 2-4 ローカルビルド確認

**実行:**
```bash
cd backend
./gradlew clean build -x test
```

**確認:**
- ✅ ビルド成功 (`BUILD SUCCESSFUL`)
- ✅ `build/libs/settlement-*.jar` が生成されている

---

## 🚀 Phase 3: Render へのバックエンドデプロイ

### 3-1 Render プロジェクト作成

**実行:**
1. Render ダッシュボード → Create → Web Service
2. Repository: calcPayApp を選択
3. 詳細設定:
   - **Name:** `calcpay-api`
   - **Region:** `Tokyo`
   - **Branch:** `main`
   - **Build Command:** `./gradlew build -x test`
   - **Start Command:** `java -jar build/libs/settlement-*.jar`
   - **Plan:** `Free` (初期段階)

---

### 3-2 環境変数登録

**実行:**
Settings → Environment に以下を追加:

| 変数名 | 値 |
|------|-----|
| `DB_URL` | `jdbc:postgresql://[HOST]:[PORT]/postgres?sslmode=require` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `[SUPABASE_PASSWORD]` |
| `SERVER_PORT` | `8080` |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `FRONTEND_URL` | `https://<vercel-project>.vercel.app` |

---

### 3-3 初回デプロイ

**実行:**
1. Render ダッシュボード → Manual Deploy
2. ビルド・デプロイログを監視
3. 完了待機（約 5-10 分）

**ログ確認ポイント:**
```
✅ "Attaching to service..." → デプロイ開始
✅ "Listening on 0.0.0.0:8080" → サーバー起動成功
❌ "Connection refused" → DB 接続失敗（環境変数確認）
```

**デプロイ URL:** `https://calcpay-api.onrender.com`

---

### 3-4 動作確認

**テスト:**
```bash
curl https://calcpay-api.onrender.com/members
# [200] OK で JSON が返ってくることを確認
```

---

## 📱 Phase 4: フロントエンド本番化

### 4-1 src/api.js 環境変数切り分け

**ファイル:** `frontend/src/api.js`

**修正:**
```javascript
// ローカル/本番の自動切り分け
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL 
    || 'http://localhost:8080';

export const fetchMembers = () => 
    fetch(`${API_BASE_URL}/members`).then(res => res.json());

export const createEvent = (data) => 
    fetch(`${API_BASE_URL}/events`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    }).then(res => res.json());

// ... その他のAPI
```

**ビルド時:**
- 開発: `VITE_API_BASE_URL=http://localhost:8080`
- 本番: `VITE_API_BASE_URL=https://calcpay-api.onrender.com`

---

### 4-2 vite.config.js 最適化

**ファイル:** `frontend/vite.config.js`

**内容:**
```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    outDir: 'dist',
    sourcemap: false,          // 本番は sourcemap 不要
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true     // console.log 削除
      }
    }
  }
})
```

---

### 4-3 package.json build 確認

**確認:**
```bash
cd frontend
npm run build
# dist/ ディレクトリが生成されたか確認
```

---

## 🌐 Phase 5: Vercel へのフロントエンドデプロイ

### 5-1 Vercel プロジェクト作成

**実行:**
1. Vercel ダッシュボード → Add New → Project
2. calcPayApp リポジトリを選択
3. 詳細設定:
   - **Framework Preset:** `Vite`
   - **Root Directory:** `frontend`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`

---

### 5-2 環境変数登録

**Settings → Environment Variables:**

| 変数名 | 値 |
|------|-----|
| `VITE_API_BASE_URL` | `https://calcpay-api.onrender.com` |

---

### 5-3 デプロイ実行

**実行:**
1. Vercel ダッシュボード → Deploy
2. デプロイログを監視
3. 完了待機（約 2-3 分）

**デプロイ URL:** `https://calcpay-<random>.vercel.app`

**カスタムドメイン設定（オプション）:**
- Settings → Domains → Add Domain
- 独自ドメイン取得後に設定可能

---

### 5-4 動作確認

**テスト:**
1. ブラウザで `https://calcpay-<random>.vercel.app` にアクセス
2. ページが読み込まれるか確認
3. DevTools (F12) → Network で API 呼び出しが成功しているか確認

---

## ✅ Phase 6: 統合テスト・最適化

### 6-1 エンドツーエンド接続テスト

**テストシナリオ:**

| # | 機能 | 期待動作 | 確認方法 |
|----|------|--------|--------|
| 1 | メンバー一覧表示 | API 呼び出し成功 | DevTools → Network → 200 OK |
| 2 | メンバー登録 | DB に保存 | Supabase UI で確認 |
| 3 | イベント作成 | イベント一覧に反映 | UI で確認 |
| 4 | 支出登録 | 精算計算が実行される | 精算結果が表示される |

---

### 6-2 ブラウザコンソール エラー確認

**DevTools 実行:**
```
F12 キー → Console タブ
→ エラーがないか確認
→ API 呼び出しの Response を確認
```

---

### 6-3 パフォーマンス確認

**Lighthouse スコア測定:**
1. Vercel デプロイページ → Analytics
2. Lighthouse スコアを確認
3. 目標:
   - Performance: > 75
   - Accessibility: > 85
   - Best Practices: > 90
   - SEO: > 85

---

### 6-4 ログ監視設定

**Render (Backend):**
- Render ダッシュボード → Logs タブを定期確認
- エラーログが出ていないか監視

**Vercel (Frontend):**
- Vercel ダッシュボード → Deployments → Logs
- ビルドエラー・ランタイムエラーを確認

**Supabase (Database):**
- Supabase ダッシュボード → SQL Editor
- 接続状態・クエリログを確認

---

## 🐛 トラブルシューティング

### ❌ Render デプロイ失敗

**症状:** Build failed

**確認:**
```bash
# ローカルでビルド確認
./gradlew clean build -x test
# エラーが出ていないか確認
```

**対策:**
- Render ログを詳しく確認
- `build.gradle` の依存関係を確認
- Java バージョン確認

---

### ❌ API が 503 Service Unavailable

**症状:** バックエンドが応答しない

**確認:**
```bash
curl -I https://calcpay-api.onrender.com/members
# HTTP/1.1 503 Service Unavailable
```

**対策:**
- Render → Logs を確認
- DB 接続テスト: `psql -U postgres -h [HOST]`
- 環境変数が正しいか確認

---

### ❌ CORS エラー

**症状:** フロントエンドからの API 呼び出しが失敗

**ブラウザ Console:**
```
Access to XMLHttpRequest at 'https://calcpay-api.onrender.com/members'
from origin 'https://calcpay-xxx.vercel.app' has been blocked by CORS policy
```

**対策:**
- WebConfig.java の `allowedOrigins` を確認
- FRONTEND_URL 環境変数が正しいか確認
- Render を再デプロイ

---

### ❌ フロントエンド API エンドポイントが 404

**症状:** API が見つからない

**確認:**
```bash
# API のパスを確認
curl https://calcpay-api.onrender.com/members
# vs
curl https://calcpay-api.onrender.com/api/members
```

**対策:**
- `application-prod.yml` の `context-path` を確認
- `src/api.js` のエンドポイント URL を確認
- `VITE_API_BASE_URL` が正しいか確認

---

## 📊 デプロイ完了チェックリスト

- [ ] Supabase PostgreSQL プロジェクト作成完了
- [ ] Render アカウント作成完了
- [ ] Vercel アカウント・GitHub 連携完了
- [ ] application-prod.yml 作成
- [ ] Dockerfile マルチステージ化
- [ ] WebConfig CORS 設定完了
- [ ] ローカルビルド成功
- [ ] Render デプロイ成功
- [ ] Render API 動作確認
- [ ] src/api.js 環境変数切り分け
- [ ] フロントエンド ビルド成功
- [ ] Vercel デプロイ成功
- [ ] Vercel UI 表示確認
- [ ] エンドツーエンドテスト合格
- [ ] ログ監視設定完了

---

## 🎉 デプロイ完了

**本番環境 URL:**

| コンポーネント | URL |
|-------------|-----|
| **Frontend** | `https://calcpay-<random>.vercel.app` |
| **Backend API** | `https://calcpay-api.onrender.com` |
| **Database** | Supabase PostgreSQL (Tokyo Region) |

**利用可都市:**
- 研究室内・外のどこからでもアクセス可能
- スマートフォンからもアクセス可能

---

## 📚 参考資料

- [Supabase PostgreSQL 接続ガイド](https://supabase.com/docs/guides/database/connecting-to-postgres)
- [Render Java デプロイガイド](https://render.com/docs/deploy-java-apps)
- [Vercel Vite デプロイガイド](https://vercel.com/docs/frameworks/vite)
- [Spring Boot 本番環境設定](https://spring.io/projects/spring-boot)

---

**作成者:** GitHub Copilot  
**最終更新:** 2026年4月6日
