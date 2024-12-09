package org.example;

public enum Money {
    grn500(500),
    grn200(200),
    grn100(100),
    grn50(50),
    grn20(20),
    grn10(10),
    grn5(5);

    private final int naminal;

    Money(int naminal) {
        this.naminal = naminal;
    }

    public int getValue() {
        return naminal;
    }

    public static Money fromValue(int value) {
        for (Money money : values()) {
            if (money.getValue() == value) {
                return money;
            }
        }
        return null;
    }
}
