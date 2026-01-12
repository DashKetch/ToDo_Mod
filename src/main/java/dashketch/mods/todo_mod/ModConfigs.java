package dashketch.mods.todo_mod;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LIST_MODE;

    static {
        BUILDER.push("ToDo List Settings");

        LIST_MODE = BUILDER.define("list_mode", false);
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
