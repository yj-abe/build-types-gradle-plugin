package jp.cloudace.buildtypes.extention

import jp.cloudace.buildtypes.data.Field
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File

open class BuildType(val name: String) {

    companion object {
        private val DS: String = File.separator
        private val OUTPUT_DIR_NAME: String = "generated${DS}sources${DS}buildConfig"
    }

    private val _fields: MutableList<Field> = mutableListOf()
    internal val fields: List<Field> = _fields

    var debuggable: Boolean = false

    fun buildConfigField(typeName: String, varName: String, value: String) {
        val field = Field(typeName, varName, value)
        _fields.add(field)
    }

    fun createSourceSet(container: SourceSetContainer, configureAction: Action<SourceSet>): SourceSet {
        return container.findByName(name)?.apply { configureAction.execute(this) } ?: container.create(
            name,
            configureAction
        )
    }

    internal fun outputDir(project: Project): File = File(project.buildDir, OUTPUT_DIR_NAME + DS + name)

}
