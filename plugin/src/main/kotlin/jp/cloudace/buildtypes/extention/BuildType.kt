package jp.cloudace.buildtypes.extention

import jp.cloudace.buildtypes.data.Field
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

open class BuildType(val name: String) {

    private val _fields: MutableList<Field> = mutableListOf()
    internal val fields: List<Field> = _fields

    var debuggable: Boolean = false

    fun buildConfigField(typeName: String, varName: String, value: String) {
        val field = Field(typeName, varName, value)
        _fields.add(field)
    }

    fun createSourceSet(container: SourceSetContainer): SourceSet {
        return container.findByName(name) ?: container.create(name)
    }

}
