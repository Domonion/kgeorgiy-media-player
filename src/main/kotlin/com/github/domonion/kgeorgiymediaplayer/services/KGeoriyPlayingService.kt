package com.github.domonion.kgeorgiymediaplayer.services

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationEvent
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemProgressNotificationManager
import com.intellij.openapi.project.Project
import com.intellij.util.io.exists
import com.wuyr.intellijmediaplayer.actions.ControllerAction
import com.wuyr.intellijmediaplayer.components.Controller
import com.wuyr.intellijmediaplayer.media.MediaPlayer
import java.awt.KeyboardFocusManager
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JFrame
import kotlin.io.path.absolutePathString
import kotlin.io.path.extension
import kotlin.io.path.outputStream

class KGeorgiyPlayingService(val project: Project) : Disposable {
    companion object {
        private val prop = System.getProperty("kgeorgiy.path")
        private val kgGeorgiyUnarchived: String = try {
            if (Paths.get(prop).extension == "mp4") {
                prop
            } else throw Exception()
        } catch (ignored: Throwable) {
            kotlin.io.path.createTempFile(suffix = "mp4").also {path ->
                this.javaClass.classLoader.getResourceAsStream("kgeorgiy_vidos.mp4")?.use { it.transferTo(path.outputStream()) }
            }.absolutePathString()
        }
    }

    private val atomicInt = AtomicInteger()

    fun playKGeorgiy() {
        if (atomicInt.compareAndSet(0, 1)) {
            try {
                if (MediaPlayer.isPlaying)
                    return
                if (kgGeorgiyUnarchived != prop) {
                    NotificationGroupManager.getInstance().getNotificationGroup("KGeorgiy Notification group")
                        .createNotification(
                            "No valid .mp4 KGeorgiy in -Dkgeorgiy.path=$prop property, switching to default",
                            NotificationType.WARNING
                        ).notify(project)
                }
                val frame = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow as JFrame
                if (MediaPlayer.init(frame, kgGeorgiyUnarchived)) {
                    if (MediaPlayer.start()) {
                        if (ControllerAction.isShowController) {
                            Controller.show(frame)
                        }
                    }
                }
            } catch (e: Throwable) {
                NotificationGroupManager.getInstance().getNotificationGroup("KGeorgiy Notification group")
                    .createNotification(
                        "Could not play KGeorgiy, exception message: ${e.message}",
                        NotificationType.ERROR
                    ).notify(project)
            } finally {
                atomicInt.set(0)
            }
        }
    }

    override fun dispose() {}
}