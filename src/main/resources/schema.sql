DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS geoclasses;
DROP TABLE IF EXISTS job;

create table geoclasses
(
    id         bigserial not null
        constraint geoclasses_pkey
            primary key,
    code       varchar(255),
    name       varchar(255),
    section_id varchar(255)
);

create table job
(
    id       bigint not null
        constraint job_pkey
            primary key,
    jobname  varchar(255),
    jobstate varchar(255)
);

create table section
(
    id   serial not null
        constraint section_pkey
            primary key,
    name varchar(255)

);


