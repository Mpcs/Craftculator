package com.mpcs.craftculator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class CalcScreen extends Screen {
    private static final int calcWidth = 110;
    private static final int calcHeight = 166;
    private static final int marginWidth = 6;
    private static final int textFieldWidth = calcWidth - 2 * marginWidth;
    private static final int textFieldHeight = 24;
    private static final int buttonWidth = 20;
    private static final int buttonHeight = 20;

    private static final int topBarHeight = 10;
    private static int topBarColor;
    private static int diffX = 0;
    private static int diffY = 0;
    private static boolean canDrag = false;
    private final Button[][] calcButtons = new Button[4][5];
    private DisplayWidget displayWidget;
    private int posX = 0;
    private int posY = 0;
    private String firstValue = "";
    private String secondValue = "";
    private String resultValue = "";
    private Operation currentOperation = Operation.EMPTY;

    protected CalcScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
    }

    protected void init() {

        this.posX = CalcConfig.GENERAL.positionX.get();
        this.posY = CalcConfig.GENERAL.positionY.get() + topBarHeight;

        createAndAddOperationButton(0, 0, Operation.CLEAR_ENTRY);
        createAndAddOperationButton(1, 0, Operation.CLEAR);
        createAndAddOperationButton(2, 0, Operation.REMOVE);
        createAndAddOperationButton(3, 0, Operation.ADD);

        for (int y = 2; y >= 0; y--) {
            for (int x = 2; x >= 0; x--) {
                createAndAddDigitButton(x, y + 1, 7 + x - (y * 3)); // generates numbers 1-9 based on their position
            }
        }

        createAndAddOperationButton(3, 1, Operation.SUBTRACT);
        createAndAddOperationButton(3, 2, Operation.MULTIPLY);
        createAndAddOperationButton(3, 3, Operation.DIVIDE);
        createAndAddDigitButton(1, 4, 0);
        createAndAddOperationButton(2, 4, Operation.DOT);
        createAndAddOperationButton(3, 4, Operation.EQUALS);

        displayWidget = new DisplayWidget(this.font, posX + marginWidth, posY + marginWidth, textFieldWidth, textFieldHeight, new StringTextComponent("output"));
        displayWidget.setFirstValue("0");

        Random random = new Random();
        final float hue = random.nextFloat();
        final float saturation = 0.9f;
        final float luminance = 1.0f;
        topBarColor = Color.getHSBColor(hue, saturation, luminance).getRGB();
        checkOutOfBoundsAndMove();
    }

    private void createAndAddDigitButton(int x, int y, int value) {
        calcButtons[x][y] = this.addButton(new Button(getButtonPosX(x, y), getButtonPosY(x, y), buttonWidth, buttonHeight, new StringTextComponent(Integer.toString(value)), (p_214187_1_) -> digitButtonPressed(Integer.toString(value))));
    }

    private void createAndAddOperationButton(int x, int y, Operation operation) {
        calcButtons[x][y] = this.addButton(new Button(getButtonPosX(x, y), getButtonPosY(x, y), buttonWidth, buttonHeight, new StringTextComponent(operation.getValue()), (p_214187_1_) -> operationButtonPressed(operation)));
    }

    private int getButtonPosX(int x, int y) {
        return posX + marginWidth + (buttonWidth + marginWidth) * x;
    }

    private int getButtonPosY(int x, int y) {
        return posY + marginWidth * 2 + textFieldHeight + (buttonHeight + marginWidth) * y;
    }

    @Override
    public void render(MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        fill(matrixStack, this.posX - 1, this.posY - 1, this.posX + calcWidth + 1, this.posY + calcHeight + 1, -1);
        fill(matrixStack, this.posX - 1, this.posY - topBarHeight, this.posX + calcWidth + 1, this.posY, -1);
        fill(matrixStack, this.posX, this.posY - topBarHeight + 1, this.posX + calcWidth, this.posY - 1, -1);
        this.font.drawShadow(matrixStack, "Craftculator", (float) this.posX + 1, (float) this.posY - topBarHeight + 1, topBarColor);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        this.minecraft.getTextureManager().bind(AbstractGui.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int brightness = 128;
        bufferbuilder.vertex(this.posX, this.posY + calcHeight, 0.0D).uv(0F, (float) (calcHeight) / 32.0F).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX + calcWidth, this.posY + calcHeight, 0.0D).uv((float) calcWidth / 32.0F, (float) (calcHeight) / 32.0F).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX + calcWidth, this.posY, 0.0D).uv((float) calcWidth / 32.0F, 0).color(brightness, brightness, brightness, 255).endVertex();
        bufferbuilder.vertex(this.posX, this.posY, 0.0D).uv(0, 0).color(brightness, brightness, brightness, 255).endVertex();
        tessellator.end();

        displayWidget.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
        super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    private void clearAll() {
        resultValue = "";
        firstValue = "";
        secondValue = "";
        currentOperation = Operation.EMPTY;
    }

    private void digitButtonPressed(String value) {
        if (!resultValue.equals("")) {
            clearAll();
        }
        appendCurrentValue(value);
        updateDisplay();
    }

    private void operationButtonPressed(Operation operation) {
        String currentValue = getCurrentValue();
        switch (operation) {
            case CLEAR_ENTRY:
                setCurrentValue("");
                if (!resultValue.equals("")) {
                    clearAll();
                }
                break;

            case CLEAR:
                clearAll();
                break;

            case EQUALS:
                if (secondValue.equals("")) {
                    break;
                }

                BigDecimal floatFirstValue = new BigDecimal(firstValue);
                BigDecimal floatSecondValue = new BigDecimal(secondValue);
                BigDecimal calculationOutput = currentOperation.calculate(floatFirstValue, floatSecondValue);
                resultValue = calculationOutput.toString();
                break;

            case DOT:
                if (!resultValue.equals("") && !resultValue.equals("0")) {
                    clearAll();
                    setCurrentValue("0.");
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
                    firstValue = resultValue.substring(0, resultValue.length() - 1);
                    resultValue = "";
                    secondValue = "";
                    currentOperation = Operation.EMPTY;
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
        displayWidget.setOperation(currentOperation);
        displayWidget.setResult(resultValue);
        displayWidget.setSecondValue(secondValue);
        displayWidget.setFirstValue(firstValue);
    }

    public void appendCurrentValue(String string) {
        if (getCurrentValue().equals("0") && string.equals("0")) {
            return;
        }
        if (getCurrentValue().equals("0") && !string.equals(Operation.DOT.getValue())) {
            setCurrentValue(string);
            return;
        }
        setCurrentValue(getCurrentValue() + string);
    }

    public String getCurrentValue() {
        if (currentOperation == Operation.EMPTY) {
            return firstValue;
        } else {
            return secondValue;
        }
    }

    public void setCurrentValue(String value) {
        if (currentOperation == Operation.EMPTY) {
            firstValue = value;
        } else {
            secondValue = value;
        }
    }

    @Override
    public boolean keyPressed(int keycode, int p_231046_2_, int p_231046_3_) {
        Operation operation = Operation.handleInput(keycode, p_231046_3_);
        if (operation != Operation.EMPTY) {
            operationButtonPressed(operation);
            return true;
        }

        if (CraftculatorMod.openCalcKeybind.getKey().getValue() == keycode) {
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

    private void checkOutOfBoundsAndMove() {
        if (this.posX + calcWidth > this.width) {
            this.posX = this.width - calcWidth - 1;
        }
        if (this.posY + calcHeight > this.height) {
            this.posY = this.height - calcHeight - 1;
        }

        if (this.posX < 0) {
            this.posX = 0;
        }
        if (this.posY - topBarHeight < 0) {
            this.posY = topBarHeight;
        }

        for (int x = 0; x < calcButtons.length; x++) {
            for (int y = 0; y < calcButtons[0].length; y++) {
                Button button = calcButtons[x][y];
                if (button != null) {
                    button.x = getButtonPosX(x, y);
                    button.y = getButtonPosY(x, y);
                }
            }
        }
        displayWidget.x = posX + marginWidth;
        displayWidget.y = posY + marginWidth;
    }

    @Override
    public void resize(Minecraft p_231152_1_, int p_231152_2_, int p_231152_3_) {
        this.width = p_231152_2_;
        this.height = p_231152_3_;

        checkOutOfBoundsAndMove();
    }

    @Override
    public boolean mouseDragged(double clickPosX, double clickPosY, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
        if (canDrag) {
            this.posX = (int) clickPosX + diffX;
            this.posY = (int) clickPosY + diffY;

            checkOutOfBoundsAndMove();
        }
        return super.mouseDragged(clickPosX, clickPosY, p_231045_5_, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean mouseClicked(double clickPosX, double clickPosY, int p_231044_5_) {
        if (clickPosX >= posX && clickPosX <= posX + calcWidth) {
            if (clickPosY >= posY - topBarHeight && clickPosY <= posY) {
                diffX = (int) (posX - clickPosX);
                diffY = (int) (posY - clickPosY);
                canDrag = true;
            }
        }
        return super.mouseClicked(clickPosX, clickPosY, p_231044_5_);
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        canDrag = false;
        return super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
    }

    @Override
    public void onClose() {
        CalcConfig.GENERAL.positionX.set(posX);
        CalcConfig.GENERAL.positionY.set(posY - topBarHeight);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
