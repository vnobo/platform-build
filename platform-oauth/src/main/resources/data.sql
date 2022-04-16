insert into se_users(id, username, password, enabled)
values (1, 'xxhzj',
        '7d8a68bc5d507bd19bc153ff10bcdef66f5a5f3d0c1ab2438630e50b5c65894bccc2c7e4404c5afa', true);

insert into se_authorities(user_id, authority, "system")
select 1, authority, "system"
from sys_authority_dict
where system = 'country'
union all
select DISTINCT 1, permission, "system"
from sys_authority_dict,
     jsonb_to_recordset(extend - > 'permissions') as items(permission text)
where system = 'country';

insert into se_groups(id, tenant_id, "name", "system", "type", description)
values (1, 0, 'Administrators', 'country', 1, '系统超级管理员组');

insert into se_group_authorities(group_id, authority)
values (1, 'ROLE_GROUP_ADMINISTRATORS');

insert into se_group_members(group_id, user_id)
values (1, 1);

insert into se_groups(id, tenant_id, "name", "system", "type", description)
values (2, 0, '访客用户组', 'country', 2, '所有未实名认证的访客用户组');

insert into se_group_authorities(group_id, authority)
values (2, 'ROLE_GROUP_ANONYMOUS');