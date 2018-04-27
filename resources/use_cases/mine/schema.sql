CREATE USER iot WITH PASSWORD 'iot';

GRANT ALL ON DATABASE smart_mine TO iot;

drop schema public cascade;

create schema public;

alter schema public owner to iot;

create sequence tbl_employee_employee_id_seq
;

create table tbl_assets
(
	asset_id integer not null
		constraint tbl_asset_pk
			primary key,
	tag_id integer not null,
	type integer not null,
	create_date timestamp default ('now'::text)::date not null,
	assigned_region integer
)
;

create table tbl_employees
(
	employee_id integer default nextval('tbl_employee_employee_id_seq'::regclass) not null
		constraint tbl_employee_pk
			primary key,
	tag_id integer not null,
	auth_level integer not null,
	create_date timestamp default ('now'::text)::date not null,
	role integer not null
)
;


create table tbl_locations
(
	tag_id integer not null,
	create_date timestamp default ('now'::text)::date not null,
    rn_id integer not null,
	subregion_id integer not null,
	temp_alarm boolean,
	motion_alarm boolean,
	panic boolean,
	constraint tbl_location_pk
		primary key (tag_id, create_date, rn_id)
)
;

create table tbl_readers
(
	rn_id integer not null
		constraint tbl_reader_pk
			primary key,
	subregion_id integer not null,
	type integer not null,
	battery integer not null,
	create_date timestamp default ('now'::text)::date not null
)
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
	type integer not null,
	create_date timestamp default ('now'::text)::date not null
)
;

create table tbl_vehicles
(
    vehicle_id integer not null
        constraint tbl_vehicle_pk
            primary key,
    type integer not null,
    tag_id integer not null
)
;


alter table tbl_assets
	add constraint tbl_assets_tbl_regions
		foreign key (assigned_region) references tbl_regions
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
	create_date timestamp default ('now'::text)::date not null,
	temperature numeric(4,2),
	humidity numeric(4,2),
	subregion_id integer
		constraint tbl_sensorreading_region
			references tbl_regions,
	constraint tbl_sensorreading_pk
		primary key (rn_id, create_date)
)
;



CREATE MATERIALIZED VIEW tbl_tagreadings AS SELECT s1.tag_id,
    s1.create_date,
    s1.subregion_id,
    s1.rn_id,
    s1.temp_alarm,
    s1.motion_alarm,
    s1.panic
   FROM (tbl_locations s1
     JOIN ( SELECT tbl_locations.tag_id,
            max(tbl_locations.create_date) AS mts
           FROM tbl_locations
          GROUP BY tbl_locations.tag_id) s2 ON (((s2.tag_id = s1.tag_id) AND (s1.create_date = s2.mts))))
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
            max(tbl_sensorreadings.create_date) AS mts
           FROM tbl_sensorreadings
          GROUP BY tbl_sensorreadings.rn_id) s2 ON (((s2.rn_id = s1.rn_id) AND (s1.create_date = s2.mts))))
;


CREATE UNIQUE INDEX region_id_last_seen ON tbl_lastsensorreadings(subregion_id);


REFRESH MATERIALIZED VIEW tbl_lastsensorreadings;


grant ALL on all tables in schema public to iot;
