package dashketch.mods.todo_mod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TodoScreen extends Screen {
    private EditBox inputField;
    private static final int ITEM_HEIGHT = 20;

    public TodoScreen() {
        super(Component.literal("ToDo List"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TodoScreen());
    }

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

        this.addRenderableWidget(Button.builder(Component.literal("Close"), b -> this.onClose())
                .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, 10, 0xFFFFFF);

        List<ToDoList.Task> tasks = ToDoList.getTasks();
        int currentY = 65;
        int startX = centerX - 100;

        // Define how wide a task is allowed to be before wrapping (e.g., 200 pixels)
        int maxTextWidth = 200;

        for (ToDoList.Task task : tasks) {
            int color = task.isPriority ? 0xFFFF00 : 0xFFFFFF;
            String prefix = task.isPriority ? "§l[!] " : "[ ] ";

            // 1. Wrap the description text
            // This returns a list of lines that fit within maxTextWidth
            List<net.minecraft.util.FormattedCharSequence> lines =
                    this.font.split(Component.literal(prefix + task.description), maxTextWidth);

            // 2. Draw each wrapped line
            for (int i = 0; i < lines.size(); i++) {
                guiGraphics.drawString(this.font, lines.get(i), startX, currentY, color);

                // Only draw the hint text on the VERY LAST line of the wrapped task
                if (i == lines.size() - 1) {
                    int lastLineWidth = this.font.width(lines.get(i));
                    String hintText = " §8(Click: Priority | Shift-Click: Delete)";
                    guiGraphics.drawString(this.font, hintText, startX + lastLineWidth, currentY, 0xFFFFFF);
                }

                currentY += 10; // Move down for the next line of the SAME task
            }

            currentY += 5; // Extra gap between different tasks
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int yStart = 65;
        int listSize = ToDoList.getTasks().size();

        // Check if the user clicked within the vertical bounds of the list
        if (mouseY >= yStart && mouseY < yStart + (listSize * ITEM_HEIGHT)) {
            int clickedIndex = (int)((mouseY - yStart) / ITEM_HEIGHT);

            if (hasShiftDown()) {
                ToDoList.removeTask(clickedIndex);
            } else {
                ToDoList.togglePriority(clickedIndex);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Set to true if you want the game to pause when the list is open
    }
}