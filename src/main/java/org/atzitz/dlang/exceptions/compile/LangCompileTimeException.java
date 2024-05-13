package org.atzitz.dlang.exceptions.compile;

import org.atzitz.dlang.exceptions.LangException;

public class LangCompileTimeException extends LangException {
    public LangCompileTimeException() {
        super();
    }

    public LangCompileTimeException(String msg) {
        super(msg);
    }

    public LangCompileTimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LangCompileTimeException(Throwable cause) {
        super(cause);
    }
}
