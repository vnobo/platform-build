--drop table if exists se_users;
create table se_users
(
    id              serial8 primary key,
    tenant_id       integer       not null default 0,
    tenant_code     varchar(64)   not null default '0',
    username        varchar(128)  not null unique,
    "password"      varchar(1024) not null,
    "enabled"       boolean       not null default true,
    "name"          varchar(20),
    id_card         varchar(30),
    email           varchar(128),
    phone           varchar(30),
    extend          jsonb,
    last_login_time timestamp              default current_timestamp,
    created_time    timestamp              default current_timestamp,
    updated_time    timestamp              default current_timestamp,
    unique (username),
    unique (id_card)
);
select setval(pg_get_serial_sequence('se_users', 'id'), 1000);
CREATE INDEX idx_se_users_extend_gin ON se_users USING gin (extend);
CREATE INDEX idx_se_users_tenant_id_code_index ON se_users (tenant_id, tenant_code);
comment
on column se_users.username is '用户登录名,系统认证唯一标识';
comment
on column se_users.name is '用户真实姓名';
comment
on column se_users.id_card is '用户身份证ID';
comment
on column se_users.email is '用户登录Email,系统认证唯一标识';
comment
on column se_users.phone is '用户登录电话,系统认证唯一标识';
comment
on column se_users.password is '用户密码';
comment
on column se_users.enabled is '是否启用';
comment
on column se_users.last_login_time is '最后登录时间';
comment
on table se_users is '认证用户基础信息表.';

--drop table if exists se_authorities;
create table se_authorities
(
    id           serial primary key,
    user_id      bigint       not null,
    authority    varchar(128) not null,
    "system"     varchar(20)  not null default 'country',
    created_time timestamp             default current_timestamp,
    updated_time timestamp             default current_timestamp,
    unique (user_id, authority, "system")
);
comment
on column se_authorities.system is '区分每个系统不同的权限';
comment
on column se_authorities.user_id is '登录用户ID';
comment
on column se_authorities.authority is '权限';
comment
on table se_authorities is '权限认证表.';

--drop table if exists se_groups;
create table se_groups
(
    id           serial primary key,
    tenant_id    int          not null default 0,
    tenant_code  varchar(32)  not null default '0',
    "name"       varchar(128) not null,
    "type"       int          not null default 0,
    "system"     varchar(20)  not null default 'country',
    description  text,
    created_time timestamp             default current_timestamp,
    updated_time timestamp             default current_timestamp,
    unique (tenant_id, "name", "type", "system")
);
select setval(pg_get_serial_sequence('se_groups', 'id'), 1000);
CREATE INDEX idx_se_groups_tenant_id_code_index ON se_groups (tenant_id, tenant_code);
comment
on column se_groups.name is '组名称';
comment
on column se_groups.description is '描述';
comment
on column se_groups.type is '类型0普通 10单位';
comment
on column se_groups.system is '区分系统不同的';
comment
on table se_groups is '权限组基础表.';

--drop table if exists se_group_authorities;
create table se_group_authorities
(
    id           serial primary key,
    group_id     int          not null,
    authority    varchar(128) not null,
    created_time timestamp default current_timestamp,
    updated_time timestamp default current_timestamp,
    unique (group_id, authority)
);
comment
on column se_group_authorities.id is '组ID';
comment
on column se_group_authorities.authority is '组的权限';
comment
on table se_group_authorities is '权限组权限认证表.';

--drop table if exists se_group_members;
create table se_group_members
(
    id           serial primary key,
    group_id     int    not null,
    user_id      bigint not null,
    created_time timestamp default current_timestamp,
    updated_time timestamp default current_timestamp,
    unique (group_id, user_id)
);
comment
on column se_group_members.group_id is '组id';
comment
on column se_group_members.user_id is '用户名ID';
comment
on table se_group_members is '权限组用户关系表.';

--drop table if exists se_tenants;
create table se_tenants
(
    id           serial primary key,
    code         varchar(32)  not null default '0',
    "name"       varchar(128) not null,
    address      varchar(1024),
    pid          int          not null default 0,
    description  text,
    extend       jsonb,
    created_time timestamp             default current_timestamp,
    updated_time timestamp             default current_timestamp,
    unique (code)
);
select setval(pg_get_serial_sequence('se_tenants', 'id'), 1000);
CREATE INDEX idx_se_tenants_extend_gin ON se_tenants USING gin (extend);
comment
on column se_tenants.name is '租户名';
comment
on column se_tenants.code is '租户名';
comment
on column se_tenants.description is '租户描述';
comment
on column se_tenants.address is '租户地址';
comment
on column se_tenants.pid is '租户父级ID';
comment
on table se_tenants is '租户用户关系表';

--drop table if exists se_tenant_members;
create table se_tenant_members
(
    id           serial8 primary key,
    tenant_id    int     not null,
    user_id      bigint  not null,
    is_default   boolean not null default true,
    created_time timestamp        default current_timestamp,
    updated_time timestamp        default current_timestamp,
    unique (tenant_id, user_id)
);
comment
on column se_tenant_members.user_id is '用户ID';
comment
on column se_tenant_members.tenant_id is '租户ID';
comment
on table se_tenant_members is '租户用户关系表';

--drop table if exists se_masses;
create table se_masses
(
    id                             serial8 primary key,
    tenant_id                      int         not null default 0,
    tenant_code                    varchar(32) not null default '0',
    user_id                        bigint,
    "name"                         varchar(20) not null,
    id_card                        varchar(30) not null,
    description                    text,
    sex                            varchar(10),
    age                            int,
    birth_date                     date,
    email                          varchar(256),
    phone                          varchar(30),
    address                        text,
    extend                         jsonb,
    pid                            BIGINT      not null default 0,
    created_time                   timestamp            default current_timestamp,
    updated_time                   timestamp            default current_timestamp,
    "household_category"           varchar(32),
    "household_relationship"       varchar(32)          default '99',
    "nation"                       varchar(8),
    "political_status"             varchar(32),
    "education"                    varchar(32),
    "student_status"               varchar(32),
    "health_status"                varchar(32),
    "labor_skills"                 varchar(32),
    "development_status"           varchar(255),
    "work_area"                    varchar(255),
    "working_time"                 numeric(6, 1),
    "is_major_illness_insurance"   bool,
    "is_destitute_supporter"       bool,
    "is_subsistence_allowance"     bool,
    "is_rural_endowment_insurance" bool,
    "is_rural_medicare_insurance"  bool,
    unique (id_card),
    unique (user_id)
);

CREATE INDEX idx_sys_masses_extend_gin ON se_masses USING gin (extend);
CREATE INDEX idx_se_masses_tenant_id_code_index ON se_masses (tenant_id, tenant_code);
comment
on column se_masses.tenant_id is '人民群众默认租户id,只有自己默认了设置租户ID';
comment
on column se_masses.user_id is '人民群众登录信息id,只有用户登录注册后,认证成功';
comment
on column se_masses.name is '人民群众真实姓名';
comment
on column se_masses.id_card is '人民群众身份证ID';
comment
on column se_masses.address is '人民群众联系地址';
comment
on table se_masses is '人民群众信息表';