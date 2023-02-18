package com.github.domonion.kgeorgiymediaplayer.services

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.Project
import com.wuyr.intellijmediaplayer.actions.ControllerAction
import com.wuyr.intellijmediaplayer.components.Controller
import com.wuyr.intellijmediaplayer.media.MediaPlayer
import java.awt.KeyboardFocusManager
import java.lang.Exception
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import javax.swing.JFrame
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.outputStream

class KGeorgiyPlayingService(val project: Project) : Disposable {
    companion object {
        private val prop = System.getProperty("kgeorgiy.path")
        private fun createKGeorgiy(): String = try {
            val kgPath = Paths.get(prop)
            if (kgPath.exists() && kgPath.extension == "mp4") {
                prop
            } else throw Exception()
        } catch (ignored: Throwable) {
            kotlin.io.path.createTempFile(suffix = "mp4").also {
                it.outputStream().use { pathStream ->
                    this.javaClass.classLoader.getResourceAsStream("kgeorgiy_vidos.mp4")
                        ?.use { it.transferTo(pathStream) }
                }
            }.absolutePathString()
        }

        private var kGeorgiyUnarchived: String = createKGeorgiy()
    }

    private val atomicInt = AtomicInteger()

    private fun notifyWarn(message: String) {
        NotificationGroupManager.getInstance().getNotificationGroup("KGeorgiy Notification group")
            .createNotification(message, NotificationType.WARNING).notify(project)
    }

    fun playKGeorgiy() = invokeLater {
        if (atomicInt.compareAndSet(0, 1)) {
            try {
                if (MediaPlayer.isPlaying)
                    return@invokeLater

                if (kGeorgiyUnarchived != prop) {
                    notifyWarn("No valid .mp4 KGeorgiy in -Dkgeorgiy.path=$prop property, switching to default")
                }

                if (!Paths.get(kGeorgiyUnarchived).exists()) {
                    notifyWarn("KGeorgiy vanished from $kGeorgiyUnarchived, regenerating from default")
                    kGeorgiyUnarchived = createKGeorgiy()
                }

                val frame = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow as JFrame
                if (MediaPlayer.init(frame, kGeorgiyUnarchived)) {
                    if (MediaPlayer.start()) {
                        if (ControllerAction.isShowController) {
                            Controller.show(frame)
                        }
                    }
                }
            } catch (e: Throwable) {
                notifyWarn("Could not play KGeorgiy, exception message: ${e.message}")
            } finally {
                atomicInt.set(0)
            }
        }
    }

    override fun dispose() {}
}