package com.mpcs.craftculator;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;
import java.util.function.Predicate;

public class DisplayWidget extends Widget {
    private final FontRenderer font;
    private final int maxLength = 32;
    private final int textColor = 14737632;
    private final Predicate<String> filter = Objects::nonNull;
    private String firstValue = "";
    private String secondValue = "";
    private String resultValue = "";
    private Operation operation = Operation.EMPTY;

    public DisplayWidget(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
        super(p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        this.font = p_i232260_1_;
    }

    @Override
    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (this.isVisible()) {
            fill(p_230431_1_, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -1);
            fill(p_230431_1_, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);

            String topString = "";
            if (operation != Operation.EMPTY) {
                topString = firstValue + operation.getValue();
                if (!resultValue.equals("")) {
                    topString += secondValue + "=";
                }
            }

            topString = this.font.plainSubstrByWidth(topString, this.getInnerWidth());
            if (!topString.isEmpty()) {
                int y = this.y + (this.height - 8) / 2;
                this.font.drawShadow(p_230431_1_, topString, (float) this.x + 4, (float) y - 4, this.textColor);
            }


            String bottomString = operation == Operation.EMPTY ? firstValue : secondValue;
            if (!resultValue.equals("")) {
                bottomString = resultValue;
            }
            bottomString = this.font.plainSubstrByWidth(bottomString, this.getInnerWidth());
            if (!bottomString.isEmpty()) {
                int y = this.y + (this.height - 8) / 2;
                this.font.drawShadow(p_230431_1_, bottomString, (float) this.x + 4, (float) y + 5, this.textColor);
            }


        }
    }

    public void setFirstValue(String value) {
        if (this.filter.test(value)) {
            if (value.length() > this.maxLength) {
                this.firstValue = value.substring(0, this.maxLength);
            } else {
                this.firstValue = value;
            }
        }
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setSecondValue(String value) {
        if (this.filter.test(value)) {
            if (value.length() > this.maxLength) {
                this.secondValue = value.substring(0, this.maxLength);
            } else {
                this.secondValue = value;
            }
        }
    }

    public void setResult(String value) {
        if (this.filter.test(value)) {
            if (value.length() > this.maxLength) {
                this.resultValue = value.substring(0, this.maxLength);
            } else {
                this.resultValue = value;
            }
        }
    }


    private boolean isBordered() {
        return true;
    }

    private boolean isVisible() {
        return true;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }
}
