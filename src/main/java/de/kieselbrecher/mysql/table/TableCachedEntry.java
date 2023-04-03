package de.kieselbrecher.mysql.table;

import java.util.ArrayList;
import java.util.List;

public class TableCachedEntry {

    private final long time;
    private final List<TableEntry> entries;

    public TableCachedEntry(long time) {
        this.time = time;
        this.entries = new ArrayList<>();
    }

    public long getTime() {
        return time;
    }

    public List<TableEntry> getEntries() {
        return entries;
    }
}
