drop schema public cascade;

create schema public;

alter schema public owner to iot;

create sequence tbl_tags_tag_id_seq
;

create sequence tbl_employee_employee_id_seq
;

create table tbl_assets
(
	asset_id integer not null
		constraint tbl_asset_pk
			primary key,
	tag_id integer not null,
	type integer not null,
	description varchar(30),
	status varchar(10) not null,
	err_code integer not null,
	update_date timestamp not null,
	create_date timestamp not null,
	assigned_region integer
)
;

create table tbl_connectedregions
(
	connection_id integer not null
		constraint tbl_connectedregions_pk
			primary key,
	first_region integer not null,
	second_region integer not null
)
;

create table tbl_employees
(
	employee_id integer default nextval('tbl_employee_employee_id_seq'::regclass) not null
		constraint tbl_employee_pk
			primary key,
	first_name varchar(15) not null,
	last_name varchar(15) not null,
	tag_id integer not null,
	auth_level integer not null,
	expertise varchar(15),
	create_date timestamp default ('now'::text)::date not null,
	role integer not null
)
;

create table tbl_hardwaremessage
(
	record_id bigint not null
		constraint tbl_hardwaremessage_pk
			primary key,
	date timestamp not null,
	dc integer not null,
	tag_id integer not null,
	sqi integer not null,
	rn_id integer not null,
	rssi integer not null,
	data_count bigint not null,
	ranges integer not null,
	x integer not null,
	y integer not null,
	data_type varchar(6) not null,
	power integer not null,
	deviation numeric(5,2) not null,
	state integer not null,
	info_id bigint not null,
	packet_id bigint not null,
	temp_alarm boolean,
	motion_alarm boolean
)
;

create table tbl_locations
(
	tag_id integer not null,
	date timestamp not null,
	x double precision not null,
	y double precision not null,
	data_count bigint not null,
	height integer,
	width integer,
	gps_latitude numeric(7,5),
	gps_longitude numeric(7,5),
	match_id bigint not null,
	subregion_id integer not null,
	gvalue numeric(7,3),
	speed numeric(7,3),
	rn_id integer not null,
	deleted boolean default false,
	temp_alarm boolean,
	motion_alarm boolean,
	panic boolean,
	constraint tbl_location_pk
		primary key (tag_id, date, rn_id)
)
;

create table tbl_readers
(
	rn_id integer not null
		constraint tbl_reader_pk
			primary key,
	x double precision not null,
	y double precision not null,
	range integer not null,
	subregion_id integer not null,
	type integer not null,
	battery integer not null,
	backup_battery integer not null,
	create_date timestamp not null,
	update_date timestamp not null
)
;

alter table tbl_hardwaremessage
	add constraint tbl_hardwaremessage_tbl_reader
		foreign key (rn_id) references tbl_readers
;

alter table tbl_locations
	add constraint tbl_location_tbl_reader
		foreign key (rn_id) references tbl_readers
;

create table tbl_regions
(
	region_id integer not null
		constraint tbl_region_pk
			primary key,
	description varchar(50),
	x double precision not null,
	y double precision not null,
	width integer not null,
	length integer not null,
	type integer not null,
	create_date timestamp not null,
	height integer
)
;

alter table tbl_assets
	add constraint tbl_assets_tbl_regions
		foreign key (assigned_region) references tbl_regions
;

alter table tbl_connectedregions
	add constraint tbl_connectedregions_tbl_region_1
		foreign key (first_region) references tbl_regions
;

alter table tbl_connectedregions
	add constraint tbl_connectedregions_tbl_region_2
		foreign key (second_region) references tbl_regions
;

alter table tbl_locations
	add constraint tbl_location_tbl_region
		foreign key (subregion_id) references tbl_regions
;

create table tbl_sensorreadings
(
	rn_id integer not null
		constraint tbl_sensorreading_tbl_reader
			references tbl_readers,
	oxygen numeric(4,2) not null,
	carbonmonoxide numeric(4,2) not null,
	methane numeric(4,2) not null,
	packet_id bigint not null,
	date timestamp not null,
	temperature numeric(4,2),
	humidity numeric(4,2),
	deleted boolean default false,
	subregion_id integer
		constraint tbl_sensorreading_region
			references tbl_regions,
	constraint tbl_sensorreading_pk
		primary key (rn_id, date)
)
;

create table tbl_tags
(
	tag_id integer default nextval('tbl_tags_tag_id_seq'::regclass) not null
		constraint tbl_tags_pk
			primary key,
	type integer not null,
	battery integer not null,
	create_date timestamp default now() not null
)
;

alter table tbl_assets
	add constraint tbl_tags_tbl_asset
		foreign key (tag_id) references tbl_tags
;

alter table tbl_employees
	add constraint tbl_tags_tbl_employee
		foreign key (tag_id) references tbl_tags
;

alter table tbl_hardwaremessage
	add constraint tbl_hardwaremessage_tbl_tags
		foreign key (tag_id) references tbl_tags
;

alter table tbl_locations
	add constraint tbl_location_tbl_tags
		foreign key (tag_id) references tbl_tags
;

create table tbl_vehicles
(
	vehicle_id integer not null
		constraint tbl_vehicle_pk
			primary key,
	width integer not null,
	height integer not null,
	type integer not null,
	tag_id integer not null
		constraint tbl_tags_tbl_vehicle
			references tbl_tags,
	model varchar(50)
)
;


CREATE MATERIALIZED VIEW tbl_tagreadings AS SELECT s1.tag_id,
    s1.date,
    s1.x,
    s1.y,
    s1.subregion_id,
    s1.rn_id,
    s1.temp_alarm,
    s1.motion_alarm,
    s1.panic
   FROM (tbl_locations s1
     JOIN ( SELECT tbl_locations.tag_id,
            max(tbl_locations.date) AS mts
           FROM tbl_locations
          GROUP BY tbl_locations.tag_id) s2 ON (((s2.tag_id = s1.tag_id) AND (s1.date = s2.mts))))
;

CREATE UNIQUE INDEX tag_id_last_seen ON tbl_tagreadings(tag_id);


REFRESH MATERIALIZED VIEW tbl_tagreadings;

CREATE MATERIALIZED VIEW tbl_lastsensorreadings AS SELECT s1.rn_id,
    s1.oxygen,
    s1.carbonmonoxide,
    s1.temperature,
    s1.humidity,
    s1.methane,
    s1.subregion_id
   FROM (tbl_sensorreadings s1
     JOIN ( SELECT tbl_sensorreadings.rn_id,
            max(tbl_sensorreadings.date) AS mts
           FROM tbl_sensorreadings
          GROUP BY tbl_sensorreadings.rn_id) s2 ON (((s2.rn_id = s1.rn_id) AND (s1.date = s2.mts))))
;


CREATE UNIQUE INDEX region_id_last_seen ON tbl_lastsensorreadings(subregion_id);


REFRESH MATERIALIZED VIEW tbl_lastsensorreadings;


grant ALL on all tables in schema public to iot;
