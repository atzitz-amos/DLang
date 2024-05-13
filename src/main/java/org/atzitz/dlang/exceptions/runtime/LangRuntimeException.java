package org.atzitz.dlang.exceptions.runtime;

import org.atzitz.dlang.exceptions.LangException;

public class LangRuntimeException extends LangException {
    public LangRuntimeException(String msg) {
        super(msg);
    }

    public LangRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LangRuntimeException(Throwable cause) {
        super(cause);
    }

    public LangRuntimeException() {
        super();
    }
}
