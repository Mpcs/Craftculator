package com.example.examplemod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DisplayWidget extends Widget{
    private final FontRenderer font;
    private String value = "";
    private final int maxLength = 32;
    private int textColor = 14737632;
    private final Predicate<String> filter = Objects::nonNull;
    private final BiFunction<String, Integer, IReorderingProcessor> formatter = (p_195610_0_, p_195610_1_) -> IReorderingProcessor.forward(p_195610_0_, Style.EMPTY);

    public String topValue = "";


    public DisplayWidget(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, ITextComponent p_i232260_6_) {
        super(p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
        this.font = p_i232260_1_;
    }

    @Override
    public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (this.isVisible()) {
            fill(p_230431_1_, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -1);
            fill(p_230431_1_, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);

            int i2 = this.textColor;
            int j = 0;
            String s = this.font.plainSubstrByWidth(this.value.substring(0), this.getInnerWidth());
            int l = this.x + 4;
            int i1 = this.y + (this.height - 8) / 2;
            int j1 = l;

            if (!s.isEmpty()) {
                String s1 = s.substring(0, j);
                j1 = this.font.drawShadow(p_230431_1_, this.formatter.apply(s1, 0), (float)l, (float)i1-8, i2);
            }

            if (!s.isEmpty()) {
                this.font.drawShadow(p_230431_1_, s, (float)j1, (float)i1+5, i2);
            }

            String s2 = this.font.plainSubstrByWidth(this.topValue, this.getInnerWidth());
            if (!s.isEmpty()) {
                //GlStateManager._pushMatrix();
                //GlStateManager._scalef(1f, 1f, 1f);
                this.font.drawShadow(p_230431_1_, s2, (this.x)* 1.2f, (this.y - 23 + (this.height - 2) * 1.2f), 14737632);
                //GlStateManager._popMatrix();
            }
        }
    }

    public void setValue(String value) {
        if (this.filter.test(value)) {
            if (value.length() > this.maxLength) {
                this.value = value.substring(0, this.maxLength);
            } else {
                this.value = value;
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
