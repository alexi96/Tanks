package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MappedSettings<S> {

    private static final HashMap<Object, MappedSettings> INSTANCES = new HashMap<>();

    private final TreeMap<String, S> settings = new TreeMap<>();
    private File file;

    private MappedSettings() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void mapSetting(String id, S set) {
        this.settings.put(id, set);
    }

    public S findSetting(String id) {
        return this.settings.get(id);
    }

    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.file))) {
            out.writeObject(this.settings);
        } catch (IOException ex) {
            Logger.getLogger(MappedSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean open() {
        return this.open(true);
    }

    public boolean open(boolean reset) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(this.file))) {
            TreeMap<String, S> set = (TreeMap<String, S>) in.readObject();
            if (reset) {
                this.settings.clear();
            }
            this.settings.putAll(set);
            return true;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MappedSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static <M> MappedSettings<M> getInstance(Object obj) {
        if (!MappedSettings.INSTANCES.containsKey(obj)) {
            MappedSettings<M> t = new MappedSettings<>();
            MappedSettings.INSTANCES.put(obj, t);
        }
        return MappedSettings.INSTANCES.get(obj);
    }
}
