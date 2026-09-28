Yes. `ON CONFLICT` does not need the primary key to be included in the `INSERT`. PostgreSQL identifies a conflict using the column or constraint specified in the `ON CONFLICT` clause—usually a `UNIQUE` constraint.

```sql
CREATE TABLE users (
    id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email       text NOT NULL UNIQUE,
    name        text NOT NULL,
    updated_at  timestamptz NOT NULL DEFAULT now()
);
```

The `id` column is omitted because PostgreSQL generates it:

```sql
INSERT INTO users (email, name)
VALUES ('alice@example.com', 'Alice')
ON CONFLICT (email)
DO UPDATE SET
    name       = EXCLUDED.name,
    updated_at = now()
RETURNING id, email, name;
```

Here is what happens:

- If `alice@example.com` does not exist, PostgreSQL inserts a new row and generates its `id`.
- If a row already has that email, the `UNIQUE` constraint on `email` detects the conflict.
- PostgreSQL updates that existing row, preserving its existing `id`.
- `EXCLUDED.name` refers to the value from the attempted insert.

For example, if the table contains:

```text
id | email               | name
---+---------------------+------
7  | alice@example.com   | Alicia
```

Running the insert with `name = 'Alice'` updates that row:

```text
id | email               | name
---+---------------------+------
7  | alice@example.com   | Alice
```

The primary key remains `7`.

You can also reference the named constraint explicitly:

```sql
INSERT INTO users (email, name)
VALUES ('alice@example.com', 'Alice')
ON CONFLICT ON CONSTRAINT users_email_key
DO UPDATE SET
    name = EXCLUDED.name;
```

If the table has a multi-column unique constraint, specify all of its columns:

```sql
CREATE TABLE memberships (
    id       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id  bigint NOT NULL,
    group_id bigint NOT NULL,
    role     text NOT NULL,
    UNIQUE (user_id, group_id)
);

INSERT INTO memberships (user_id, group_id, role)
VALUES (10, 3, 'admin')
ON CONFLICT (user_id, group_id)
DO UPDATE SET
    role = EXCLUDED.role;
```

In this case, PostgreSQL identifies the row by the combination of `user_id` and `group_id`, not by the generated primary key.

If you write:

```sql
ON CONFLICT (id)
```

then PostgreSQL can only detect a conflict on `id`. Since `id` is omitted and auto-generated, each insert normally gets a new value, so it will not match an existing row. To perform an upsert without supplying the generated primary key, use another column or column combination with a `UNIQUE` constraint.