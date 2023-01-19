create table idempotency_key
(
    id            uuid      not null,
    creation_time timestamp not null,
    primary key (id)
);
create table payment_type
(
    id int8 not null,
    primary key (id)
);
create table rate_code
(
    id int8 not null,
    primary key (id)
);
create table taxi_trip
(
    id                     uuid not null,
    dolocationid           int8,
    pulocationid           int8,
    tpep_dropoff_datetime  timestamp,
    dropoff_day            int2,
    dropoff_month          int2,
    dropoff_year           int4,
    extra                  numeric(19, 2),
    fare_amount            numeric(19, 2),
    improvement_surcharge  numeric(19, 2),
    mta_tax                numeric(19, 2),
    passenger_count        int8,
    tpep_pick_up_datetime  timestamp,
    store_and_forward_flag varchar(255),
    tip_amount             numeric(19, 2),
    tolls_amount           numeric(19, 2),
    total_amount           numeric(19, 2),
    trip_distance          numeric(19, 2),
    payment_type_id        int8,
    rate_code_id           int8,
    vendor_id              int8,
    primary key (id)
);
create table vendor
(
    id int8 not null,
    primary key (id)
);
alter table taxi_trip
    add constraint FK80f4q97iaa4obmtsncambvhqk foreign key (payment_type_id) references payment_type;
alter table taxi_trip
    add constraint FK50ahf46o1897phun8b70dx7v foreign key (rate_code_id) references rate_code;
alter table taxi_trip
    add constraint FK7tovredm0nedial6n1kcrk6je foreign key (vendor_id) references vendor;
