package jp.cloudace.buildtypes.processor

import jp.cloudace.buildtypes.ext.javaConvention
import jp.cloudace.buildtypes.extention.BuildType
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

class MainSourceProcessor(private val project: Project) {

    private val mainSourceSet: SourceSet by lazy {
        project.javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    }

    fun addSourceSet(sourceSet: SourceSet, buildType: BuildType) {
        mainSourceSet.apply {
            java { sourceDirectorySet ->
                val srcDirs = sourceDirectorySet.srcDirs
                srcDirs.addAll(sourceSet.java.srcDirs)
                srcDirs.add(buildType.outputDir(project))
                sourceDirectorySet.srcDirs(srcDirs)
            }
            resources { sourceDirectorySet ->
                val srcDirs = sourceDirectorySet.srcDirs
                srcDirs.addAll(sourceSet.resources.srcDirs)
                sourceDirectorySet.srcDirs(srcDirs)
            }
        }
    }

    fun removeAndAddSourceSet(deleteSet: SourceSet, addSet: SourceSet, deleteType: BuildType, addType: BuildType) {
        mainSourceSet.apply {
            java.srcDirs.apply {
                println("before remove : ")
                forEach { println(it) }
                removeAll(deleteSet.java.srcDirs)
                remove(deleteType.outputDir(project))
                addAll(addSet.java.srcDirs)
                add(addType.outputDir(project))
                println("after remove : ")
                forEach { println(it) }
            }
            resources.srcDirs.apply {
                removeAll(deleteSet.resources.srcDirs)
                addAll(addSet.resources.srcDirs)
            }
        }
    }


}
