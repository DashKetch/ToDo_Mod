package dashketch.mods.todo_mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("todo_mod")
public class Todo_mod {
    public static final Logger log = LoggerFactory.getLogger(Todo_mod.class);

    static int black = 0x99000000;

    public Todo_mod(IEventBus modEventBus) {
        ToDoList.load();
    }
}