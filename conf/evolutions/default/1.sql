# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "eveats_account_status" ("id" BIGINT NOT NULL PRIMARY KEY,"paid_until" TIMESTAMP NOT NULL,"created_at" TIMESTAMP NOT NULL,"logon_count" INTEGER NOT NULL,"logon_duration" BIGINT NOT NULL,"cached_until" TIMESTAMP NOT NULL);
create table "eveats_apikey_info" ("id" BIGINT NOT NULL PRIMARY KEY,"access_mask" INTEGER NOT NULL,"key_type" VARCHAR(254) NOT NULL,"expires" TIMESTAMP,"cached_until" TIMESTAMP NOT NULL);
create table "eveats_apikeys" ("id" BIGINT NOT NULL PRIMARY KEY,"vcode" VARCHAR(254) NOT NULL);
create table "eveats_character" ("id" BIGINT NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"corporation_id" BIGINT NOT NULL,"cached_until" TIMESTAMP NOT NULL);
create table "eveats_character_asset_item" ("id" BIGINT NOT NULL,"asset_list_id" BIGINT NOT NULL,"parent_id" BIGINT,"location_id" BIGINT,"type_id" BIGINT NOT NULL,"quantity" INTEGER NOT NULL,"flag" INTEGER NOT NULL,"singleton" BOOLEAN NOT NULL,"raw_quantity" INTEGER);
alter table "eveats_character_asset_item" add constraint "eveats_character_asset_item_id_asset_list_pk" primary key("id","asset_list_id");
create unique index "eveats_character_asset_item_id_idx" on "eveats_character_asset_item" ("id");
create table "eveats_character_asset_list" ("id" BIGSERIAL NOT NULL,"affiliation_id" BIGINT NOT NULL,"created_at" TIMESTAMP NOT NULL,"cached_until" TIMESTAMP NOT NULL);
alter table "eveats_character_asset_list" add constraint "eveats_character_asset_list_id_affiliation_id_pk" primary key("id","affiliation_id");
create unique index "eveats_character_asset_list_id_idx" on "eveats_character_asset_list" ("id");
create table "eveats_character_location" ("asset_item_id" BIGINT NOT NULL,"asset_list_id" BIGINT NOT NULL,"item_name" VARCHAR(254) NOT NULL,"x" DOUBLE PRECISION NOT NULL,"y" DOUBLE PRECISION NOT NULL,"z" DOUBLE PRECISION NOT NULL,"cached_until" TIMESTAMP NOT NULL);
alter table "eveats_character_location" add constraint "eveats_character_location_id_asset_list_id_pk" primary key("asset_item_id","asset_list_id");
create table "eveats_characters_to_apikeys" ("character_id" BIGINT NOT NULL,"apikey_id" BIGINT NOT NULL);
alter table "eveats_characters_to_apikeys" add constraint "character_apikey_pk" primary key("character_id","apikey_id");
create table "eveats_corporation" ("id" BIGINT NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL);
create table "eveats_corporation_asset_item" ("id" BIGINT NOT NULL,"asset_list_id" BIGINT NOT NULL,"parent_id" BIGINT,"location_id" BIGINT,"type_id" BIGINT NOT NULL,"quantity" INTEGER NOT NULL,"flag" INTEGER NOT NULL,"singleton" BOOLEAN NOT NULL,"raw_quantity" INTEGER);
alter table "eveats_corporation_asset_item" add constraint "eveats_corporation_asset_item_id_asset_list_pk" primary key("id","asset_list_id");
create unique index "eveats_corporation_asset_item_id_idx" on "eveats_corporation_asset_item" ("id");
create table "eveats_corporation_asset_list" ("id" BIGSERIAL NOT NULL,"affiliation_id" BIGINT NOT NULL,"created_at" TIMESTAMP NOT NULL,"cached_until" TIMESTAMP NOT NULL);
alter table "eveats_corporation_asset_list" add constraint "eveats_corporation_asset_list_id_affiliation_id_pk" primary key("id","affiliation_id");
create unique index "eveats_corporation_asset_list_id_idx" on "eveats_corporation_asset_list" ("id");
create table "eveats_corporation_location" ("asset_item_id" BIGINT NOT NULL,"asset_list_id" BIGINT NOT NULL,"item_name" VARCHAR(254) NOT NULL,"x" DOUBLE PRECISION NOT NULL,"y" DOUBLE PRECISION NOT NULL,"z" DOUBLE PRECISION NOT NULL,"cached_until" TIMESTAMP NOT NULL);
alter table "eveats_corporation_location" add constraint "eveats_corporation_location_id_asset_list_id_pk" primary key("asset_item_id","asset_list_id");
create table "eveats_users" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"email" VARCHAR(254) NOT NULL,"password" VARCHAR(254) NOT NULL);
create unique index "email_idx" on "eveats_users" ("email");
create table "eveats_users_to_apikeys" ("user_id" BIGINT NOT NULL,"apikey_id" BIGINT NOT NULL);
alter table "eveats_users_to_apikeys" add constraint "user_apikey_pk" primary key("user_id","apikey_id");
alter table "eveats_account_status" add constraint "apikey_fk" foreign key("id") references "eveats_apikeys"("id") on update CASCADE on delete CASCADE;
alter table "eveats_apikey_info" add constraint "apikey_fk" foreign key("id") references "eveats_apikeys"("id") on update CASCADE on delete CASCADE;
alter table "eveats_character" add constraint "corporation_fk" foreign key("corporation_id") references "eveats_corporation"("id") on update NO ACTION on delete NO ACTION;
alter table "eveats_character_asset_item" add constraint "eveats_character_asset_item_asset_list_fk" foreign key("asset_list_id") references "eveats_character_asset_list"("id") on update CASCADE on delete CASCADE;
alter table "eveats_character_asset_list" add constraint "character_affiliation_fk" foreign key("affiliation_id") references "eveats_character"("id") on update CASCADE on delete CASCADE;
alter table "eveats_character_location" add constraint "asset_item_fk" foreign key("asset_item_id") references "eveats_character_asset_item"("id") on update CASCADE on delete CASCADE;
alter table "eveats_character_location" add constraint "asset_list_fk" foreign key("asset_list_id") references "eveats_character_asset_list"("id") on update CASCADE on delete CASCADE;
alter table "eveats_characters_to_apikeys" add constraint "apikey_fk" foreign key("apikey_id") references "eveats_apikeys"("id") on update CASCADE on delete CASCADE;
alter table "eveats_characters_to_apikeys" add constraint "character_fk" foreign key("character_id") references "eveats_character"("id") on update CASCADE on delete CASCADE;
alter table "eveats_corporation_asset_item" add constraint "eveats_corporation_asset_item_asset_list_fk" foreign key("asset_list_id") references "eveats_corporation_asset_list"("id") on update CASCADE on delete CASCADE;
alter table "eveats_corporation_asset_list" add constraint "corporation_affiliation_fk" foreign key("affiliation_id") references "eveats_corporation"("id") on update CASCADE on delete CASCADE;
alter table "eveats_corporation_location" add constraint "asset_item_fk" foreign key("asset_item_id") references "eveats_corporation_asset_item"("id") on update CASCADE on delete CASCADE;
alter table "eveats_corporation_location" add constraint "asset_list_fk" foreign key("asset_list_id") references "eveats_corporation_asset_list"("id") on update CASCADE on delete CASCADE;
alter table "eveats_users_to_apikeys" add constraint "apikey_fk" foreign key("apikey_id") references "eveats_apikeys"("id") on update CASCADE on delete CASCADE;
alter table "eveats_users_to_apikeys" add constraint "user_fk" foreign key("user_id") references "eveats_users"("id") on update CASCADE on delete CASCADE;

# --- !Downs

alter table "eveats_users_to_apikeys" drop constraint "apikey_fk";
alter table "eveats_users_to_apikeys" drop constraint "user_fk";
alter table "eveats_corporation_location" drop constraint "asset_item_fk";
alter table "eveats_corporation_location" drop constraint "asset_list_fk";
alter table "eveats_corporation_asset_list" drop constraint "corporation_affiliation_fk";
alter table "eveats_corporation_asset_item" drop constraint "eveats_corporation_asset_item_asset_list_fk";
alter table "eveats_characters_to_apikeys" drop constraint "apikey_fk";
alter table "eveats_characters_to_apikeys" drop constraint "character_fk";
alter table "eveats_character_location" drop constraint "asset_item_fk";
alter table "eveats_character_location" drop constraint "asset_list_fk";
alter table "eveats_character_asset_list" drop constraint "character_affiliation_fk";
alter table "eveats_character_asset_item" drop constraint "eveats_character_asset_item_asset_list_fk";
alter table "eveats_character" drop constraint "corporation_fk";
alter table "eveats_apikey_info" drop constraint "apikey_fk";
alter table "eveats_account_status" drop constraint "apikey_fk";
alter table "eveats_users_to_apikeys" drop constraint "user_apikey_pk";
drop table "eveats_users_to_apikeys";
drop table "eveats_users";
alter table "eveats_corporation_location" drop constraint "eveats_corporation_location_id_asset_list_id_pk";
drop table "eveats_corporation_location";
alter table "eveats_corporation_asset_list" drop constraint "eveats_corporation_asset_list_id_affiliation_id_pk";
drop table "eveats_corporation_asset_list";
alter table "eveats_corporation_asset_item" drop constraint "eveats_corporation_asset_item_id_asset_list_pk";
drop table "eveats_corporation_asset_item";
drop table "eveats_corporation";
alter table "eveats_characters_to_apikeys" drop constraint "character_apikey_pk";
drop table "eveats_characters_to_apikeys";
alter table "eveats_character_location" drop constraint "eveats_character_location_id_asset_list_id_pk";
drop table "eveats_character_location";
alter table "eveats_character_asset_list" drop constraint "eveats_character_asset_list_id_affiliation_id_pk";
drop table "eveats_character_asset_list";
alter table "eveats_character_asset_item" drop constraint "eveats_character_asset_item_id_asset_list_pk";
drop table "eveats_character_asset_item";
drop table "eveats_character";
drop table "eveats_apikeys";
drop table "eveats_apikey_info";
drop table "eveats_account_status";

