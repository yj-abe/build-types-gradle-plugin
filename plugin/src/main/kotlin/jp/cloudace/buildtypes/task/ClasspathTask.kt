package jp.cloudace.buildtypes.task

import jp.cloudace.buildtypes.ext.javaConvention
import jp.cloudace.buildtypes.extention.BuildType
import jp.cloudace.buildtypes.extention.BuildTypes
import jp.cloudace.buildtypes.processor.MainSourceProcessor
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import javax.inject.Inject

open class ClasspathTask @Inject constructor(
    private val buildTypes: BuildTypes,
    private val buildType: BuildType
) : DefaultTask() {

//    override fun doLast(action: Action<in Task>): Task {
//        val developTargetType = buildTypes.buildTypes.getByName(buildTypes.developOn!!)
//        val processor = MainSourceProcessor(project)
//        project.javaConvention.sourceSets.findByName(developTargetType.name)?.let {
//            processor.removeSourceSet(it, developTargetType)
//        }
//        project.javaConvention.sourceSets.findByName(buildType.name)?.let {
//            processor.addSourceSet(it, buildType)
//        }
//        return super.doLast(action)
//    }
}
