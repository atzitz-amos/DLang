package org.atzitz.dlang.compile.parser;

import lombok.Getter;
import org.atzitz.dlang.exceptions.compile.LangCompileTimeException;

import java.util.Stack;

@Getter
public class ScopeVisitor {
    private final Stack<Scope> scopes = new Stack<>();
    private final Scope root;
    private final Stack<Scope> history = new Stack<>();
    private Scope current;

    public ScopeVisitor() {
        root = new Scope(Scope.Type.FILE, "_file_", null);
        current = root;
        scopes.add(root);
    }

    public ScopeVisitor(Scope root) {
        this.root = root;
        this.current = root;
    }

    public Scope open(Scope.Type type, String name) {
        Scope sc = new Scope(type, name, scopes.peek());
        scopes.add(sc);
        current.attachChild(sc);
        current = sc;
        return sc;
    }

    public Scope close() {
        Scope sc = scopes.size() == 1 ? scopes.peek() : scopes.pop();
        current = sc.parent;
        return sc;
    }

    public boolean isGlobal() {
        return scopes.size() == 1;
    }

    public void addLocal(String name) {
        current.addLocal(name);
    }

    public void addParam(String name) {
        current.addParam(name);
    }


    public Scope visit(String name) {
        current = current.children.get(name);
        return current;
    }

    public Scope unvisit() {
        current = current.parent;
        return current;
    }

    public void revert() {
        current = history.pop();
    }

    public Scope navigateTo(String name) {
        history.add(current);
        return navigateTo(name, current);
    }

    private Scope navigateTo(String name, Scope curr) {
        if (curr.children.containsKey(name)) {
            current = curr.children.get(name);
            return current;
        }
        if (curr.parent == null) {
            throw new LangCompileTimeException(STR."Scope \{name} wasn't found");
        }
        return navigateTo(name, curr.parent);
    }


    public boolean isLocal(String name) {
        return current.isLocal(name);
    }

    public PropertyLevel getPropertyLevel(String name) {
        Scope curr = getLocalAccessor(name, current);
        if (curr.isParam(name)) {
            return PropertyLevel.PARAM;
        }
        return switch (curr.type) {
            case FUNCTION, INTERNAL -> PropertyLevel.LOCAL;
            case CLASS -> PropertyLevel.CLASS_LEVEL;
            case FILE -> PropertyLevel.GLOBAL;
        };
    }

    public int label(String name) {
        return getLocalAccessor(name, current).label(name);
    }

    public int labelObj(String name) {
        Scope curr = getLocalAccessor(name, current);
        if (!curr.getLocalObjects().contains(name)) {
            throw new LangCompileTimeException(STR."Property \{name} not found in scope");
        }
        int index = curr.getLocals().size();
        for (int i = 0; i < curr.getLocalObjects().size(); i++) {
            if (curr.getLocalObjects().get(i).equals(name)) {
                return index;
            }
            navigateTo(curr.getLocalObjects().get(i));
            index += current.getLocals().size() + 1;
            revert();
        }
        return -1;
    }

    private Scope getLocalAccessor(String name, Scope curr) {
        if (curr.isLocal(name) || curr.isParam(name) || curr.isLocalObj(name)) {
            return curr;
        }
        if (curr.parent == null) {
            throw new LangCompileTimeException(STR."Property \{name} not found in scope");
        }
        return getLocalAccessor(name, curr.parent);
    }

    public ScopeVisitor visitor() {
        return new ScopeVisitor(root);
    }

    public void addLocalObject(String name) {
        current.addLocalObject(name);
    }


    public enum PropertyLevel {
        GLOBAL, CLASS_LEVEL, PARAM, LOCAL;
    }
}
