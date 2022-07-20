drop table if exists se_users;
create table se_users
(
    id              serial8 primary key,
    code            varchar(128)  not null,
    tenant_code     varchar(128)  not null default '0',
    username        varchar(128)  not null unique,
    "password"      varchar(2048) not null,
    "enabled"       boolean       not null default true,
    extend          jsonb,
    last_login_time timestamp              default current_timestamp,
    created_time    timestamp              default current_timestamp,
    updated_time    timestamp              default current_timestamp,
    unique (username),
    unique (code)
);
select setval(pg_get_serial_sequence('se_users', 'id'), 100);
CREATE INDEX idx_se_users_extend_gin ON se_users USING gin (extend);
CREATE INDEX idx_se_users_tenant_code_index ON se_users (tenant_code, username);
comment on table se_users is '用户表';

drop table if exists se_authorities;
create table se_authorities
(
    id        serial primary key,
    user_code varchar(128) not null,
    authority varchar(128) not null,
    unique (user_code, authority)
);
comment on table se_authorities is '用户权限表';

drop table if exists se_groups;
create table se_groups
(
    id           serial primary key,
    code         varchar(128) not null,
    tenant_code  varchar(128) not null default '0',
    "name"       varchar(128) not null,
    description  text,
    created_time timestamp             default current_timestamp,
    updated_time timestamp             default current_timestamp,
    unique (code),
    unique (tenant_code, "name")
);
select setval(pg_get_serial_sequence('se_groups', 'id'), 100);
CREATE INDEX idx_se_groups_tenant_code_index ON se_groups (tenant_code, "name");
comment on table se_groups is '角色表';

drop table if exists se_group_authorities;
create table se_group_authorities
(
    id         serial8 primary key,
    group_code varchar(128) not null,
    authority  varchar(128) not null,
    unique (group_code, authority)
);
comment on table se_group_authorities is '角色权限表';

drop table if exists se_group_members;
create table se_group_members
(
    id         serial8 primary key,
    group_code varchar(128) not null,
    user_code  varchar(128) not null,
    unique (group_code, user_code)
);
comment on table se_group_members is '角色用户表';

drop table if exists se_tenants;
create table se_tenants
(
    id           serial primary key,
    code         varchar(128) not null,
    p_code       varchar(128) not null,
    "name"       varchar(128) not null,
    address      text,
    description  text,
    extend       jsonb,
    created_time timestamp default current_timestamp,
    updated_time timestamp default current_timestamp,
    unique (code)
);
select setval(pg_get_serial_sequence('se_tenants', 'id'), 100);
CREATE INDEX idx_se_tenants_extend_gin ON se_tenants USING gin (extend);
comment on table se_tenants is '租户表';

drop table if exists se_tenant_members;
create table se_tenant_members
(
    id          serial8 primary key,
    tenant_code varchar(128) not null,
    user_code   varchar(128) not null,
    "enabled"   boolean      not null default true,
    unique (tenant_code, user_code)
);
comment on table se_tenant_members is '租户用户表';