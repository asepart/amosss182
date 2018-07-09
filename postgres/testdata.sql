insert into admin_account values ('admin', crypt('admin', gen_salt('bf', 8)), 'Default', 'Admin');
insert into user_account values ('user', crypt('user', gen_salt('bf', 8)), 'Default', 'User', '+4917123456');

insert into project values ('pizza', 'Pizza Project', 'admin');

insert into ticket(name, summary, description, category, status, required_obversations, project_key)
            values ('Developers are hungry', 'There is an insufficient amount of pizza available.',
                    'A developer is a tool which converts pizza into code.',
                    'behavior', 'open', 8, 'pizza');

insert into membership values ('pizza', 'user');

-- these accounts should own nothing/should not be part of any projects etc., useful for test cases
insert into admin_account values ('nobodyadmin', crypt('nobodyadmin', gen_salt('bf', 8)), 'Nobody', 'Admin');
insert into user_account values ('nobodyuser', crypt('nobodyuser', gen_salt('bf', 8)), 'Nobody', 'User', '000');

-- these accounts are created for demo day
insert into user_account values ('david', crypt('david', gen_salt('bf', 8)), 'David', 'Haller', '000');
insert into user_account values ('sebastian', crypt('sebastian', gen_salt('bf', 8)), 'Sebastian', 'Duda', '000');
insert into user_account values ('long', crypt('long', gen_salt('bf', 8)), 'Long', 'Do', '000');
insert into user_account values ('mark', crypt('mark', gen_salt('bf', 8)), 'Mark', 'Rudtke', '000');
insert into user_account values ('dumitru', crypt('dumitru', gen_salt('bf', 8)), 'Dumitru', 'Cotet', '000');
insert into user_account values ('tanja', crypt('tanja', gen_salt('bf', 8)), 'Tanja', 'Batz', '000');
insert into user_account values ('michaela', crypt('michaela', gen_salt('bf', 8)), 'Michaela', 'Macht', '000');
insert into user_account values ('georg', crypt('georg', gen_salt('bf', 8)), 'Georg', 'Schwarz', '000');
