alter table se_groups
    add column type int not null default 0;
-- 系统管理 1 防贫管理员2 帮扶管理员3 信息专员3
update se_groups
set type=1
where name ilike '%系统管理组';
update se_groups
set type=2
where name ilike '%防贫管理员组';
update se_groups
set type=3
where name ilike '%帮扶管理员组';
update se_groups
set type=4
where name ilike '%信息专员组';

alter table se_groups
    add column system varchar(30) not null default 'poverty';

alter table se_authority_dict
    add column system varchar(30) not null default 'poverty';

alter table se_authority_dict
drop
constraint se_authority_dict_authority_key;
alter table se_authority_dict
    add unique (authority, system);

alter table sys_custom_menus
    add column system varchar(30) not null default 'poverty';

alter table sys_custom_column
    add column system varchar(30) not null default 'poverty';

alter table sys_custom_column
    rename to sys_custom_column_bak;

alter table sys_custom_column_bak
drop
constraint sys_custom_column_pkey;

alter table sys_custom_column_bak
drop
constraint sys_custom_column_tenant_id_user_id_prop_name_label_categor_key;

drop index idx_sys_custom_column_extend_gin;

drop table if exists sys_custom_column;
create table sys_custom_column
(
    id              serial8 primary key,
    tenant_id       int           not null default 0,
    user_id         bigint        not null default 0,
    system          varchar(64)   not null default 'country',
    menu_id         bigint        not null default 0,
    category        bigint        not null default 0,
    prop_name       varchar(256)  not null,
    label           varchar(256)  not null,
    is_fixed        bool                   default false,
    is_hide         bool                   default false,
    is_custom       bool                   default false,
    is_sortable     bool                   default true,
    is_show         bool                   default true,
    icon            varchar(1024),
    type            varchar(256)           default 'text',
    sort_no         int           not null default 0,
    prop_code       varchar(256),
    width           varchar(256)           default '120',
    filtration      bool                   default false,
    is_edit         bool                   default false,
    dictionary_type varchar(100),
    path            varchar(1024) not null,
    cell_style      varchar(100)           default 'center',
    from_type       int,
    created_time    timestamp              default current_timestamp,
    updated_time    timestamp              default current_timestamp,
    extend          jsonb,
    unique (tenant_id, menu_id, user_id, system, prop_name)
);

CREATE INDEX idx_sys_custom_column_extend_gin ON sys_custom_column USING gin (extend);
comment
on column sys_custom_column.id is '主键';
comment
on column sys_custom_column.tenant_id is '租户id';
comment
on column sys_custom_column.user_id is '用户id';
comment
on column sys_custom_column.label is '列名称';
comment
on column sys_custom_column.is_fixed is '是否固定';
comment
on column sys_custom_column.is_hide is '是否换行';
comment
on column sys_custom_column.is_custom is '是否自定义内容';
comment
on column sys_custom_column.is_sortable is '是否排序';
comment
on column sys_custom_column.is_show is '是否显示';
comment
on column sys_custom_column.icon is '图标';
comment
on column sys_custom_column.type is '类型';
comment
on column sys_custom_column.category is '相同自定义列母版id';
comment
on column sys_custom_column.sort_no is '排序';
comment
on column sys_custom_column.prop_name is '字段名';
comment
on column sys_custom_column.prop_code is '字段编码';
comment
on column sys_custom_column.width is '列宽度';
comment
on column sys_custom_column.filtration is '是否过滤';
comment
on column sys_custom_column.is_edit is '是否可编辑';
comment
on column sys_custom_column.dictionary_type is '字典类型';
comment
on column sys_custom_column.path is '资源路径';
comment
on column sys_custom_column.cell_style is '单元格样式';
comment
on column sys_custom_column.from_type is '数据来源';
comment
on column sys_custom_column.created_time is '创建时间';
comment
on column sys_custom_column.updated_time is '修改时间';
comment
on column sys_custom_column.extend is '扩展JSON字段';
comment
on table sys_custom_column is '用户自定义列表';

delete
from sys_custom_column_bak
where id not in (select min(id)
                 from sys_custom_column_bak
                 group by prop_name, res_type);

insert into sys_custom_column(tenant_id, user_id, system, menu_id, prop_name,
                              label, is_fixed, is_hide, is_custom, is_sortable, is_show,
                              icon, type, category, sort_no, width, filtration, is_edit, dictionary_type,
                              path, cell_style, from_type, created_time, updated_time, extend)
select tenant_id,
       user_id,
       sys_custom_column_bak.system,
       se_authority_dict.id,
       prop_name,
       label,
       is_fixed,
       is_hide,
       is_custom,
       is_sortable,
       is_show,
       icon,
       sys_custom_column_bak.type,
       0,
       sort_no,
       width,
       filtration,
       is_edit,
       dictionary_type,
       se_authority_dict.path,
       cell_style,
       from_type,
       sys_custom_column_bak.created_time,
       sys_custom_column_bak.updated_time,
       null
from sys_custom_column_bak,
     se_authority_dict
where sys_custom_column_bak.res_type = se_authority_dict.path
  and sys_custom_column_bak.system = se_authority_dict.system;

drop table if exists sys_feed_back;
create table sys_feed_back
(
    id            serial8 primary key,
    tenant_id     int          not null default 0,
    tenant_code   varchar(32)  not null default '0',
    user_id       bigint       not null default 0,
    system        varchar(64)  not null default 'country',
    title         varchar(256) not null,
    type          varchar(8)   not null default 0,
    status        int          not null default 0,
    content       text         not null,
    reply_id      bigint,
    reply_time    timestamp,
    reply_content text,
    created_time  timestamp             default current_timestamp,
    updated_time  timestamp             default current_timestamp,
    extend        jsonb
);
create index idx_sys_feed_back_ids_in on sys_feed_back (tenant_id, tenant_code, user_id, system, title);
CREATE INDEX idx_sys_feed_back_extend_gin ON sys_feed_back USING gin (extend);
COMMENT
ON table sys_feed_back is '意见反馈';
COMMENT
ON COLUMN sys_feed_back.title is '意见反馈标题';
COMMENT
ON COLUMN sys_feed_back.type is '意见反馈类型';
COMMENT
ON COLUMN sys_feed_back.status is '意见反馈状态';
COMMENT
ON COLUMN sys_feed_back.content is '意见反馈内容';
COMMENT
ON COLUMN sys_feed_back.reply_id is '回复人id';
COMMENT
ON COLUMN sys_feed_back.reply_id is '回复时间';
COMMENT
ON COLUMN sys_feed_back.reply_content is '回复内容';

drop table if exists sys_authority_dict;
create table sys_authority_dict
(
    id           serial primary key,
    pid          int          not null default 0,
    system       varchar(32)  not null default 'country',
    authority    varchar(128) not null,
    name         varchar(256) not null,
    sort         int          not null default 0,
    description  text,
    path         text,
    type         int          not null default 0,
    extend       jsonb,
    created_time timestamp             default current_timestamp,
    updated_time timestamp             default current_timestamp,
    unique (system, authority)
);
CREATE INDEX idx_sys_authority_dict_extend_gin ON sys_authority_dict USING gin (extend);
comment
on column sys_authority_dict.authority is '定义权限,如: ROLE_USER';
comment
on column sys_authority_dict.name is '定义权限显示名,如: 用户管理';
comment
on column sys_authority_dict.description is '描述';
comment
on column sys_authority_dict.path is '权限控制路径,如: /user';
comment
on column sys_authority_dict.pid is '权限树父节点ID,根节点为0';
comment
on column sys_authority_dict.type is '菜单类型: 0,菜单,其他为非菜单';
comment
on table sys_authority_dict is '权限字典,主要生成用户认证权限的基础表,所有权限在这里定义.';
select setval(pg_get_serial_sequence('sys_authority_dict', 'id'), 500);

insert into sys_authority_dict (id, system, authority, name, sort, description, path,
                                pid, type, extend)
select id,
       system,
       authority,
       name,
       sort,
       description,
       path,
       pid,
       type,
       extend
from se_authority_dict;

alter table sys_configurations
    add column system varchar(32) not null default 'poverty';

alter table sys_configurations
drop
constraint sys_configurations_tenant_id_type_key;

alter table sys_configurations
    add constraint sys_configurations_tenant_id_type_system_key
        unique (tenant_id, system, type);