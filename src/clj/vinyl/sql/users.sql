-- name: get-users
-- Gets a list of all users with limit/offset
select *
from users
offset :offset
limit :limit

-- name: get-users-by-id
-- Gets a user by an id
select *
from users
where id = :id
offset 0
limit 1

-- name: get-users-by-email
-- Gets a user by email
select *
from users
where email = :email
offset 0
limit 1

-- name: insert-user<!
-- Creates a user
insert into users (
  email,
  password
) values (
  :email,
  :password
)

-- name: delete-user<!
-- Deletes a user
delete from users
where id = :id

-- name: update-user<!
-- Updates a user
update users
set email = coalesce(:email, email),
    password = coalesce(:password, password)
where id = :id
