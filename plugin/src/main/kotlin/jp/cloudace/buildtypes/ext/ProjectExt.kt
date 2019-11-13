package jp.cloudace.buildtypes.ext

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

val Project.javaConvention: JavaPluginConvention
    get() = project.convention.getPlugin(JavaPluginConvention::class.java) as JavaPluginConvention
