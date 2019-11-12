package jp.cloudace.buildtypes.generator

import com.squareup.javapoet.JavaFile

interface JavaCodeGenerator<T> {
    fun generateJavaCode(rootPackage: String, source: T): JavaFile
}
