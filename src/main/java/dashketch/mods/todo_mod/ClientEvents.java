package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "todo_mod", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        // Check Open GUI Key
        while (KeyInit.OPEN_TODO_KEY.consumeClick()) {
            TodoScreen.open();
        }

        // Check Toggle HUD Key
        while (KeyInit.TOGGLE_HUD_KEY.consumeClick()) {
            ToDoList.hudVisible = !ToDoList.hudVisible;
        }
    }
}