package dashketch.mods.todo_mod;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LIST_MODE;
    public static final ModConfigSpec.ConfigValue<String> ICON_DEFAULT;

    static {
        BUILDER.push("ToDo List Settings");

        LIST_MODE = BUILDER.define("icon_list_mode", false);
        BUILDER.comment("Default icon format must be modname:item for modded items, and minecraft:item for ");
        BUILDER.comment("vanilla items. A good way to find the correct format is to use the /give command in creative");
        ICON_DEFAULT = BUILDER.define("default_icon", "minecraft:paper");
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
