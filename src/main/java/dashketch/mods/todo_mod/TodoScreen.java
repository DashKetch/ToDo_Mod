package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TodoScreen extends Screen {
    private EditBox inputField;
    private final List<TaskHitbox> hitboxes = new ArrayList<>();
    private record TaskHitbox(int index, int yTop, int yBottom) {}

    public TodoScreen() {
        super(Component.literal("ToDo List"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TodoScreen());
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        this.inputField = new EditBox(this.font, centerX - 100, 30, 150, 20, Component.literal("Add new task..."));
        this.addRenderableWidget(inputField);

        this.addRenderableWidget(Button.builder(Component.literal("Add"), button -> {
            if (!inputField.getValue().isEmpty()) {
                ToDoList.addTask(inputField.getValue());
                inputField.setValue(""); // Clear input after adding
            }
        }).bounds(centerX + 55, 30, 45, 20).build());

        // Mode switch button
        this.addRenderableWidget(Button.builder(Component.literal("Mode"), button -> HudOverlay.switchMode())
                .bounds(centerX - 150, 30, 45, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int startX = centerX - 100;

        // 1. RENDER GLOBAL CONTROLS TIP (Only once at the top)
        String globalTip = "§8Click: Prio | Shift: Del | Ctrl: Set Icon";
        guiGraphics.drawString(this.font, globalTip, startX, 55, 0x808080);

        int currentY = 65;
        hitboxes.clear();
        List<ToDoList.Task> tasks = ToDoList.getTasks();

        for (int i = 0; i < tasks.size(); i++) {
            ToDoList.Task task = tasks.get(i);
            int color = task.isPriority ? 0xFFFF00 : 0xFFFFFF;
            String prefix = task.isPriority ? "[!] " : "[ ] ";

            // 2. CLEAN TEXT COMPONENT (No extra indicators)
            net.minecraft.network.chat.MutableComponent taskComponent = Component.literal(prefix + task.description)
                    .withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(color));

            if (task.isPriority) {
                taskComponent.withStyle(s -> s.withBold(true));
            }

            int wrapWidth = 200;
            var wrappedLines = this.font.split(taskComponent, wrapWidth);
            int taskYStart = currentY;

            for (var line : wrappedLines) {
                guiGraphics.drawString(this.font, line, startX, currentY, color);
                currentY += 10;
            }

            // Record hitbox for clicking logic
            hitboxes.add(new TaskHitbox(i, taskYStart, currentY));
            currentY += 6; // Space between tasks
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        if (mouseX >= (centerX - 100) && mouseX <= (centerX + 150)) {
            for (TaskHitbox hitbox : hitboxes) {
                if (mouseY >= hitbox.yTop && mouseY < hitbox.yBottom) {

                    // --- CONTROL CLICK: SET ICON ---
                    if (hasControlDown()) {
                        ItemStack held = Minecraft.getInstance().player.getMainHandItem();
                        // Get the ID (e.g., "minecraft:apple")
                        String id = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
                        ToDoList.setIcon(hitbox.index, id);
                        return true;
                    }
                    // --- SHIFT CLICK: DELETE ---
                    else if (hasShiftDown()) {
                        ToDoList.removeTask(hitbox.index);
                    }
                    // --- CLICK: TOGGLE PRIORITY ---
                    else {
                        ToDoList.togglePriority(hitbox.index);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}