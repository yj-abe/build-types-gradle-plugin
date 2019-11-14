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
            java.setSrcDirs(java.srcDirs.apply {
                addAll(sourceSet.java.srcDirs)
                add(buildType.outputDir(project))
            })
            resources.setSrcDirs(resources.srcDirs.apply {
                addAll(sourceSet.resources.srcDirs)
            })
        }
    }

    fun removeSourceSet(sourceSet: SourceSet, buildType: BuildType) {
        mainSourceSet.apply {
            java.setSrcDirs(java.srcDirs.apply {
                removeAll(sourceSet.java.srcDirs)
                remove(buildType.outputDir(project))
            })
            resources.setSrcDirs(resources.srcDirs.apply {
                removeAll(sourceSet.resources.srcDirs)
            })
        }
    }

}
