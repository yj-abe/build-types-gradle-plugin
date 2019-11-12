package jp.cloudace.buildtypes.task

import jp.cloudace.buildtypes.extention.BuildType
import jp.cloudace.buildtypes.generator.BuildConfigGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class GenerateBuildConfigTask @Inject constructor(
    private val buildType: BuildType
) : DefaultTask() {

    companion object {
        private val DS: String = File.separator
        private val OUTPUT_DIR_NAME: String = "generated${DS}source${DS}buildConfig"
    }

    @TaskAction
    fun generate() {
        val rootPackage = project.group as String
        val javaFile = BuildConfigGenerator.generateJavaCode(rootPackage, buildType)
        javaFile.writeTo(outputDir())
    }

    fun outputDir(): File = File(project.buildDir, OUTPUT_DIR_NAME + DS + buildType.name)

}
