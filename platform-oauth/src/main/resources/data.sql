insert into se_users(id, username, password, enabled)
values (1, 'xxhzj',
        '7d8a68bc5d507bd19bc153ff10bcdef66f5a5f3d0c1ab2438630e50b5c65894bccc2c7e4404c5afa', true);

insert into se_authorities(user_id, authority, "system")
select 1, authority, "system"
from sys_authority_dict
union all
select DISTINCT 1, permission, "system"
from sys_authority_dict,
     jsonb_to_recordset(extend -> 'permissions') as items(permission text);