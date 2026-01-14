package dashketch.mods.todo_mod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoList {
    private static final File SAVE_FILE = new File(Minecraft.getInstance().gameDirectory, "todolist.json");
    private static final Gson GSON = new Gson();

    private static List<Task> tasks = new ArrayList<>();
    public static boolean hudVisible = true;

    public static class Task {
        public String description;
        public boolean isPriority;
        //Stores the registry name of the item (e.g., "minecraft:diamond_sword")
        public String iconID;

        public Task(String description, boolean isPriority) {
            this.description = description;
            this.isPriority = isPriority;
            this.iconID = ModConfigs.ICON_DEFAULT.get();
        }
    }

    public static List<Task> getTasks() {
        return tasks;
    }

    public static void addTask(String desc) {
        tasks.add(new Task(desc, false));
        save();
        HudOverlay.taskAmount = HudOverlay.taskAmount + 1;
    }

    public static void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            save();
            HudOverlay.taskAmount = HudOverlay.taskAmount - 1;
        }
    }

    public static void togglePriority(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task t = tasks.get(index);
            t.isPriority = !t.isPriority;
            save();
        }
    }

    // NEW: Updates the icon for a specific task
    public static void setIcon(int index, String itemID) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).iconID = itemID;
            save();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(SAVE_FILE)) {
            GSON.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!SAVE_FILE.exists()) return;
        try (Reader reader = new FileReader(SAVE_FILE)) {
            tasks = GSON.fromJson(reader, new TypeToken<List<Task>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}