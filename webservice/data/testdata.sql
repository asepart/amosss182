insert into admin_account values ('admin', crypt('admin', gen_salt('bf', 8)), 'Default', 'Admin');
insert into user_account values ('user', crypt('user', gen_salt('bf', 8)), 'Default', 'User', '+4917123456');

insert into project values ('pizza', 'Pizza Project', 'admin');

insert into ticket(name, summary, description, category, status, required_obversations, project_key)
            values ('Developers are hungry', 'There is an insufficient amount of pizza available.',
                    'A developer is a tool which converts pizza into code.',
                    'behavior', 'open', 8, 'pizza');

insert into membership values ('pizza', 'user');
