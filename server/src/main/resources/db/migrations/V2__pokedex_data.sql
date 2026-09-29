-- Respuestas crudas de PokeAPI: cada URL se descarga una sola vez.
create table pokeapi_cache (
    path        varchar(200) primary key,
    body        jsonb        not null,
    fetched_at  timestamp(6) with time zone not null
);

create table pokedex_meta (
    id             int primary key,
    generation     int         not null,
    version_group  varchar(50) not null
);

create table pokedex_ability (
    id              bigint          primary key,
    slug            varchar(50)  not null unique,
    name            varchar(50)  not null,
    description     varchar(1000),
    created_at          timestamp(6) with time zone not null,
    updated_at          timestamp(6)
);


create table pokedex_move (
    id              bigint          primary key,
    slug            varchar(50)  not null unique,
    name         varchar(50)  not null,
    description  varchar(1000),
    type            varchar(20)  not null,
    power           int,
    accuracy        int,
    pp              int,
    effect_chance   int,
    category        varchar(10)  not null,
    priority        int          not null,
    created_at          timestamp(6) with time zone not null,
    updated_at          timestamp(6)
);

create table pokedex_specie (
    id          bigint          primary key,
    slug        varchar(50)  not null unique,
    name        varchar(50)  not null,
    types       jsonb        not null,
    stats       jsonb        not null,
    abilities   jsonb        not null,
    family      varchar(50)  not null,
    learnset    jsonb        not null,
    created_at          timestamp(6) with time zone not null,
    updated_at          timestamp(6)
);





