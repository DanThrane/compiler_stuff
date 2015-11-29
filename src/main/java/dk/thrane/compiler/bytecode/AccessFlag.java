package dk.thrane.compiler.bytecode;

/**
 * Taken from table 4.1-A from the JVM SE 8 Specification
 */
public enum AccessFlag {
    /**
     * Declared public; may be accessed from outside its package.
     */
    ACC_PUBLIC(0x0001),
    /**
     * Declared final; no subclasses allowed.
     */
    ACC_FINAL(0x0010),
    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     */
    ACC_SUPER(0x0020),
    /**
     * Is an interface, not a class.
     */
    ACC_INTERFACE(0x0200),
    /**
     * Declared abstract; must not be instantiated.
     */
    ACC_ABSTRACT(0x0400),
    /**
     * Declared synthetic; not present in the source code.
     */
    ACC_SYNTHETIC(0x1000),
    /**
     * Declared as an annotation type.
     */
    ACC_ANNOTATION(0x2000),
    /**
     * Declared as an enum type.
     */
    ACC_ENUM(0x4000);

    private final int value;

    AccessFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int combine(AccessFlag... modifiers) {
        int result = 0;
        for (AccessFlag modifier : modifiers) {
            result |= modifier.getValue();
        }
        return result;
    }
}
