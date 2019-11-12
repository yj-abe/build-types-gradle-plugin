package jp.cloudace.buildtypes.task

import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class CleanBuildConfigTask @Inject constructor(
    private val outputDir: File
) : Delete() {

    @TaskAction
    override fun clean() {
        delete(outputDir)
        super.clean()
    }
}
