package dashketch.mods.todo_mod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.checkerframework.framework.qual.AnnotatedFor;

@Mod("todo_mod")
public class Todo_mod {

    static int black = 0x99000000;

    public Todo_mod(IEventBus modEventBus) {
        ToDoList.load();
    }
}