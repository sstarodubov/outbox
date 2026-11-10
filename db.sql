create table outbox_offsets (
   "partition" serial primary key,
   last_processed_id int8 not null,
   available_after timestamp default now() not null,
   last_processed_transaction_id xid8 not null
);

create table outbox_messages (
	id bigserial primary key,
	topic varchar(128) not null,
	"key" varchar(128),
	"type" varchar(128),
	headers jsonb,
	payload bytea,
	"partition" int4 default 0 not null,
	transaction_id xid8 default pg_current_xact_id() not null
);