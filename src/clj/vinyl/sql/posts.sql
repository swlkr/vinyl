-- name: get-posts
-- Gets a list of all posts made by a user
select *
from posts
where is_draft = 0

-- name: get-drafts
select *
from posts
where is_draft = 1

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
  user_id,
  is_draft
) values (
  :title,
  :content,
  :cover_image_url,
  :user_id,
  :is_draft
)

-- name: delete-post<!
-- Deletes a post
delete from posts
where id = :id

-- name: update-post<!
-- Updates a post
update posts
set title = coalesce(:title, title),
    content = coalesce(:content, content),
    cover_image_url = coalesce(:cover_image_url, cover_image_url),
    is_draft = coalesce(:is_draft, is_draft)
where id = :id
