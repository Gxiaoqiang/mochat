create database mochat;

drop table user_info;
create table user_info (
 user_id varchar(33) not null,
 user_name varchar(64),
 user_pass_word varchar(128),
 user_phone varchar(12),
 user_nick_name varchar(64),
 user_email varchar(128),
 user_sex char(1),
 user_status char(1),   
 activate_time timestamp ,
 create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 upd_time timestamp null
);
drop table chat_room;
create table chat_room(
  room_id varchar(33) not null,
  room_name varchar(64),
  room_desc varchar(128),
  user_id varchar(33),
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  upd_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

drop table question;
create table question(
  question_id varchar(33),
  question_desc varchar(128)
);

drop table user_question;
create table user_question(
  user_mail varchar(128),
  question_id varchar(33),
  question_result varchar(512),
  question_index integer
);