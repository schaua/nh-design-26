package com.neueda.leap.sprint5;

// A CHECKED exception: extends Exception, not RuntimeException. The compiler forces
// every caller to either catch it or declare "throws MalformedLoanException" -
// there's no Python equivalent of this compile-time enforcement; Python's
// try/except only catches at runtime, whatever you choose to catch.
public class MalformedLoanException extends Exception {

    public MalformedLoanException(String message) {
        super(message);
    }
}
