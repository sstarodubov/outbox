create table outbox_offsets (
   id int4 primary key,
   last_processed_id int4 not null,
   available_after timestamp default now() not null,
   last_processed_transaction_id xid8 not null,
   "partition" int4 not null,
   topic varchar(128)
);

create table outbox_messages (
	id int4 primary key,
	topic varchar(128) not null,
	"key" varchar(128),
	"type" varchar,
	payload bytea,
	"partition" int4 default 0 not null,
	transaction_id xid8 default pg_current_xact_id() not null
);