package io.zbx.repositories;

import io.zbx.dto.Session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryDB {

    private static volatile MemoryDB memoryDb;

    private List<Session> data;

    private MemoryDB() {
        data = new CopyOnWriteArrayList<>();
    }

    public static MemoryDB instance() {

        if (memoryDb == null) {
            memoryDb = new MemoryDB();
        }

        return memoryDb;
    }

    public void add(Session session) {
        data.add(session);
    }

    public List<Session> all() {
        return data;
    }

    public int size() {
        return this.data.size();
    }
}

