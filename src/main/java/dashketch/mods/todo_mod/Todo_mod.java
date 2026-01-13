package dashketch.mods.todo_mod;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("todo_mod")
public class Todo_mod {
    public static final Logger log = LoggerFactory.getLogger(Todo_mod.class);
    static int black = 0x99000000;

    // NeoForge injects ModContainer automatically here
    public Todo_mod(ModContainer container) {
        // This line references ModConfigs.SPEC
        container.registerConfig(ModConfig.Type.CLIENT, ModConfigs.SPEC);

        ToDoList.load();
    }
}