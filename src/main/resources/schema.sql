DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS geoclasses;
-- DROP TABLE IF EXISTS job;

create table section
(
    id       serial not null
        constraint section_pkey
            primary key,
    name     varchar(255),
    jobid    integer,
    jobstate varchar(255)

);

-- alter table section owner to postgres;

create table if not exists geoclasses
(
    id         bigserial not null
        constraint geoclasses_pkey
            primary key,
    name       varchar(255),
    code       varchar(255),
    section_id varchar(255)
);

-- alter table geoclasses owner to postgres;


