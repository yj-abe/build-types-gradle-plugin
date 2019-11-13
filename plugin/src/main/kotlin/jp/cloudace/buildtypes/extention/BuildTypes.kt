package jp.cloudace.buildtypes.extention

import groovy.lang.Closure
import jp.cloudace.buildtypes.task.CleanBuildConfigTask
import jp.cloudace.buildtypes.task.GenerateBuildConfigTask
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile

open class BuildTypes(project: Project) {

    var developOn: String? = null

    private val buildTypes: NamedDomainObjectContainer<BuildType> = project.container(BuildType::class.java) {
        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java) as JavaPluginConvention
        BuildType(it).apply {
            createSourceSet(javaConvention.sourceSets, Action { sourceSet ->
                if (developOn == name) {
                    project.tasks.getByName("compileJava").let { compileTask ->
                        sourceSet.java.srcDirs.forEach { dir -> (compileTask as JavaCompile).source(dir) }
                        javaConvention.sourceSets.getByName("main").java { sourceDirectorySet ->
                            val current = sourceDirectorySet.srcDirs
                            current.addAll(sourceSet.java.srcDirs)
                            current.add(outputDir(project))
                            sourceDirectorySet.srcDirs(current)
                        }
                    }
                }
            })
            createBuildConfigTasks(project, this)
        }
    }

    fun types(closure: Closure<BuildType>) {
        buildTypes.configure(closure)
    }

    private fun createBuildConfigTasks(project: Project, buildType: BuildType) {
        val capitalizedTypeName = buildType.name.capitalize()
        project.tasks.create(
            "generate${capitalizedTypeName}BuildConfig",
            GenerateBuildConfigTask::class.java,
            buildType
        ).let { task ->
            project.tasks.create(
                "clean${capitalizedTypeName}BuildConfig",
                CleanBuildConfigTask::class.java,
                buildType.outputDir(project)
            ).let { cleanTask ->
                task.dependsOn(cleanTask.name)
            }

            project.tasks.getByName("compile${capitalizedTypeName}Java").let { compileTask ->
                compileTask.dependsOn(task)
                (compileTask as JavaCompile).source(buildType.outputDir(project))
            }
            project.tasks.getByName("compileJava").dependsOn(task)
        }
    }
}
