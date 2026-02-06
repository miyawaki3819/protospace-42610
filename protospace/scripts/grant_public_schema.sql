-- PostgreSQL 15 以降では public スキーマに CREATE 権限がデフォルトでないため、
-- アプリ用ユーザーに権限を付与する。
--
-- 実行方法（プロジェクト直下の protospace フォルダで）:
--   psql -U postgres -d protospace -f scripts/grant_public_schema.sql
--
-- または psql に接続してから:
--   \c protospace
--   GRANT ALL ON SCHEMA public TO my_user;
--   GRANT CREATE ON SCHEMA public TO my_user;

GRANT ALL ON SCHEMA public TO my_user;
GRANT CREATE ON SCHEMA public TO my_user;
