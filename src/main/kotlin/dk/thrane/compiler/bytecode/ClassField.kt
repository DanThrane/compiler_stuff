package dk.thrane.compiler.bytecode

class ClassField(val accessFlags: List<FieldAccessFlag>, val name: ConstantUtf8Info, val descriptor: ConstantUtf8Info,
                 val attributes: List<Attribute>) {

}