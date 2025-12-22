# ProtoSpace ER図
---

## users テーブル
| カラム名               | データ型         | 制約                 |
| ------------------ | ------------ | ------------------ |
| id                 | BIGINT       | PK, AUTO_INCREMENT |
| email              | VARCHAR(255) | NOT NULL, UNIQUE   |
| encrypted_password | VARCHAR(255) | NOT NULL           |
| name               | VARCHAR(255) | NOT NULL           |
| profile            | TEXT         | NOT NULL           |
| occupation         | TEXT         | NOT NULL           |
| position           | TEXT         | NOT NULL           |
| created_at         | TIMESTAMP    | NOT NULL           |
| updated_at         | TIMESTAMP    | NOT NULL           |

### アソシエーション
- User は複数の Prototype を持つ（1 対多）
- User は複数の Comment を持つ（1 対多）
---

## prototypes テーブル
| カラム名       | データ型         | 制約                 |
| ---------- | ------------ | ------------------ |
| id         | BIGINT       | PK, AUTO_INCREMENT |
| title      | VARCHAR(255) | NOT NULL           |
| catch_copy | TEXT         | NOT NULL           |
| concept    | TEXT         | NOT NULL           |
| user_id    | BIGINT       | NOT NULL, FK       |
| created_at | TIMESTAMP    | NOT NULL           |
| updated_at | TIMESTAMP    | NOT NULL           |

### アソシエーション
- 1つのプロトタイプに対して複数のコメントが投稿される（1対多）
---

## images テーブル
| カラム名         | データ型         | 制約                 |
| ------------ | ------------ | ------------------ |
| id           | BIGINT       | PK, AUTO_INCREMENT |
| prototype_id | BIGINT       | NOT NULL, FK       |
| image_name   | VARCHAR(255) | NOT NULL           |
| image_type   | VARCHAR(100) | NOT NULL           |
| image_data   | BYTEA        | NOT NULL           |
| created_at   | TIMESTAMP    | NOT NULL           |

### アソシエーション
- 1つのプロトタイプに対して複数の画像が投稿される（1対多）
---

## comments テーブル
| カラム名         | データ型      | 制約                 |
| ------------ | --------- | ------------------ |
| id           | BIGINT    | PK, AUTO_INCREMENT |
| content      | TEXT      | NOT NULL           |
| prototype_id | BIGINT    | NOT NULL, FK       |
| user_id      | BIGINT    | NOT NULL, FK       |
| created_at   | TIMESTAMP | NOT NULL           |
| updated_at   | TIMESTAMP | NOT NULL           |
---