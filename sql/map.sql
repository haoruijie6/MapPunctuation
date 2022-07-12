-- auto-generated definition
create table t_image_puancuation_information
(
    id           varchar(200) not null comment '主键id',
    image_name   varchar(200) null comment '图片名称',
    image_url    varchar(200) null comment '图片路径',
    image_width  varchar(100) null comment '图片宽度',
    image_heigth varchar(100) null comment '图片高度',
    create_time  datetime     not null comment '创建时间',
    create_by    varchar(200) not null comment '创建人',
    update_time  datetime     null comment '修改时间',
    update_by    varchar(100) null comment '修改者'
)
    charset = utf8;


-- auto-generated definition
create table t_punctuation_information
(
    id                    varchar(200)             not null comment '主键id',
    image_id              varchar(200)             not null comment '线路图id',
    relative_to_picture_X varchar(200) default '0' not null comment 'x距离',
    relative_to_picture_Y varchar(200) default '0' not null comment 'y距离',
    sign_width            varchar(200) default '0' not null comment '宽度',
    sign_height           varchar(200) default '0' not null comment '高度',
    punctuation_element   varchar(100) default '0' null comment '样式',
    sign_content          varchar(200) default '0' not null comment '内容',
    sign_shape            varchar(100)             null comment '形状',
    sign_color            varchar(100)             null comment '颜色',
    state                 varchar(100)             null comment '状态0使用1不使用',
    sort                  int                      not null comment '顺序',
    create_time           datetime                 not null comment '创建时间'
)
    charset = utf8;

