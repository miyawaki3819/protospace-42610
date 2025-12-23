# ProtoSpace ER図
---

## users テーブル
| カラム名               | データ型         | 制約                 |
| ------------------ | ------------ | ------------------ |
| id                 | SERIAL       | PK                 |
| email              | VARCHAR(255) | NOT NULL, UNIQUE   |
| encrypted_password | VARCHAR(255) | NOT NULL           |
| name               | VARCHAR(255) | NOT NULL           |
| profile            | TEXT         | NOT NULL           |
| occupation         | TEXT         | NOT NULL           |
| position           | TEXT         | NOT NULL           |

### アソシエーション
- @OneToMany Prototypes
- @OneToMany Comments
---

## prototypes テーブル
| カラム名       | データ型         | 制約                 |
| ---------- | ------------ | ------------------ |
| id         | SERIAL       | PK                 |
| title      | VARCHAR(255) | NOT NULL           |
| catch_copy | TEXT         | NOT NULL           |
| concept    | TEXT         | NOT NULL           |
| user_id    | INTEGER      | NOT NULL, FK       |
| image_name | VARCHAR(255) | NOT NULL           |
| image_type | VARCHAR(100) | NOT NULL           |
| image_data | BYTEA        | NOT NULL           |

### アソシエーション
- @ManyToOne User
- @OneToMany Comments
---

## comments テーブル
| カラム名         | データ型      | 制約                 |
| ------------ | ----------- | ------------------ |
| id           | SERIAL      | PK                 |
| content      | TEXT        | NOT NULL           |
| prototype_id | INTEGER     | NOT NULL, FK       |
| user_id      | INTEGER     | NOT NULL, FK       |

### アソシエーション
- @ManyToOne User
- @ManyToOne Prototype
---