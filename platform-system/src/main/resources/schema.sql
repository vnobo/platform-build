drop table if exists sys_authority_dict;
create table sys_authority_dict
(
    id           serial primary key,
    pid          int          not null default 0,
    system       varchar(64)  not null default 'country',
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

drop table if exists sys_custom_menus;
create table sys_custom_menus
(
    id           serial8 primary key,
    user_id      bigint      not null default 0,
    menu_id      bigint      not null default 0,
    system       varchar(64) not null default 'country',
    name         varchar(64) not null,
    path         varchar(1024),
    sort         int         not null default 0,
    type         int                  default 0,
    pid          bigint      not null default 0,
    extend       jsonb,
    created_time timestamp            default current_timestamp,
    updated_time timestamp            default current_timestamp,
    unique (user_id, menu_id)
);

CREATE INDEX idx_sys_custom_menus_extend_gin ON sys_custom_menus USING gin (extend);
comment
on column sys_custom_menus.user_id is '用户登录信息ID';
comment
on column sys_custom_menus.menu_id is '菜单id';
comment
on column sys_custom_menus.name is '菜单名称';
comment
on column sys_custom_menus.path is '菜单路径';
comment
on column sys_custom_menus.sort is '菜单排序';
comment
on column sys_custom_menus.extend is '扩展JSON字段';
comment
on table sys_custom_menus is '用户自定义菜单.';

drop table if exists sys_configurations;
create table sys_configurations
(
    id            serial8 primary key,
    tenant_id     int          not null default 0,
    tenant_code   varchar(64)  not null default '0',
    system        varchar(64)  not null default 'country',
    type          varchar(64)  not null,
    name          varchar(128) not null,
    configuration jsonb        not null,
    created_time  timestamp             default current_timestamp,
    updated_time  timestamp             default current_timestamp,
    unique (tenant_id, system, type)
);

CREATE INDEX idx_sys_configurations_configuration_gin ON sys_configurations USING gin (configuration);
comment
on column sys_configurations.tenant_id is '租户ID';
comment
on column sys_configurations.name is '配置名称';
comment
on column sys_configurations.configuration is '配置项JSON字段';
comment
on table sys_configurations is '租户系统配置表.';

drop table if exists sys_user_configurations;
create table sys_user_configurations
(
    id            serial8 primary key,
    tenant_id     int          not null default 0,
    tenant_code   varchar(64)  not null default '0',
    user_id       bigint       not null default 0,
    system        varchar(64)  not null default 'country',
    type          varchar(128) not null,
    configuration jsonb        not null,
    created_time  timestamp             default current_timestamp,
    updated_time  timestamp             default current_timestamp,
    unique (user_id, type, system)
);

CREATE INDEX idx_sys_user_configurations_configuration_gin ON sys_user_configurations USING gin (configuration);
comment
on column sys_user_configurations.tenant_id is '租户ID';
comment
on column sys_user_configurations.system is '系统类型';
comment
on column sys_user_configurations.type is '配置类型';
comment
on column sys_user_configurations.configuration is '配置项JSON字段';
comment
on table sys_user_configurations is '用户系统配置表';

drop table if exists sys_villages;
create table sys_villages
(
    id            serial8 primary key,
    code          bigint       not null,
    name          varchar(128) not null,
    province_code bigint       not null,
    city_code     bigint       not null,
    area_code     bigint       not null,
    street_code   bigint       not null,
    unique (province_code, city_code, area_code, street_code, code)
);

comment
on column sys_villages.code is '村庄行政编码';
comment
on column sys_villages.street_code is '乡级（乡镇、街道）行政编码';
comment
on column sys_villages.province_code is '省级（省份、直辖市、自治区）行政编码';
comment
on column sys_villages.city_code is '地级（城市）行政编码';
comment
on column sys_villages.area_code is '县级（区县）行政编码';
comment
on table sys_villages is '全国村级行政记录表.';

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

drop table if exists sys_news;
create table sys_news
(
    id           serial8 primary key,
    tenant_id    int           not null default 0,
    tenant_code  varchar(32)   not null default '0',
    system       varchar(64)   not null default 'country',
    title        varchar(1024) not null,
    type         varchar(8)    not null,
    status       varchar(32)   not null default '01',
    content      text          not null,
    source       varchar(10),
    auditor      bigint,
    audit_time   timestamp,
    creator      bigint,
    updater      bigint,
    created_time timestamp              default current_timestamp,
    updated_time timestamp              default current_timestamp,
    extend       jsonb
);
CREATE INDEX idx_sys_news_tenant_creator_title_idx ON sys_news (tenant_code, system, creator, title, type);
CREATE INDEX idx_sys_news_extend_gin ON sys_news USING gin (extend);
comment
on column sys_news.system is '新闻系统类型';
comment
on column sys_news.tenant_id is '租户id';
comment
on column sys_news.creator is '创建人';
comment
on column sys_news.updater is '修改人';
comment
on column sys_news.title is '标题';
comment
on column sys_news.type is '新闻类型';
comment
on column sys_news.status is '状态-审核用';
comment
on column sys_news.content is '新闻内容';
comment
on column sys_news.source is '新闻来源';
comment
on column sys_news.auditor is '审核人id';
comment
on column sys_news.audit_time is '审核时间';
comment
on column sys_news.content is '新闻内容';
comment
on column sys_news.created_time is '创建时间';
comment
on column sys_news.updated_time is '最后一次修改时间';
comment
on column sys_news.extend is '预留字段';
comment
on table sys_news is '新闻';

drop table if exists sys_notifications;
create table sys_notifications
(
    id           serial8 primary key,
    tenant_id    int           not null default 0,
    tenant_code  varchar(32)   not null default '0',
    system       varchar(64)   not null default 'country',
    type         varchar(12)   not null,
    title        varchar(1024) not null,
    content      text          not null,
    creator      bigint,
    updater      bigint,
    created_time timestamp              default current_timestamp,
    updated_time timestamp              default current_timestamp,
    extend       jsonb
);
CREATE INDEX idx_sys_notifications_tenant_creator_title_idx ON sys_news (tenant_code, system, creator, title, type);
CREATE INDEX idx_sys_notifications_extend_gin ON sys_notifications USING gin (extend);
comment
on column sys_notifications.id is '主键';
comment
on column sys_notifications.tenant_id is '租户id';
comment
on column sys_notifications.tenant_code is '租户code';
comment
on column sys_notifications.type is '1、公告 2、通知';
comment
on column sys_notifications.title is '行政监督标题';
comment
on column sys_notifications.content is '行政监督内容';
comment
on column sys_notifications.created_time is '创建时间';
comment
on column sys_notifications.updated_time is '最后一次修改时间';
comment
on column sys_notifications.extend is '预留字段';
comment
on column sys_notifications.creator is '创建人';
comment
on column sys_notifications.updater is '修改人';
comment
on table sys_notifications is '通知公告';

drop table if exists sys_common_problem;
create table sys_common_problem
(
    id           serial8 primary key,
    tenant_id    int                  default 0,
    tenant_code  varchar(64)          default '0',
    title        varchar(1024),
    solution     text,
    system       varchar(64) not null default 'country',
    extend       jsonb,
    created_time timestamp            default current_timestamp,
    updated_time timestamp            default current_timestamp
);

CREATE INDEX idx_sys_common_problem_extend_gin ON sys_common_problem USING gin (extend);
comment
on column sys_common_problem.tenant_id is '租户ID';
comment
on column sys_common_problem.title is '问题标题';
comment
on column sys_common_problem.solution is '解决方法';
comment
on column sys_common_problem.system is '系统类型';
comment
on column sys_common_problem.extend is '保留字段';
comment
on table sys_common_problem is '常见问题.';