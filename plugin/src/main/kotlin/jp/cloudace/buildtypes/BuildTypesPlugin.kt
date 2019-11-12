package jp.cloudace.buildtypes

import jp.cloudace.buildtypes.extention.BuildSettings
import jp.cloudace.buildtypes.extention.BuildType
import jp.cloudace.buildtypes.task.CleanBuildConfigTask
import jp.cloudace.buildtypes.task.GenerateBuildConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

class BuildTypesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java) as JavaPluginConvention
        val buildTypes = project.container(BuildType::class.java)
        buildTypes.all {
            it.createSourceSet(javaConvention.sourceSets)
            val capitalizedTypeName = it.name.capitalize()

            project.tasks.create(
                "generate${capitalizedTypeName}BuildConfig",
                GenerateBuildConfigTask::class.java,
                it
            ).let { task ->
                project.tasks.create(
                    "clean${capitalizedTypeName}BuildConfig",
                    CleanBuildConfigTask::class.java,
                    task.outputDir()
                ).let { cleanTask ->
                    task.dependsOn(cleanTask.name)
                }
            }

            // TODO: リソース、自動生成のパス設定
        }
        project.extensions.add("buildTypes", buildTypes)
        project.extensions.create("buildSettings", BuildSettings::class.java)
    }

}
