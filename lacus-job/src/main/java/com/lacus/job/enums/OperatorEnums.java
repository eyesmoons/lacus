package com.lacus.job.enums;


public enum OperatorEnums {


    INSERT_OP("c", "insert"),
    CREATE_OP("CREATE", "CREATE"),

    UPDATE_OP("u", "update"),

    ROW_OP("r", "row"),

    DELETE_OP("d", "delete");


    private String op;

    private String desc;


    private OperatorEnums(String op, String desc) {
        this.op = op;
        this.desc = desc;
    }

    public String getOp() {
        return op;
    }

    public String getDesc() {
        return desc;
    }


    public static OperatorEnums getOpEnums(String op) {
        for (OperatorEnums opEnums : OperatorEnums.values()) {
            if (opEnums.getOp().equals(op)) {
                return opEnums;
            }
        }
        return null;
    }


}
