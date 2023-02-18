package com.github.domonion.kgeorgiymediaplayer.listeners

import com.github.domonion.kgeorgiymediaplayer.services.KGeorgiyPlayingService
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationEvent
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemProgressNotificationManager
import com.intellij.openapi.project.Project
import com.intellij.task.*
import java.lang.Exception

internal class KGeorgiyBuildTaskListener(private val project: Project) : ProjectTaskListener, ExecutionListener {

    init {
        ExternalSystemProgressNotificationManager.getInstance().addNotificationListener(object :
            ExternalSystemTaskNotificationListener {
            override fun onStart(id: ExternalSystemTaskId) {
            }

            override fun onStatusChange(event: ExternalSystemTaskNotificationEvent) {
            }

            override fun onTaskOutput(id: ExternalSystemTaskId, text: String, stdOut: Boolean) {

            }

            override fun onEnd(id: ExternalSystemTaskId) {
            }

            override fun onSuccess(id: ExternalSystemTaskId) {
            }

            override fun onFailure(id: ExternalSystemTaskId, e: Exception) {
                // maven is not external system, it cannot be handled here
                if (id.projectSystemId.id.equals("gradle", true) && e.message?.contains("compilation", true) == true)
                    project.service<KGeorgiyPlayingService>().playKGeorgiy()
            }

            override fun beforeCancel(id: ExternalSystemTaskId) {
            }

            override fun onCancel(id: ExternalSystemTaskId) {
            }

        }, project.service<KGeorgiyPlayingService>())
    }

    override fun finished(result: ProjectTaskManager.Result) {
        // this is for green hammer
        val shouldShowKGeorgiy = result.anyTaskMatches { task: ProjectTask, state: ProjectTaskState ->
            task is ModuleBuildTask && state.isFailed
        }

        if (shouldShowKGeorgiy) {
            project.service<KGeorgiyPlayingService>().playKGeorgiy()
        }
    }

    override fun processTerminated(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler,
        exitCode: Int
    ) {
        // also gradle processes are tracked here, but gradke has its own external system, so better handle only maven here
        if (env.runProfile.name.contains("compile")
            && env.runProfile.javaClass.simpleName.contains("maven", true)
            && exitCode != 0) {
            project.service<KGeorgiyPlayingService>().playKGeorgiy()
        }
    }
}
