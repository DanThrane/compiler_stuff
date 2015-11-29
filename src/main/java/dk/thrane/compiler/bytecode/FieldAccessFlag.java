package dk.thrane.compiler.bytecode;

/**
 * Taken from table 4.5-A from the JVM SE 8 Specification
 */
public enum FieldAccessFlag {
    /**
     * Declared public; may be accessed from outside its package.
     */
    ACC_PUBLIC(0x0001),
    /**
     * Declared private; usable only within the defining class.
     */
    ACC_PRIVATE(0x0002),
    /**
     * Declared protected; may be accessed within subclasses.
     */
    ACC_PROTECTED(0x0004),
    /**
     * Declared static.
     */
    ACC_STATIC(0x0008),
    /**
     * Declared final; never directly assigned to after object construction (JLS §17.5).
     */
    ACC_FINAL(0x0010),
    /**
     * Declared volatile; cannot be cached.
     */
    ACC_VOLATILE(0x0040),
    /**
     * Declared transient; not written or read by a persistent object manager.
     */
    ACC_TRANSIENT(0x0080),
    /**
     * Declared synthetic; not present in the source code.
     */
    ACC_SYNTHETIC(0x1000),
    /**
     * Declared as an element of an enum.
     */
    ACC_ENUM(0x4000);

    private final int value;

    FieldAccessFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static int combine(FieldAccessFlag... modifiers) {
        int result = 0;
        for (FieldAccessFlag modifier : modifiers) {
            result |= modifier.getValue();
        }
        return result;
    }
}
