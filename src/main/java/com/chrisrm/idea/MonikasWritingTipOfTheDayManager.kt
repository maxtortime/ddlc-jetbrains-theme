package com.chrisrm.idea

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.WindowManagerEx

class MonikasWritingTipOfTheDayManager : StartupActivity, DumbAware {

    override fun runActivity(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) return

        ToolWindowManager.getInstance(project).invokeLater {
            if (!project.isDisposed)
                ToolWindowManager.getInstance(project).invokeLater {
                    if (!project.isDisposed)
                        WritingTipDialog.createForProject(project).show()
                }
        }
    }
}
