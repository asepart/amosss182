create table asepart_accounts (login_name varchar(255) not null,
                               first_name varchar(255),
                               last_name varchar(255),
                               password_hash varchar(255) not null,
                               primary key (login_name))

create table asepart_admins (admin_id integer not null,
                             login_name varchar(255),
                             primary key (admin_id))

create table asepart_users (user_id integer not null,
                            phone varchar(255) not null,
                            login_name varchar(255),
                            primary key (user_id))

create table asepart_projects (project_name varchar(255) not null,
                               entry_key varchar(255) not null,
                               primary key (project_name))

alter table asepart_admins add constraint admin_belongs_to_account foreign key (login_name) references asepart_accounts
alter table asepart_users add constraint user_belongs_to_account foreign key (login_name) references asepart_accounts
