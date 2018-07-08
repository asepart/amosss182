create table account(
  login_name character varying (32) primary key,
  password text default null,
  first_name character varying (64) not null,
  last_name character varying (64) not null
);

create table admin_account(
  primary key (login_name)
) inherits (account);

create table user_account (
  phone_number character varying (32) not null,
  primary key (login_name)
) inherits (account);

create table project (
  entry_key character varying (32) primary key,
  name character varying (32) not null,
  owner character varying (32) not null references admin_account(login_name) on delete cascade,
  finished boolean not null default 'no'
);

create type ticket_category as enum ('one-time-error', 'trace', 'behavior');
create type ticket_status as enum ('open', 'accepted', 'processed', 'finished');

create table ticket(
  id serial primary key,
  name character varying (128) not null,
  summary text not null,
  description text not null,
  category ticket_category not null,
  status ticket_status not null default 'open',
  required_obversations integer not null check (required_obversations >= 0),
  project_key character varying (32) not null references project(entry_key) on delete cascade
);

create table message(
  id serial primary key,
  sender character varying (32) not null,
  timestamp timestamp not null default current_timestamp,
  content text not null,
  attachment character varying (255) default null,
  ticket_id serial not null references ticket(id) on delete cascade
);

create table membership(
  project_key character varying (32) not null references project(entry_key) on delete cascade,
  login_name character varying (32) not null references user_account(login_name) on delete cascade,
  primary key (project_key, login_name)
);

create table assignment(
  ticket_id serial not null references ticket(id) on delete cascade,
  login_name character varying (32) not null references user_account(login_name) on delete cascade,
  primary key (ticket_id, login_name)
);

create type observation_outcome as enum ('positive', 'negative');

create table observation(
  id serial primary key,
  ticket_id serial not null references ticket(id) on delete cascade,
  login_name character varying (32) not null references user_account(login_name) on delete cascade,
  outcome observation_outcome not null,
  quantity integer not null check (quantity > 0)
);

create table fileinfo(
  id serial primary key,
  internal_name text not null,
  thumbnail_name text default null,
  original_name text not null,
  ticket_id serial references ticket(id) on delete set null
);
