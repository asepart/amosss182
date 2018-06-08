create table accounts(
  login_name character varying (32) primary key,
  password text not null,
  first_name character varying (64) not null,
  last_name character varying (64) not null
);

create table admins(
  primary key (login_name)
) inherits (accounts);

create table users (
  phone_number character varying (32) not null,
  primary key (login_name)
) inherits (accounts);

create table projects (
  entry_key character varying (32) primary key,
  name character varying (32) not null,
  owner character varying (32) not null references admins(login_name)
);

create type ticket_category as enum ('one-time-error', 'trace', 'behavior');
create type ticket_status as enum ('open', 'accepted', 'processed', 'finished');

create table tickets(
  id serial primary key,
  name character varying (128) not null,
  summary text not null,
  description text not null,
  category ticket_category not null,
  status ticket_status not null,
  required_obversations integer not null check (required_obversations >= 0),
  project_key character varying (32) not null references projects(entry_key)
);

create table message(
  id serial primary key,
  sender character varying (32) not null references accounts(login_name),
  content text not null,
  ticket_id serial not null references tickets(id)
);
