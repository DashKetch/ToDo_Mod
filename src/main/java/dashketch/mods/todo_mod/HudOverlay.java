package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
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

                    // Count priority tasks to decide if we should render anything
                    boolean hasPriority = ToDoList.getTasks().stream().anyMatch(t -> t.isPriority);
                    if (!hasPriority) return;

                    int x = 10;
                    int y = 10;

                    if (ModConfigs.LIST_MODE.get()) {
                        renderIconMode(guiGraphics, mc, x, y);
                    } else {
                        renderListMode(guiGraphics, mc, x, y);
                    }
                }
        );
    }

    private static void renderIconMode(net.minecraft.client.gui.GuiGraphics guiGraphics, Minecraft mc, int x, int y) {
        // 1. Render the Header first so the user knows what the icons represent
        guiGraphics.drawString(mc.font, "Priority Tasks:", x, y, 0xFFFFFF);

        // 2. Adjust the starting Y position for icons so they don't overlap the text
        // Adding 12 pixels moves them safely to the next line
        int iconY = y + 12;
        int iconX = x;

        for (ToDoList.Task task : ToDoList.getTasks()) {
            if (task.isPriority) {
                // Get the Item ID, default to paper if null
                String id = (task.iconID == null) ? "minecraft:paper" : task.iconID;

                try {
                    // Parse ID and render the item
                    ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)));
                    guiGraphics.renderFakeItem(stack, iconX, iconY);

                    // 3. Move X to the right for the next icon (16px width + 2px padding)
                    iconX += 18;

                    // Optional: If you have MANY icons, you could add logic here
                    // to wrap to a new line if iconX > some limit
                } catch (Exception e) {
                    // Fallback in case a saved ID becomes invalid (e.g. a mod was removed)
                    guiGraphics.renderFakeItem(new ItemStack(Items.BARRIER), iconX, iconY);
                    iconX += 18;
                }
            }
        }
    }

    private static void renderListMode(net.minecraft.client.gui.GuiGraphics guiGraphics, Minecraft mc, int x, int y) {
        int padding = 4;
        int lineheight = 10;

        int priorityCount = 0;
        int maxWidth = mc.font.width("Priority Tasks:");

        for (ToDoList.Task task : ToDoList.getTasks()) {
            if (task.isPriority) {
                priorityCount++;
                int taskWidth = mc.font.width("- " + task.description);
                if (taskWidth > maxWidth) maxWidth = taskWidth;
            }
        }

        int bgWidth = maxWidth + (padding * 2);
        int bgHeight = (priorityCount + 1) * lineheight + padding;

        guiGraphics.fill(x - padding, y - padding, x + bgWidth - padding, y + bgHeight - padding, black);
        guiGraphics.drawString(mc.font, "Priority Tasks:", x, y, 0xFFFFFF);

        int currentY = y + lineheight;

        for (ToDoList.Task task : ToDoList.getTasks()) {
            if (task.isPriority) {
                guiGraphics.drawString(mc.font, "- " + task.description, x, currentY, 0xFFFF00);
                currentY += lineheight;
            }
        }
    }

    public static void switchMode() {
        log.info("Mode Switch");
        if (ModConfigs.LIST_MODE.get()) {
            ModConfigs.LIST_MODE.set(false);
        } else {
            ModConfigs.LIST_MODE.set(true);
        }
    }
}