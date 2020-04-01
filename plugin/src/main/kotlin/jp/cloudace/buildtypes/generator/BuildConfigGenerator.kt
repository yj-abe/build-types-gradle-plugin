package jp.cloudace.buildtypes.generator

import com.squareup.javapoet.*
import jp.cloudace.buildtypes.extention.BuildType
import javax.lang.model.element.Modifier

object BuildConfigGenerator : JavaCodeGenerator<BuildType> {

    private val CONST_VALUE_MODIFIERS = arrayOf(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)

    override fun generateJavaCode(rootPackage: String, source: BuildType): JavaFile {
        val classBuilder = TypeSpec.classBuilder("BuildConfig")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        FieldSpec.builder(String::class.java, "BUILD_TYPE", *CONST_VALUE_MODIFIERS)
            .initializer("\"${source.name}\"")
            .build()
            .let { classBuilder.addField(it) }

        FieldSpec.builder(Boolean::class.java, "DEBUG", *CONST_VALUE_MODIFIERS)
            .initializer("${source.debuggable}")
            .build()
            .let { classBuilder.addField(it) }

        source.fields.map {
            FieldSpec.builder(typeOf(it.typeName), it.varName, *CONST_VALUE_MODIFIERS)
                .initializer(it.value)
                .build()
        }.forEach { classBuilder.addField(it) }
        return JavaFile.builder(rootPackage, classBuilder.build()).build()
    }

    private fun typeOf(typeName: String): TypeName {
        return when {
            typeName.endsWith("[]") -> {
                ArrayTypeName.of(typeOf(typeName.substring(0, typeName.length - 2)))
            }
            isGenerics(typeName) -> {
                "([^<]+)<(.+)>$".toRegex().matchEntire(typeName)?.groupValues?.let { group ->
                    val typeArgs = if (isGenerics(group[2])) {
                        arrayOf(typeOf(group[2]))
                    } else {
                        group[2].split(",")
                            .map { arg -> arg.trim() }
                            .map { arg -> typeOf(arg) }
                            .toTypedArray()
                    }
                    ParameterizedTypeName.get(ClassName.bestGuess(group[1]), *typeArgs)
                } ?: throw IllegalStateException("not reached")
            }
            else -> when (typeName) {
                "String" -> TypeName.get(String::class.java)
                "byte" -> TypeName.BYTE
                "short" -> TypeName.SHORT
                "int" -> TypeName.INT
                "long" -> TypeName.LONG
                "boolean" -> TypeName.BOOLEAN
                "Byte" -> TypeName.BYTE.box()
                "Short" -> TypeName.SHORT.box()
                "Integer" -> TypeName.INT.box()
                "Long" -> TypeName.LONG.box()
                "Boolean" -> TypeName.BOOLEAN.box()
                else -> ClassName.bestGuess(typeName)
            }
        }
    }

    private fun isGenerics(target: String): Boolean {
        return target.matches("([^<]+)<(.+)>$".toRegex())
    }
}
