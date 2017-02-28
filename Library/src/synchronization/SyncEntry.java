package synchronization;

import java.io.Serializable;

@Deprecated
public class SyncEntry implements Comparable<SyncEntry>, Serializable {

    private Synchronizer synch;
    private String command;

    public SyncEntry(Synchronizer synch, String command) {
        this.synch = synch;
        this.command = command;
    }

    public Synchronizer getSynch() {
        return synch;
    }

    public void setSynch(Synchronizer synch) {
        this.synch = synch;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public int compareTo(SyncEntry o) {
        int cmp = this.synch.compareTo(o.synch);
        if (cmp == 0) {
            return this.command.compareTo(o.command);
        }
        return cmp;
    }
}
