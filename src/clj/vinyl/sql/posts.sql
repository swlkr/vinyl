-- name: get-posts
-- Gets a list of all posts made by a user
select *
from posts

-- name: get-posts-by-id
-- Gets a post by an id
select *
from posts
where id = :id
offset 0
limit 1

-- name: insert-post<!
-- Creates a post
insert into posts (
  title,
  content,
  cover_image_url,
  user_id
) values (
  :title,
  :content,
  :cover_image_url,
  :user_id
)

-- name: delete-post<!
-- Deletes a post
delete from posts
where id = :id

-- name: update-post<!
-- Updates a post
update posts
set title = coalesce(:title, title),
    content = coalesce(:content, content)
    cover_image_url = coalesce(:cover_image_url, cover_image_url)
where id = :id
