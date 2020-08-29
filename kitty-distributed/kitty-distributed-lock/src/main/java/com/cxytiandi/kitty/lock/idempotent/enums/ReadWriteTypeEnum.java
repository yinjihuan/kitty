package com.cxytiandi.kitty.lock.idempotent.enums;


public enum ReadWriteTypeEnum {

    ORDER("顺序"),
    PARALLEL("并行");

    ReadWriteTypeEnum(String type) {
        this.type = type;
    };

    private String type;

    public String getType() {
        return type;
    }

    public static boolean exists(String type) {
        for (ReadWriteTypeEnum typeEnum : ReadWriteTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
