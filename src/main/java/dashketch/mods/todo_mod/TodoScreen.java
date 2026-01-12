package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static dashketch.mods.todo_mod.Todo_mod.log;

public class TodoScreen extends Screen {
    private EditBox inputField;

    public TodoScreen() {
        super(Component.literal("ToDo List"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TodoScreen());
    }

    private final List<TaskHitbox> hitboxes = new ArrayList<>();
    private record TaskHitbox(int index, int yTop, int yBottom) {}

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // Create the Text Input Field
        this.inputField = new EditBox(this.font, centerX - 100, 30, 150, 20, Component.literal("Add new task..."));
        this.addRenderableWidget(inputField);

        // "Add" Button
        this.addRenderableWidget(Button.builder(Component.literal("Add"), button -> {
            if (!inputField.getValue().isEmpty()) {
                ToDoList.addTask(inputField.getValue());
            }
        }).bounds(centerX + 55, 30, 45, 20).build());

        // "Mode" Button
        this.addRenderableWidget(Button.builder(Component.literal("Mode"), button -> {
            HudOverlay.switchMode();
                }).bounds(centerX - 150, 30, 45, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int currentY = 65;
        int startX = centerX - 100;
        // Adjust this to match your desired GUI width

        hitboxes.clear(); // Clear old hitboxes every frame
        List<ToDoList.Task> tasks = ToDoList.getTasks();

        for (int i = 0; i < tasks.size(); i++) {
            ToDoList.Task task = tasks.get(i);

            // 1. Determine color and prefix based on priority
            int color = task.isPriority ? 0xFFFF00 : 0xFFFFFF; // Yellow for priority, White for normal
            String prefix = task.isPriority ? "[!] " : "[ ] ";

            // 2. Create a Component and FORCED style
            // We use Style.EMPTY.withColor(color) to ensure the splitter sees this as "Rich Text"
            net.minecraft.network.chat.MutableComponent taskComponent = Component.literal(prefix + task.description)
                    .withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(color));

            if (task.isPriority) {
                taskComponent.withStyle(s -> s.withBold(true));
            }

            // 3. Perform the split with a strict pixel width (adjust 180 to fit your screen)
            int wrapWidth = 180;
            var wrappedLines = this.font.split(taskComponent, wrapWidth);

            int taskYStart = currentY;

            // 4. Render each wrapped line
            for (int lineIdx = 0; lineIdx < wrappedLines.size(); lineIdx++) {
                var line = wrappedLines.get(lineIdx);

                // Use drawString with the CharSequence from the split
                guiGraphics.drawString(this.font, line, startX, currentY, color);

                // 5. Append hint text to the last line of this specific task
                if (lineIdx == wrappedLines.size() - 1) {
                    int lineWidth = this.font.width(line);
                    guiGraphics.drawString(this.font, " §8(Click: Prioritize | Shift-Click: Delete)", startX + lineWidth, currentY, 0x808080);
                }

                currentY += 10; // Move down for the next line
            }

            // 6. Record hitboxes for the click logic (crucial for multi-line tasks)
            hitboxes.add(new TaskHitbox(i, taskYStart, currentY));
            currentY += 6; // Padding between different tasks
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        // Check if the click is within the horizontal bounds of our list
        if (mouseX >= (centerX - 100) && mouseX <= (centerX + 150)) {
            for (TaskHitbox hitbox : hitboxes) {
                if (mouseY >= hitbox.yTop && mouseY < hitbox.yBottom) {
                    if (hasShiftDown()) {
                        ToDoList.removeTask(hitbox.index);
                    } else {
                        ToDoList.togglePriority(hitbox.index);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Set to true if you want the game to pause when the list is open
    }
}