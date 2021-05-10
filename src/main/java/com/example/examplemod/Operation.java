package com.example.examplemod;

import org.lwjgl.glfw.GLFW;

public enum Operation {
    EMPTY(""),
    CLEAR("C"),
    CLEAR_EVERYTHING("CE"),
    REMOVE("<"),
    DOT("."),
    EQUALS("="),
    ADD("+", Float::sum),
    SUBTRACT("-", (a, b) -> a-b),
    MULTIPLY("*", ((a, b) -> a*b)),
    DIVIDE("/", ((a, b) -> b == 0 ?  0 : a/b));

    public interface Calculation {
        float calculate(float a, float b);
    }

    private String value;
    private Calculation functionality;

    Operation(String v, Calculation functionality) {
        this.value = v;
        this.functionality = functionality;
    }

    Operation(String v) {
        this(v, (a, b)-> 0);
    }

    public String getValue() {
        return value;
    }

    public float calculate(float a, float b) {
        return this.functionality.calculate(a, b);
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
}
