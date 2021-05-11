package com.mpcs.craftculator;

import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Operation {
    EMPTY(""),
    CLEAR("C"),
    CLEAR_ENTRY("CE"),
    REMOVE("<"),
    DOT("."),
    EQUALS("="),
    ADD("+", (BigDecimal::add)),
    SUBTRACT("-", BigDecimal::subtract),
    MULTIPLY("*", (BigDecimal::multiply)),
    DIVIDE("/", ((a, b) -> b.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : a.divide(b, RoundingMode.HALF_UP)));

    private final String value;
    private final Calculation functionality;
    Operation(String v, Calculation functionality) {
        this.value = v;
        this.functionality = functionality;
    }

    Operation(String v) {
        this(v, (a, b) -> BigDecimal.ZERO);
    }

    public static Operation handleInput(int keycode, int modifiers) {
        if (modifiers == GLFW.GLFW_MOD_SHIFT) {
            switch (keycode) {
                case GLFW.GLFW_KEY_8:
                    return MULTIPLY;
                case GLFW.GLFW_KEY_MINUS:
                    return SUBTRACT;
                case GLFW.GLFW_KEY_EQUAL:
                    return ADD;
            }
        }

        switch (keycode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                return REMOVE;
            case GLFW.GLFW_KEY_PERIOD:
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return DOT;
            case GLFW.GLFW_KEY_EQUAL:
            case GLFW.GLFW_KEY_KP_EQUAL:
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                return EQUALS;
            case GLFW.GLFW_KEY_KP_ADD:
                return ADD;
            case GLFW.GLFW_KEY_KP_SUBTRACT:
            case GLFW.GLFW_KEY_MINUS:
                return SUBTRACT;
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return MULTIPLY;
            case GLFW.GLFW_KEY_KP_DIVIDE:
            case GLFW.GLFW_KEY_SLASH:
                return DIVIDE;
        }

        return EMPTY;
    }

    public String getValue() {
        return value;
    }

    public BigDecimal calculate(BigDecimal a, BigDecimal b) {
        return this.functionality.calculate(a, b);
    }

    public interface Calculation {
        BigDecimal calculate(BigDecimal a, BigDecimal b);
    }
}
