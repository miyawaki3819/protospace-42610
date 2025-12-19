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

### 外部キー
FOREIGN KEY (user_id) REFERENCES users(id)
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

### 外部キー
FOREIGN KEY (prototype_id) REFERENCES prototypes(id)
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

### 外部キー
FOREIGN KEY (prototype_id) REFERENCES prototypes(id)
FOREIGN KEY (user_id) REFERENCES users(id)
---

## リレーションまとめ（ER構造）
users 1 ── * prototypes
users 1 ── * comments
prototypes 1 ── * comments
prototypes 1 ── * images
---

## Java / JPA エンティティ対応（参考）
| エンティティ    | 関連                                              |
| --------- | ----------------------------------------------- |
| User      | @OneToMany prototypes / comments                |
| Prototype | @ManyToOne user<br>@OneToMany images / comments |
| Image     | @ManyToOne prototype                            |
| Comment   | @ManyToOne user / prototype                     |
---