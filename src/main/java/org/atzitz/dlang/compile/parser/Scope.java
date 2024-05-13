package org.atzitz.dlang.compile.parser;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class Scope {
    public final Type type;
    public final String name;
    public final Scope parent;
    public final Map<String, Scope> children = new HashMap<>();

    private @Getter List<String> locals = new ArrayList<>();
    private @Getter List<String> params = new ArrayList<>();
    private @Getter List<String> localObjects = new ArrayList<>();

    public Scope(Type type, String name, Scope parent) {
        this.type = type;
        this.name = name;
        this.parent = parent;
    }

    public void addLocal(String name) {
        locals.add(name);
    }

    public void addParam(String name) {
        params.add(name);
    }

    public void addLocalObject(String name) {
        localObjects.add(name);
    }

    public boolean isLocal(String name) {
        return locals.contains(name);
    }

    public void attachChild(Scope child) {
        children.put(child.name, child);
    }

    public boolean isParam(String name) {
        return params.contains(name);
    }

    public boolean isLocalObj(String name) {
        return localObjects.contains(name);
    }

    public int label(String name) {
        if (isLocal(name)) {
            return locals.indexOf(name);
        } else if (isParam(name)) {
            return params.indexOf(name);
        } else if (isLocalObj(name)) {
            return localObjects.indexOf(name) + locals.size();
        } else {
            return 0;
        }
    }


    public enum Type {
        FILE, CLASS, FUNCTION, INTERNAL
    }
}
