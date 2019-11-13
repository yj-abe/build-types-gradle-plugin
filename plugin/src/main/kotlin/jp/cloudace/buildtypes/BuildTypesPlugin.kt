package jp.cloudace.buildtypes

import jp.cloudace.buildtypes.extention.BuildTypes
import org.gradle.api.Plugin
import org.gradle.api.Project

class BuildTypesPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("buildTypes", BuildTypes::class.java, project)
    }

}
