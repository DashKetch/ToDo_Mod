package dashketch.mods.todo_mod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "todo_mod", bus = EventBusSubscriber.Bus.MOD)
public class KeyInit {

    public static final KeyMapping OPEN_TODO_KEY = new KeyMapping(
            "key.todomod.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "category.todomod"
    );

    public static final KeyMapping TOGGLE_HUD_KEY = new KeyMapping(
            "key.todomod.toggle_hud",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.todomod"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_TODO_KEY);
        event.register(TOGGLE_HUD_KEY);
    }
}