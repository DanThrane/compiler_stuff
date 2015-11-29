package dk.thrane.compiler.bytecode

class Method(val accessFlags: List<MethodAccessFlag>, val name: ConstantUtf8Info, val descriptor: ConstantUtf8Info,
             val attributes: List<Attribute>) {

}