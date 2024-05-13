package org.atzitz.dlang.compile;

public record Location(int startLine, int startColumn, int endLine, int endColumn) {
    public static Location of(int startLine, int startColumn, int endLine, int endColumn) {
        return new Location(startLine, startColumn, endLine, endColumn);
    }

    public static Location of(Location begin, Location end) {
        return new Location(begin.startLine, begin.startColumn, end.endLine, end.endColumn);
    }

    public static Location of(Location loc) {
        return new Location(loc.startLine, loc.startColumn, loc.endLine, loc.endColumn);
    }

    @Override
    public String toString() {
        return STR."\{startLine};\{startColumn}->\{endLine};\{endColumn}";
    }
}
