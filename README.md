# ProtoSpace ER図
---

## users テーブル
| カラム名               | データ型         | 制約                 |
| ------------------ | ------------ | ------------------ |
| id                 | INTEGER      | PK                 |
| email              | VARCHAR(255) | NOT NULL, UNIQUE   |
| encrypted_password | VARCHAR(255) | NOT NULL           |
| name               | VARCHAR(255) | NOT NULL           |
| profile            | TEXT         | NOT NULL           |
| occupation         | TEXT         | NOT NULL           |
| position           | TEXT         | NOT NULL           |

### アソシエーション
- @OneToMany Prototype
- @OneToMany Comment
---

## prototypes テーブル
| カラム名       | データ型         | 制約                 |
| ---------- | ------------ | ------------------ |
| id         | INTEGER      | PK                 |
| title      | VARCHAR(255) | NOT NULL           |
| catch_copy | TEXT         | NOT NULL           |
| concept    | TEXT         | NOT NULL           |
| user_id    | INTEGER      | NOT NULL, FK       |

### アソシエーション
- @ManyToOne User
- @OneToMany Comment
---

## comments テーブル
| カラム名         | データ型      | 制約                 |
| ------------ | ----------- | ------------------ |
| id           | INTEGER     | PK                 |
| content      | TEXT        | NOT NULL           |
| prototype_id | INTEGER     | NOT NULL, FK       |
| user_id      | INTEGER     | NOT NULL, FK       |
| image_name   | VARCHAR(255) | NOT NULL          |
| image_type   | VARCHAR(100) | NOT NULL          |
| image_data   | BYTEA        | NOT NULL          |

### アソシエーション
- @ManyToOne User
- @ManyToOne Prototype
---