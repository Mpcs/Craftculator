package com.mpcs.craftculator;

import net.minecraftforge.common.ForgeConfigSpec;

public class CalcConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {

        public final ForgeConfigSpec.ConfigValue<Integer> positionX;
        public final ForgeConfigSpec.ConfigValue<Integer> positionY;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            positionX = builder.define("positionX", 20);
            positionY = builder.define("positionY", 20);

            builder.pop();
        }
    }
}
