create table if not exists eylsia.user
(
    id           bigint auto_increment
    primary key,
    username     varchar(256)                       null comment '用户名',
    useraccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userpassword varchar(512)                       not null comment '用户密码',
    phone        varchar(256)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userstatus   tinyint  default 0                 not null comment ' 状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    列_name      int                                null,
    列_name_2    int                                null,
    列_name_3    int                                null,
    column_name  int                                null,
    role         int      default 0                 not null comment '用户角色 0-普通用户 1-管理员',
    planetCode   varchar(512)                       null comment '编号'
    )
    comment '用户';

