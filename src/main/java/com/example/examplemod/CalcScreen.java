package com.example.examplemod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalcScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int calcWidth = 110;
    private static final int marginWidth = 6;
    private static final int textFieldWidth = calcWidth - 2*marginWidth;
    private static final int textFieldHeight = 24;
    private static final int buttonWidth = 20;
    private static final int buttonHeight = 20;

    private Button[] digitButtons = new Button[10];
    private DisplayWidget textFieldWidget;

    private int posX = 20;
    private int posY = 20;

    private String firstValue = "";
    private String secondValue = "";
    private String resultValue = "";
    private Operation currentOperation = Operation.EMPTY;

    DecimalFormatSymbols otherSymbols;
    DecimalFormat decimalFormat;

    protected CalcScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
        otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        decimalFormat = new DecimalFormat("##0.######", otherSymbols);
    }

    protected void init() {

        createOperationButton(0, 0, Operation.CLEAR_EVERYTHING);
        createOperationButton(1, 0, Operation.CLEAR);
        createOperationButton(2, 0, Operation.REMOVE);
        createOperationButton(3, 0, Operation.ADD);

        for (int y = 2; y >= 0; y--) {
            for (int x = 2; x >= 0; x--) {
                createDigitButton(x, y+1, 7 + x - (y * 3)); // generates numbers 1-9 based on their position
            }
        }

        createOperationButton(3, 1, Operation.SUBTRACT);
        createOperationButton(3, 2, Operation.MULTIPLY);
        createOperationButton(3, 3, Operation.DIVIDE);
        createDigitButton(1, 4, 0);
        createOperationButton(2, 4, Operation.DOT);
        createOperationButton(3, 4, Operation.EQUALS);

        textFieldWidget = new DisplayWidget(this.font, posX + marginWidth, posY + marginWidth, textFieldWidth, textFieldHeight, new StringTextComponent("output"));
        textFieldWidget.setValue("0");
    }

    private void createDigitButton(int x, int y, int value) {
        digitButtons[value] = this.addButton(new Button(posX + marginWidth + ((buttonWidth + marginWidth) * x), posY + textFieldHeight + marginWidth*2 + ((buttonHeight + marginWidth) * y), buttonWidth, buttonHeight,  new StringTextComponent(Integer.toString(value)), (p_214187_1_) -> {
            digitButtonPressed(Integer.toString(value));
        }));
    }

    private void createOperationButton(int x, int y, Operation operation) {
        this.addButton(new Button(posX + marginWidth + ((buttonWidth + marginWidth) * x), posY + textFieldHeight + marginWidth*2 + ((buttonHeight + marginWidth) * y), buttonWidth, buttonHeight, new StringTextComponent(operation.getValue()), (p_214187_1_) -> {
            operationButtonPressed(operation);
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        fill(matrixStack, this.posX - 1, this.posY - 1, this.posX + calcWidth + 1, this.posY + 166 + 1, -1);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        this.minecraft.getTextureManager().bind(AbstractGui.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int brightness = 128;
        bufferbuilder.vertex(this.posX, this.posY + 166, 0.0D).uv(0F, (float)(this.posY + 166) / 32.0F).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX +calcWidth, this.posY + 166, 0.0D).uv((float)calcWidth / 32.0F, (float)(this.posY + 166) / 32.0F).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX +calcWidth, this.posY, 0.0D).uv((float)calcWidth / 32.0F, 0).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX, this.posY, 0.0D).uv(0, 0).color(brightness, brightness, brightness, 255).endVertex();
        tessellator.end();

        textFieldWidget.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
        super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    private void digitButtonPressed(String value) {
        if (!resultValue.equals("")) {
            resultValue = "";
            firstValue = "";
            secondValue = "";
            currentOperation = Operation.EMPTY;
        }
        appendCurrentValue(value);
        updateDisplay();
    }

    private void operationButtonPressed(Operation operation) {
        String currentValue = getCurrentValue();
        switch(operation) {
            case CLEAR:
                setCurrentValue("");
                if (resultValue.equals("")) {
                    break;
                }
            case CLEAR_EVERYTHING:
                currentOperation = Operation.EMPTY;
                firstValue = "";
                secondValue = "";
                resultValue = "";
                break;


            case EQUALS:
                if (secondValue.equals("") || firstValue.equals("") || currentOperation == Operation.EMPTY) {
                    break;
                }

                float floatFirstValue = Float.parseFloat(firstValue);
                float floatSecondValue = Float.parseFloat(secondValue);
                float calculationOutput = calculate(floatFirstValue, floatSecondValue, currentOperation);

                resultValue = decimalFormat.format(calculationOutput);

                LOGGER.error(resultValue);

                break;
            case DOT:
                if (!resultValue.equals("")) {
                    currentOperation = Operation.EMPTY;
                    firstValue = "0.";
                    secondValue = "";
                    resultValue = "";
                    break;
                }
                if (currentValue.contains(".")) {
                    break;
                }
                if (currentValue.equals("")) {
                    appendCurrentValue("0.");
                } else {
                    appendCurrentValue(".");
                }
                break;
            case REMOVE:
                if (!resultValue.equals("")) {
                    firstValue = resultValue.substring(0, resultValue.length()-1);
                    resultValue = "";
                    secondValue = "";
                    currentOperation = Operation.EMPTY;
                    updateDisplay();
                    break;
                }
                if (currentValue.length() > 0)
                    setCurrentValue(currentValue.substring(0, currentValue.length() - 1));
                break;
            default:
                if (!resultValue.equals("")) {
                    firstValue = resultValue;
                    resultValue = "";
                    secondValue = "";
                }
                if (firstValue.equals("")) {
                    firstValue = "0";
                }
                if (!secondValue.equals("")) {
                    operationButtonPressed(Operation.EQUALS);
                    firstValue = resultValue;
                    resultValue = "";
                    secondValue = "";
                }
                currentOperation = operation;
        }
        updateDisplay();
    }

    private void updateDisplay() {
        if (!resultValue.equals("")) {
            textFieldWidget.setValue(resultValue);
            textFieldWidget.topValue = firstValue + currentOperation.getValue() + secondValue + "=";
        } else if (firstValue.equals("")) {
            textFieldWidget.setValue("0");
            textFieldWidget.topValue = "";
        } else {
            if (currentOperation == Operation.EMPTY) {
                textFieldWidget.setValue(firstValue);
                textFieldWidget.topValue = "";
            } else {
                textFieldWidget.topValue = firstValue + currentOperation.getValue();
                textFieldWidget.setValue(secondValue);
            }
        }
    }

    private float calculate(float firstVal, float secondVal, Operation operation) {
        float returnValue = operation.calculate(firstVal, secondVal);
        if (returnValue == -0.0f) {
            returnValue = 0;
        }

        return returnValue;
    }

    public void appendCurrentValue(String string) {
        if (!(getCurrentValue().equals("") && string.equals("0"))) {
            setCurrentValue(getCurrentValue() + string);
        }
    }

    public void setCurrentValue(String value) {
        if (currentOperation == Operation.EMPTY) {
            firstValue = value;
        } else {
            secondValue = value;
        }
    }

    public String getCurrentValue() {
        if (currentOperation == Operation.EMPTY) {
            return firstValue;
        } else {
            return secondValue;
        }
    }

    @Override
    public boolean keyPressed(int keycode, int p_231046_2_, int p_231046_3_) {

        Operation operation = Operation.handleInput(keycode, p_231046_3_);
        if (operation != Operation.EMPTY) {
            operationButtonPressed(operation);
            return true;
        }

        if (ExampleMod.openCalcKeybind.getKey().getValue() == keycode) {
            this.onClose();
            return true;
        }

        if (keycode >= GLFW.GLFW_KEY_0 && keycode <= GLFW.GLFW_KEY_9) {
            digitButtonPressed(Integer.toString(keycode - GLFW.GLFW_KEY_0));
            return true;
        }

        if (keycode >= GLFW.GLFW_KEY_KP_0 && keycode <= GLFW.GLFW_KEY_KP_9) {
            digitButtonPressed(Integer.toString(keycode - GLFW.GLFW_KEY_KP_0));
            return true;
        }

        return super.keyPressed(keycode, p_231046_2_, p_231046_3_);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
