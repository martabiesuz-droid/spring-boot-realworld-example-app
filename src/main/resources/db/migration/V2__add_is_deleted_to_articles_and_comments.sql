alter table articles add column is_deleted boolean not null default 0;
alter table comments add column is_deleted boolean not null default 0;
