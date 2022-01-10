package com.nowcoder.community.entity;

public interface LoginStatus {
    int SUCCESS = 0;
    int EXIST = 1;
    int FIELD = 2;

    int DEFUALT_TIME = 3600*12;
    int REMEBERME_TIME = 3600*24;

    //实体类对象状态
    int ENTITY_TYPE_DISCUSS = 1;

    int ENTITY_ID_DISCUSS = 2;

    int ENTITY_USER_DISCUSS = 3;
}
