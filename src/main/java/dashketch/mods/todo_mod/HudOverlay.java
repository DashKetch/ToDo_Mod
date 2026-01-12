package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static dashketch.mods.todo_mod.Todo_mod.black;
import static dashketch.mods.todo_mod.Todo_mod.log;

@EventBusSubscriber(modid = "todo_mod", bus = EventBusSubscriber.Bus.MOD)
public class HudOverlay {

    public static int taskAmount = 0;

    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CHAT, ResourceLocation.fromNamespaceAndPath("todo_mod", "todo_overlay"),
                (guiGraphics, deltaTracker) -> {
                    if (!ToDoList.hudVisible) return;

                    Minecraft mc = Minecraft.getInstance();
                    if (mc.options.hideGui || ToDoList.getTasks().isEmpty()) return;

                    int x = 10;
                    int y = 10;
                    int padding = 4;
                    int lineheight = 10;

                    int maxWidth = mc.font.width("Priority Tasks:");
                    int priorityCount = 0;

                    for (ToDoList.Task task : ToDoList.getTasks()) {
                        if (task.isPriority) {
                            priorityCount++;
                            // Add 8 pixels to account for the "- " prefix
                            int taskWidth = mc.font.width("- " + task.description);
                            if (taskWidth > maxWidth) {
                                maxWidth = taskWidth;
                            }
                        }
                    }

                    if (priorityCount == 0) return;

                    // 2. Draw the Background Box
                    // width = maxWidth + padding on both sides
                    // height = (title + tasks) * lineheight + padding
                    int bgWidth = maxWidth + (padding * 2);
                    int bgHeight = (priorityCount + 1) * lineheight + padding;

                    guiGraphics.fill(x - padding, y - padding, x + bgWidth - padding, y + bgHeight - padding, black); // Using ARGB for transparency

                    // 3. Draw the Text on top of the box
                    guiGraphics.drawString(mc.font, "Priority Tasks:", x, y, 0xFFFFFF);
                    int currentY = y + lineheight;

                    for (ToDoList.Task task : ToDoList.getTasks()) {
                        if (task.isPriority) {
                            guiGraphics.drawString(mc.font, "- " + task.description, x, currentY, 0xFFFF00);
                            currentY += lineheight;
                        }
                    }
                }
        );
    }

    public static void switchMode() {
        log.info("Mode Switch");
        if (ModConfigs.LIST_MODE.get()) {
            ModConfigs.LIST_MODE.set(false);
        } else ModConfigs.LIST_MODE.set(true);

    }
}