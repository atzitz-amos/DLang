package org.atzitz.dlang.compile;


import java.util.ArrayList;
import java.util.Collection;

public class Constants {
    public static final _Keywords KEYWORDS = new _Keywords();
    public static final _Operators OPERATORS = new _Operators();

    private static class ConstantsHolder extends ArrayList<String> {
        protected final static Collection<String> _keywords = new ArrayList<>();

        public ConstantsHolder() {
            super(_keywords);
        }

        protected static String _define(String s) {
            _keywords.add(s);
            return s;
        }
    }

    public static final class _Keywords extends ConstantsHolder {
        public static String IF = _define("if");
        public static String ELSE = _define("else");

        public static String WHILE = _define("while");

        public static String CLASS = _define("class");
        public static String NEW = _define("new");
        public static String RETURN = _define("return");

        public static String TRUE = _define("true");
        public static String FALSE = _define("false");
    }

    public static final class _Operators extends ConstantsHolder {
        public static String EQUALS = _define("=");
        public static String DOUBLE_EQUALS = _define("==");

        public static String PLUS = _define("+");
        public static String MINUS = _define("-");
        public static String MULTIPLY = _define("*");
        public static String DIVIDE = _define("/");
        public static String MODULO = _define("%");
    }
}
