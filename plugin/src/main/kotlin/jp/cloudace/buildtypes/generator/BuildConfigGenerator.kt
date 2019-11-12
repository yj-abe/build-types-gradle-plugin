package jp.cloudace.buildtypes.generator

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import jp.cloudace.buildtypes.extention.BuildType
import java.lang.reflect.Type
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

    private fun typeOf(typeName: String): Type {
        return when (typeName) {
            "String" -> String::class.java
            "byte" -> Byte::class.java
            "short" -> Short::class.java
            "int" -> Int::class.java
            "long" -> Long::class.java
            "boolean" -> Boolean::class.java
            "Byte" -> java.lang.Byte::class.java
            "Short" -> java.lang.Short::class.java
            "Integer" -> java.lang.Integer::class.java
            "Long" -> java.lang.Long::class.java
            "Boolean" -> java.lang.Boolean::class.java
            else -> Class.forName(typeName)
        }
    }

}
