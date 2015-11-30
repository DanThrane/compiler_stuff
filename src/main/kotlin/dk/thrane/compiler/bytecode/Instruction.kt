package dk.thrane.compiler.bytecode

import java.io.DataOutputStream

class Instruction(val opcode: Int, val body: ((DataOutputStream) -> (Unit))? = null) {
    fun write(out: DataOutputStream) {
        out.writeByte(opcode)
        if (body != null) body!!(out)
    }

    companion object {
        // Ripped directly from https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings

        /**
         * arrayref, index → value
         * load onto the stack a reference from an array
         */
        val aaload = Instruction(0x32)

        /**
         * arrayref, index, value →
         * store into a reference in an array
         */
        val aastore = Instruction(0x53)

        /**
         * → null
         * push a null reference onto the stack
         */
        val aconst_null = Instruction(0x01)

        /**
         * → objectref
         * load a reference onto the stack from local variable 0
         */
        val aload_0 = Instruction(0x2a)

        /**
         * → objectref
         * load a reference onto the stack from local variable 1
         */
        val aload_1 = Instruction(0x2b)

        /**
         * → objectref
         * load a reference onto the stack from local variable 2
         */
        val aload_2 = Instruction(0x2c)

        /**
         * → objectref
         * load a reference onto the stack from local variable 3
         */
        val aload_3 = Instruction(0x2d)

        /**
         * objectref → [empty]
         * return a reference from a method
         */
        val areturn = Instruction(0xb0)

        /**
         * arrayref → length
         * get the length of an array
         */
        val arraylength = Instruction(0xbe)

        /**
         * objectref →
         * store a reference into local variable 0
         */
        val astore_0 = Instruction(0x4b)

        /**
         * objectref →
         * store a reference into local variable 1
         */
        val astore_1 = Instruction(0x4c)

        /**
         * objectref →
         * store a reference into local variable 2
         */
        val astore_2 = Instruction(0x4d)

        /**
         * objectref →
         * store a reference into local variable 3
         */
        val astore_3 = Instruction(0x4e)

        /**
         * objectref → [empty], objectref
         * throws an error or exception (notice that the rest of the stack is cleared, leaving only a reference to the Throwable)
         */
        val athrow = Instruction(0xbf)

        /**
         * arrayref, index → value
         * load a byte or Boolean value from an array
         */
        val baload = Instruction(0x33)

        /**
         * arrayref, index, value →
         * store a byte or Boolean value into an array
         */
        val bastore = Instruction(0x54)

        /**
         *
         * reserved for breakpoints in Java debuggers; should not appear in any class file
         */
        val breakpoint = Instruction(0xca)

        /**
         * arrayref, index → value
         * load a char from an array
         */
        val caload = Instruction(0x34)

        /**
         * arrayref, index, value →
         * store a char into an array
         */
        val castore = Instruction(0x55)

        /**
         * value → result
         * convert a double to a float
         */
        val d2f = Instruction(0x90)

        /**
         * value → result
         * convert a double to an int
         */
        val d2i = Instruction(0x8e)

        /**
         * value → result
         * convert a double to a long
         */
        val d2l = Instruction(0x8f)

        /**
         * value1, value2 → result
         * add two doubles
         */
        val dadd = Instruction(0x63)

        /**
         * arrayref, index → value
         * load a double from an array
         */
        val daload = Instruction(0x31)

        /**
         * arrayref, index, value →
         * store a double into an array
         */
        val dastore = Instruction(0x52)

        /**
         * value1, value2 → result
         * compare two doubles
         */
        val dcmpg = Instruction(0x98)

        /**
         * value1, value2 → result
         * compare two doubles
         */
        val dcmpl = Instruction(0x97)

        /**
         * → 0.0
         * push the constant 0.0 onto the stack
         */
        val dconst_0 = Instruction(0x0e)

        /**
         * → 1.0
         * push the constant 1.0 onto the stack
         */
        val dconst_1 = Instruction(0x0f)

        /**
         * value1, value2 → result
         * divide two doubles
         */
        val ddiv = Instruction(0x6f)

        /**
         * → value
         * load a double from local variable 0
         */
        val dload_0 = Instruction(0x26)

        /**
         * → value
         * load a double from local variable 1
         */
        val dload_1 = Instruction(0x27)

        /**
         * → value
         * load a double from local variable 2
         */
        val dload_2 = Instruction(0x28)

        /**
         * → value
         * load a double from local variable 3
         */
        val dload_3 = Instruction(0x29)

        /**
         * value1, value2 → result
         * multiply two doubles
         */
        val dmul = Instruction(0x6b)

        /**
         * value → result
         * negate a double
         */
        val dneg = Instruction(0x77)

        /**
         * value1, value2 → result
         * get the remainder from a division between two doubles
         */
        val drem = Instruction(0x73)

        /**
         * value → [empty]
         * return a double from a method
         */
        val dreturn = Instruction(0xaf)

        /**
         * value →
         * store a double into local variable 0
         */
        val dstore_0 = Instruction(0x47)

        /**
         * value →
         * store a double into local variable 1
         */
        val dstore_1 = Instruction(0x48)

        /**
         * value →
         * store a double into local variable 2
         */
        val dstore_2 = Instruction(0x49)

        /**
         * value →
         * store a double into local variable 3
         */
        val dstore_3 = Instruction(0x4a)

        /**
         * value1, value2 → result
         * subtract a double from another
         */
        val dsub = Instruction(0x67)

        /**
         * value → value, value
         * duplicate the value on top of the stack
         */
        val dup = Instruction(0x59)

        /**
         * value2, value1 → value1, value2, value1
         * insert a copy of the top value into the stack two values from the top. value1 and value2 must not be of the type double or long.
         */
        val dup_x1 = Instruction(0x5a)

        /**
         * value3, value2, value1 → value1, value3, value2, value1
         * insert a copy of the top value into the stack two (if value2 is double or long it takes up the entry of value3, too) or three values (if value2 is neither double nor long) from the top
         */
        val dup_x2 = Instruction(0x5b)

        /**
         * {value2, value1} → {value2, value1}, {value2, value1}
         * duplicate top two stack words (two values, if value1 is not double nor long; a single value, if value1 is double or long)
         */
        val dup2 = Instruction(0x5c)

        /**
         * value3, {value2, value1} → {value2, value1}, value3, {value2, value1}
         * duplicate two words and insert beneath third word (see explanation above)
         */
        val dup2_x1 = Instruction(0x5d)

        /**
         * {value4, value3}, {value2, value1} → {value2, value1}, {value4, value3}, {value2, value1}
         * duplicate two words and insert beneath fourth word
         */
        val dup2_x2 = Instruction(0x5e)

        /**
         * value → result
         * convert a float to a double
         */
        val f2d = Instruction(0x8d)

        /**
         * value → result
         * convert a float to an int
         */
        val f2i = Instruction(0x8b)

        /**
         * value → result
         * convert a float to a long
         */
        val f2l = Instruction(0x8c)

        /**
         * value1, value2 → result
         * add two floats
         */
        val fadd = Instruction(0x62)

        /**
         * arrayref, index → value
         * load a float from an array
         */
        val faload = Instruction(0x30)

        /**
         * arrayref, index, value →
         * store a float in an array
         */
        val fastore = Instruction(0x51)

        /**
         * value1, value2 → result
         * compare two floats
         */
        val fcmpg = Instruction(0x96)

        /**
         * value1, value2 → result
         * compare two floats
         */
        val fcmpl = Instruction(0x95)

        /**
         * → 0.0f
         * push 0.0f on the stack
         */
        val fconst_0 = Instruction(0x0b)

        /**
         * → 1.0f
         * push 1.0f on the stack
         */
        val fconst_1 = Instruction(0x0c)

        /**
         * → 2.0f
         * push 2.0f on the stack
         */
        val fconst_2 = Instruction(0x0d)

        /**
         * value1, value2 → result
         * divide two floats
         */
        val fdiv = Instruction(0x6e)

        /**
         * → value
         * load a float value from local variable 0
         */
        val fload_0 = Instruction(0x22)

        /**
         * → value
         * load a float value from local variable 1
         */
        val fload_1 = Instruction(0x23)

        /**
         * → value
         * load a float value from local variable 2
         */
        val fload_2 = Instruction(0x24)

        /**
         * → value
         * load a float value from local variable 3
         */
        val fload_3 = Instruction(0x25)

        /**
         * value1, value2 → result
         * multiply two floats
         */
        val fmul = Instruction(0x6a)

        /**
         * value → result
         * negate a float
         */
        val fneg = Instruction(0x76)

        /**
         * value1, value2 → result
         * get the remainder from a division between two floats
         */
        val frem = Instruction(0x72)

        /**
         * value → [empty]
         * return a float
         */
        val freturn = Instruction(0xae)

        /**
         * value →
         * store a float value into local variable 0
         */
        val fstore_0 = Instruction(0x43)

        /**
         * value →
         * store a float value into local variable 1
         */
        val fstore_1 = Instruction(0x44)

        /**
         * value →
         * store a float value into local variable 2
         */
        val fstore_2 = Instruction(0x45)

        /**
         * value →
         * store a float value into local variable 3
         */
        val fstore_3 = Instruction(0x46)

        /**
         * value1, value2 → result
         * subtract two floats
         */
        val fsub = Instruction(0x66)

        /**
         * value → result
         * convert an int into a byte
         */
        val i2b = Instruction(0x91)

        /**
         * value → result
         * convert an int into a character
         */
        val i2c = Instruction(0x92)

        /**
         * value → result
         * convert an int into a double
         */
        val i2d = Instruction(0x87)

        /**
         * value → result
         * convert an int into a float
         */
        val i2f = Instruction(0x86)

        /**
         * value → result
         * convert an int into a long
         */
        val i2l = Instruction(0x85)

        /**
         * value → result
         * convert an int into a short
         */
        val i2s = Instruction(0x93)

        /**
         * value1, value2 → result
         * add two ints
         */
        val iadd = Instruction(0x60)

        /**
         * arrayref, index → value
         * load an int from an array
         */
        val iaload = Instruction(0x2e)

        /**
         * value1, value2 → result
         * perform a bitwise and on two integers
         */
        val iand = Instruction(0x7e)

        /**
         * arrayref, index, value →
         * store an int into an array
         */
        val iastore = Instruction(0x4f)

        /**
         * → -1
         * load the int value −1 onto the stack
         */
        val iconst_m1 = Instruction(0x02)

        /**
         * → 0
         * load the int value 0 onto the stack
         */
        val iconst_0 = Instruction(0x03)

        /**
         * → 1
         * load the int value 1 onto the stack
         */
        val iconst_1 = Instruction(0x04)

        /**
         * → 2
         * load the int value 2 onto the stack
         */
        val iconst_2 = Instruction(0x05)

        /**
         * → 3
         * load the int value 3 onto the stack
         */
        val iconst_3 = Instruction(0x06)

        /**
         * → 4
         * load the int value 4 onto the stack
         */
        val iconst_4 = Instruction(0x07)

        /**
         * → 5
         * load the int value 5 onto the stack
         */
        val iconst_5 = Instruction(0x08)

        /**
         * value1, value2 → result
         * divide two integers
         */
        val idiv = Instruction(0x6c)

        /**
         * → value
         * load an int value from local variable 0
         */
        val iload_0 = Instruction(0x1a)

        /**
         * → value
         * load an int value from local variable 1
         */
        val iload_1 = Instruction(0x1b)

        /**
         * → value
         * load an int value from local variable 2
         */
        val iload_2 = Instruction(0x1c)

        /**
         * → value
         * load an int value from local variable 3
         */
        val iload_3 = Instruction(0x1d)

        /**
         *
         * reserved for implementation-dependent operations within debuggers; should not appear in any class file
         */
        val impdep1 = Instruction(0xfe)

        /**
         *
         * reserved for implementation-dependent operations within debuggers; should not appear in any class file
         */
        val impdep2 = Instruction(0xff)

        /**
         * value1, value2 → result
         * multiply two integers
         */
        val imul = Instruction(0x68)

        /**
         * value → result
         * negate int
         */
        val ineg = Instruction(0x74)

        /**
         * value1, value2 → result
         * bitwise int or
         */
        val ior = Instruction(0x80)

        /**
         * value1, value2 → result
         * logical int remainder
         */
        val irem = Instruction(0x70)

        /**
         * value → [empty]
         * return an integer from a method
         */
        val ireturn = Instruction(0xac)

        /**
         * value1, value2 → result
         * int shift left
         */
        val ishl = Instruction(0x78)

        /**
         * value1, value2 → result
         * int arithmetic shift right
         */
        val ishr = Instruction(0x7a)

        /**
         * value →
         * store int value into variable 0
         */
        val istore_0 = Instruction(0x3b)

        /**
         * value →
         * store int value into variable 1
         */
        val istore_1 = Instruction(0x3c)

        /**
         * value →
         * store int value into variable 2
         */
        val istore_2 = Instruction(0x3d)

        /**
         * value →
         * store int value into variable 3
         */
        val istore_3 = Instruction(0x3e)

        /**
         * value1, value2 → result
         * int subtract
         */
        val isub = Instruction(0x64)

        /**
         * value1, value2 → result
         * int logical shift right
         */
        val iushr = Instruction(0x7c)

        /**
         * value1, value2 → result
         * int xor
         */
        val ixor = Instruction(0x82)

        /**
         * value → result
         * convert a long to a double
         */
        val l2d = Instruction(0x8a)

        /**
         * value → result
         * convert a long to a float
         */
        val l2f = Instruction(0x89)

        /**
         * value → result
         * convert a long to a int
         */
        val l2i = Instruction(0x88)

        /**
         * value1, value2 → result
         * add two longs
         */
        val ladd = Instruction(0x61)

        /**
         * arrayref, index → value
         * load a long from an array
         */
        val laload = Instruction(0x2f)

        /**
         * value1, value2 → result
         * bitwise and of two longs
         */
        val land = Instruction(0x7f)

        /**
         * arrayref, index, value →
         * store a long to an array
         */
        val lastore = Instruction(0x50)

        /**
         * value1, value2 → result
         * push 0 if the two longs are the same, 1 if value2 is greater than value1, -1 otherwise
         */
        val lcmp = Instruction(0x94)

        /**
         * → 0L
         * push the long 0 onto the stack
         */
        val lconst_0 = Instruction(0x09)

        /**
         * → 1L
         * push the long 1 onto the stack
         */
        val lconst_1 = Instruction(0x0a)

        /**
         * value1, value2 → result
         * divide two longs
         */
        val ldiv = Instruction(0x6d)

        /**
         * → value
         * load a long value from a local variable 0
         */
        val lload_0 = Instruction(0x1e)

        /**
         * → value
         * load a long value from a local variable 1
         */
        val lload_1 = Instruction(0x1f)

        /**
         * → value
         * load a long value from a local variable 2
         */
        val lload_2 = Instruction(0x20)

        /**
         * → value
         * load a long value from a local variable 3
         */
        val lload_3 = Instruction(0x21)

        /**
         * value1, value2 → result
         * multiply two longs
         */
        val lmul = Instruction(0x69)

        /**
         * value → result
         * negate a long
         */
        val lneg = Instruction(0x75)

        /**
         * value1, value2 → result
         * bitwise or of two longs
         */
        val lor = Instruction(0x81)

        /**
         * value1, value2 → result
         * remainder of division of two longs
         */
        val lrem = Instruction(0x71)

        /**
         * value → [empty]
         * return a long value
         */
        val lreturn = Instruction(0xad)

        /**
         * value1, value2 → result
         * bitwise shift left of a long value1 by int value2 positions
         */
        val lshl = Instruction(0x79)

        /**
         * value1, value2 → result
         * bitwise shift right of a long value1 by int value2 positions
         */
        val lshr = Instruction(0x7b)

        /**
         * value →
         * store a long value in a local variable 0
         */
        val lstore_0 = Instruction(0x3f)

        /**
         * value →
         * store a long value in a local variable 1
         */
        val lstore_1 = Instruction(0x40)

        /**
         * value →
         * store a long value in a local variable 2
         */
        val lstore_2 = Instruction(0x41)

        /**
         * value →
         * store a long value in a local variable 3
         */
        val lstore_3 = Instruction(0x42)

        /**
         * value1, value2 → result
         * subtract two longs
         */
        val lsub = Instruction(0x65)

        /**
         * value1, value2 → result
         * bitwise shift right of a long value1 by int value2 positions, unsigned
         */
        val lushr = Instruction(0x7d)

        /**
         * value1, value2 → result
         * bitwise exclusive or of two longs
         */
        val lxor = Instruction(0x83)

        /**
         * objectref →
         * enter monitor for object ("grab the lock" – start of synchronized() section)
         */
        val monitorenter = Instruction(0xc2)

        /**
         * objectref →
         * exit monitor for object ("release the lock" – end of synchronized() section)
         */
        val monitorexit = Instruction(0xc3)

        /**
         * [No change]
         * perform no operation
         */
        val nop = Instruction(0x00)

        /**
         * value →
         * discard the top value on the stack
         */
        val pop = Instruction(0x57)

        /**
         * {value2, value1} →
         * discard the top two values on the stack (or one value, if it is a double or long)
         */
        val pop2 = Instruction(0x58)

        /**
         * → [empty]
         * return void from method
         */
        val `return` = Instruction(0xb1)

        /**
         * arrayref, index → value
         * load short from array
         */
        val saload = Instruction(0x35)

        /**
         * arrayref, index, value →
         * store short to array
         */
        val sastore = Instruction(0x56)

        /**
         * value2, value1 → value1, value2
         * swaps two top words on the stack (note that value1 and value2 must not be double or long)
         */
        val swap = Instruction(0x5f)

        /**
         * count → arrayref
         * create new array with count elements of primitive type identified by atype
         */
        fun newarray(atype: Int) = Instruction(0xbc, { it.writeByte(atype) })

        /**
         * → value
         * push a byte onto the stack as an integer value
         */
        fun bipush(byte: Int) = Instruction(0x10, { it.writeByte(byte) })

        /**
         * → objectref
         * load a reference onto the stack from a local variable #index
         */
        fun aload(index: Int) = Instruction(0x19, { it.writeByte(index) })

        /**
         * objectref →
         * store a reference into a local variable #index
         */
        fun astore(index: Int) = Instruction(0x3a, { it.writeByte(index) })

        /**
         * → value
         * load a double value from a local variable #index
         */
        fun dload(index: Int) = Instruction(0x18, { it.writeByte(index) })

        /**
         * value →
         * store a double value into a local variable #index
         */
        fun dstore(index: Int) = Instruction(0x39, { it.writeByte(index) })

        /**
         * → value
         * load a float value from a local variable #index
         */
        fun fload(index: Int) = Instruction(0x17, { it.writeByte(index) })

        /**
         * value →
         * store a float value into a local variable #index
         */
        fun fstore(index: Int) = Instruction(0x38, { it.writeByte(index) })

        /**
         * → value
         * load an int value from a local variable #index
         */
        fun iload(index: Int) = Instruction(0x15, { it.writeByte(index) })

        /**
         * value →
         * store int value into variable #index
         */
        fun istore(index: Int) = Instruction(0x36, { it.writeByte(index) })

        /**
         * → value
         * push a constant #index from a constant pool (String, int or float) onto the stack
         */
        fun ldc(index: Int) = Instruction(0x12, { it.writeByte(index) })

        /**
         * → value
         * load a long value from a local variable #index
         */
        fun lload(index: Int) = Instruction(0x16, { it.writeByte(index) })

        /**
         * value →
         * store a long value in a local variable #index
         */
        fun lstore(index: Int) = Instruction(0x37, { it.writeByte(index) })

        /**
         * [No change]
         * continue execution from address taken from a local variable #index (the asymmetry with jsr is intentional)
         */
        fun ret(index: Int) = Instruction(0xa9, { it.writeByte(index) })

        /**
         * [no change]
         * goes to another instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun goto(param: Int) = Instruction(0xa7, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if references are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_acmpeq(param: Int) = Instruction(0xa5, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if references are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_acmpne(param: Int) = Instruction(0xa6, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if ints are equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmpeq(param: Int) = Instruction(0x9f, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if value1 is greater than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmpge(param: Int) = Instruction(0xa2, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if value1 is greater than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmpgt(param: Int) = Instruction(0xa3, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if value1 is less than or equal to value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmple(param: Int) = Instruction(0xa4, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if value1 is less than value2, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmplt(param: Int) = Instruction(0xa1, { it.writeShort(param) })

        /**
         * value1, value2 →
         * if ints are not equal, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun if_icmpne(param: Int) = Instruction(0xa0, { it.writeShort(param) })

        /**
         * value →
         * if value is 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifeq(param: Int) = Instruction(0x99, { it.writeShort(param) })

        /**
         * value →
         * if value is greater than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifge(param: Int) = Instruction(0x9c, { it.writeShort(param) })

        /**
         * value →
         * if value is greater than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifgt(param: Int) = Instruction(0x9d, { it.writeShort(param) })

        /**
         * value →
         * if value is less than or equal to 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifle(param: Int) = Instruction(0x9e, { it.writeShort(param) })

        /**
         * value →
         * if value is less than 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun iflt(param: Int) = Instruction(0x9b, { it.writeShort(param) })

        /**
         * value →
         * if value is not 0, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifne(param: Int) = Instruction(0x9a, { it.writeShort(param) })

        /**
         * value →
         * if value is not null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifnonnull(param: Int) = Instruction(0xc7, { it.writeShort(param) })

        /**
         * value →
         * if value is null, branch to instruction at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2)
         */
        fun ifnull(param: Int) = Instruction(0xc6, { it.writeShort(param) })

        /**
         * → address
         * jump to subroutine at branchoffset (signed short constructed from unsigned bytes branchbyte1 << 8 + branchbyte2) and place the return address on the stack
         */
        fun jsr(param: Int) = Instruction(0xa8, { it.writeShort(param) })

        /**
         * → value
         * push a short onto the stack
         */
        fun sipush(param: Int) = Instruction(0x11, { it.writeShort(param) })

        /**
         * [No change]
         * increment local variable #index by signed byte const
         */
        val iinc /*(2: index, const)*/ = Instruction(0x84)

        /**
         * objectref → value
         * get a field value of an object objectref, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)
         */
        fun getfield(param: Int) = Instruction(0xb4, { it.writeShort(param) })

        /**
         * → value
         * get a static field value of a class, where the field is identified by field reference in the constant pool index (index1 << 8 + index2)
         */
        fun getstatic(param: Int) = Instruction(0xb2, { it.writeShort(param) })

        /**
         * count → arrayref
         * create a new array of references of length count and component type identified by the class reference index (indexbyte1 << 8 + indexbyte2) in the constant pool
         */
        fun anewarray(param: Int) = Instruction(0xbd, { it.writeShort(param) })

        /**
         * objectref → objectref
         * checks whether an objectref is of a certain type, the class reference of which is in the constant pool at index (indexbyte1 << 8 + indexbyte2)
         */
        fun checkcast(param: Int) = Instruction(0xc0, { it.writeShort(param) })

        /**
         * objectref → result
         * determines if an object objectref is of a given type, identified by class reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun instanceof(param: Int) = Instruction(0xc1, { it.writeShort(param) })

        /**
         * objectref, [arg1, arg2, ...] → result
         * invoke instance method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun invokespecial(param: Int) = Instruction(0xb7, { it.writeShort(param) })

        /**
         * [arg1, arg2, ...] → result
         * invoke a static method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun invokestatic(param: Int) = Instruction(0xb8, { it.writeShort(param) })

        /**
         * objectref, [arg1, arg2, ...] → result
         * invoke virtual method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun invokevirtual(param: Int) = Instruction(0xb6, { it.writeShort(param) })

        /**
         * → value
         * push a constant #index from a constant pool (String, int or float) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)
         */
        fun ldc_w(param: Int) = Instruction(0x13, { it.writeShort(param) })

        /**
         * → value
         * push a constant #index from a constant pool (double or long) onto the stack (wide index is constructed as indexbyte1 << 8 + indexbyte2)
         */
        fun ldc2_w(param: Int) = Instruction(0x14, { it.writeShort(param) })

        /**
         * → objectref
         * create new object of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2)
         */
        fun new(param: Int) = Instruction(0xbb, { it.writeShort(param) })

        /**
         * objectref, value →
         * set field to value in an object objectref, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun putfield(param: Int) = Instruction(0xb5, { it.writeShort(param) })

        /**
         * value →
         * set static field to value in a class, where the field is identified by a field reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        fun putstatic(param: Int) = Instruction(0xb3, { it.writeShort(param) })

        /**
         * [same as for corresponding instructions]
         * execute opcode, where opcode is either iload, fload, aload, lload, dload, istore, fstore, astore, lstore, dstore, or ret, but assume the index is 16 bit; or execute iinc, where the index is 16 bits and the constant to increment by is a signed 16 bit short
         */
        val wide /*(3/5: opcode, indexbyte1, indexbyte2
or
iinc, indexbyte1, indexbyte2, countbyte1, countbyte2)*/ = Instruction(0xc4)

        /**
         * count1, [count2,...] → arrayref
         * create a new array of dimensions dimensions with elements of type identified by class reference in constant pool index (indexbyte1 << 8 + indexbyte2); the sizes of each dimension is identified by count1, [count2, etc.]
         */
        val multianewarray /*(3: indexbyte1, indexbyte2, dimensions)*/ = Instruction(0xc5)

        /**
         * key →
         * a target address is looked up from a table using a key and execution continues from the instruction at that address
         */
        val lookupswitch /*(4+: <0–3 bytes padding>, defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, npairs1, npairs2, npairs3, npairs4, match-offset pairs...)*/ = Instruction(0xab)

        /**
         * index →
         * continue execution from an address in the table at offset index
         */
        val tableswitch /*(4+: [0–3 bytes padding], defaultbyte1, defaultbyte2, defaultbyte3, defaultbyte4, lowbyte1, lowbyte2, lowbyte3, lowbyte4, highbyte1, highbyte2, highbyte3, highbyte4, jump offsets...)*/ = Instruction(0xaa)

        /**
         * [no change]
         * goes to another instruction at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4)
         */
        val goto_w /*(4: branchbyte1, branchbyte2, branchbyte3, branchbyte4)*/ = Instruction(0xc8)

        /**
         * → address
         * jump to subroutine at branchoffset (signed int constructed from unsigned bytes branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4) and place the return address on the stack
         */
        val jsr_w /*(4: branchbyte1, branchbyte2, branchbyte3, branchbyte4)*/ = Instruction(0xc9)

        /**
         * [arg1, [arg2 ...]] → result
         * invokes a dynamic method and puts the result on the stack (might be void); the method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        val invokedynamic /*(4: indexbyte1, indexbyte2, 0, 0)*/ = Instruction(0xba)

        /**
         * objectref, [arg1, arg2, ...] → result
         * invokes an interface method on object objectref and puts the result on the stack (might be void); the interface method is identified by method reference index in constant pool (indexbyte1 << 8 + indexbyte2)
         */
        val invokeinterface /*(4: indexbyte1, indexbyte2, count, 0)*/ = Instruction(0xb9)

    }
}

