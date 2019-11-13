package jp.cloudace.buildtypes.task

import jp.cloudace.buildtypes.extention.BuildType
import jp.cloudace.buildtypes.generator.BuildConfigGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class GenerateBuildConfigTask @Inject constructor(
    private val buildType: BuildType
) : DefaultTask() {

    @TaskAction
    fun generate() {
        val rootPackage = project.group as String
        val javaFile = BuildConfigGenerator.generateJavaCode(rootPackage, buildType)
        javaFile.writeTo(buildType.outputDir(project))
    }

}
