package dashketch.mods.todo_mod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static dashketch.mods.todo_mod.HudOverlay.taskAmount;

public class ToDoList {
    private static final File SAVE_FILE = new File(Minecraft.getInstance().gameDirectory, "todolist.json");
    private static final Gson GSON = new Gson();

    // The list of tasks
    private static List<Task> tasks = new ArrayList<>();
    // HUD visibility toggle
    public static boolean hudVisible = true;

    public static class Task {
        public String description;
        public boolean isPriority;

        public Task(String description, boolean isPriority) {
            this.description = description;
            this.isPriority = isPriority;
        }
    }

    public static List<Task> getTasks() {
        return tasks;
    }

    public static void addTask(String desc) {
        tasks.add(new Task(desc, false));
        save();
        taskAmount = taskAmount + 1;
    }

    public static void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            save();
            taskAmount = taskAmount - 1;
        }
    }

    public static void togglePriority(int index) {
        if (index >= 0 && index < tasks.size()) {
            Task t = tasks.get(index);
            t.isPriority = !t.isPriority;
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
