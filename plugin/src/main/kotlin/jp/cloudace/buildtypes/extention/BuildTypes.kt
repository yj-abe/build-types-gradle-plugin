package jp.cloudace.buildtypes.extention

import groovy.lang.Closure
import jp.cloudace.buildtypes.ext.javaConvention
import jp.cloudace.buildtypes.processor.MainSourceProcessor
import jp.cloudace.buildtypes.task.ClasspathTask
import jp.cloudace.buildtypes.task.CleanBuildConfigTask
import jp.cloudace.buildtypes.task.GenerateBuildConfigTask
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile

open class BuildTypes(project: Project) {

    var developOn: String? = null

    val buildTypes: NamedDomainObjectContainer<BuildType> = project.container(BuildType::class.java) {
        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java) as JavaPluginConvention
        BuildType(it).apply {
            createSourceSet(javaConvention.sourceSets, Action { sourceSet ->
                if (developOn == name) {
                    MainSourceProcessor(project).addSourceSet(sourceSet, this)
                    println("set develop on")
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
        ).let { generateTask ->
            project.tasks.create(
                "clean${capitalizedTypeName}BuildConfig",
                CleanBuildConfigTask::class.java,
                buildType.outputDir(project)
            ).let { cleanTask ->
                generateTask.dependsOn(cleanTask.name)
            }

            project.tasks.getByName("compile${capitalizedTypeName}Java").let { compileTask ->
                compileTask.dependsOn(generateTask)
                (compileTask as JavaCompile).source(buildType.outputDir(project))
            }

            project.tasks.getByName("compileJava").dependsOn(generateTask)
            project.tasks.create(
                "setup${capitalizedTypeName}Classpath",
                ClasspathTask::class.java,
                this,
                buildType
            ).let { classpathTask ->

                classpathTask.doLast {
                    val developTargetType = buildTypes.getByName(developOn!!)
                    val deleteSet = project.javaConvention.sourceSets.findByName(developTargetType.name)!!
                    val addSet = project.javaConvention.sourceSets.findByName(buildType.name)!!

                    val processor = MainSourceProcessor(project)
                    processor.removeAndAddSourceSet(deleteSet, addSet, developTargetType, buildType)
                    developOn = buildType.name
//                    processor.removeAndAddSourceSet()
//                    project.javaConvention.sourceSets.findByName(developTargetType.name)?.let {
//                        processor.removeSourceSet(it, developTargetType)
//                    }
//                    project.javaConvention.sourceSets.findByName(buildType.name)?.let {
//                        processor.addSourceSet(it, buildType)
//                    }
                }

                project.tasks.create("build$capitalizedTypeName") {
                    val compileJavaTask = project.tasks.getByName("compileJava")
                    compileJavaTask.mustRunAfter(classpathTask)
                    it.dependsOn(classpathTask)
                    it.dependsOn(compileJavaTask)
                }

            }
        }
    }
}
