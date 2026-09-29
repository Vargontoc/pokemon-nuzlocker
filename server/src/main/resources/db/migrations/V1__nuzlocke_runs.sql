create table run_nuzlocke (
    id                  bigint primary key,
    game_id             varchar(100) not null,
    status              varchar(20)  not null,
    starter_choosen     varchar(50),
    current_step_order  int          not null,
    current_location_id varchar(100),
    rules               jsonb        not null,
    badges              jsonb        not null,
    flags               jsonb        not null,
    defeated_trainers   jsonb        not null,
    unlocks             jsonb        not null,
    created_at          timestamp(6) with time zone not null,
    updates_at          timestamp(6),
    version             bigint
);

create table run_pokemon (
    id              bigint primary key,
    run_id          bigint       not null references run_nuzlocke (id) on delete cascade,
    personality     bigint,
    specie          varchar(50)  not null,
    nickname        varchar(30),
    level           int          not null,
    status          varchar(10)  not null,
    is_shiny           boolean      not null,
    location_id     varchar(100),
    caught_at_step  int          not null,
    cause_of_death  varchar(200),
    created_at          timestamp(6) with time zone not null,
    updates_at          timestamp(6)
);
create index idx_run_pokemon_run on run_pokemon (run_id);
create unique index ux_run_pokemon_personality on run_pokemon (run_id, personality) where personality is not null;

alter table run_pokemon alter column personality type varchar(64) using personality::text;
alter table run_pokemon rename column personality to identity_key;
alter index ux_run_pokemon_personality rename to ux_run_pokemon_identity;

create table run_encounter (
    id             bigint primary key,
    run_id         bigint         not null references run_nuzlocke (id) on delete cascade,
    location_id    varchar(100) not null,
    specie         varchar(50)  not null,
    is_shiny          boolean      not null,
    outcome        varchar(20)  not null,
    consumes_zone  boolean      not null,
    step_order     int          not null,
    created_at          timestamp(6) with time zone not null,
    updates_at          timestamp(6)
);
create index idx_run_encounter_run on run_encounter (run_id);
