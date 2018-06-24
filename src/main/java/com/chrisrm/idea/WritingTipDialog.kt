package com.chrisrm.idea

import com.intellij.CommonBundle
import com.intellij.ide.IdeBundle
import com.intellij.ide.util.TipPanel
import com.intellij.internal.statistic.UsageTrigger
import com.intellij.openapi.application.impl.LaterInvocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.wm.ex.WindowManagerEx
import com.intellij.util.containers.stream
import com.intellij.util.ui.JBUI

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent

class WritingTipDialog : DialogWrapper {
    private var myTipPanel: TipPanel? = null

    constructor() : super(WindowManagerEx.getInstanceEx().findVisibleFrame(), true) {
        initialize()
    }

    constructor(parent: Window) : super(parent, true) {
        initialize()
    }

    override fun getDimensionServiceKey(): String? {
        return javaClass.name
    }

    private fun initialize() {
        isModal = false
        title = "Monika's Writing Tip of the Day."
        setCancelButtonText(CommonBundle.getCloseButtonText())
        myTipPanel = TipPanel()
        myTipPanel!!.nextTip()
        setDoNotAskOption(myTipPanel)
        horizontalStretch = 1.33f
        verticalStretch = 1.25f
        init()
        LaterInvocator.getCurrentModalEntities().stream()
                .forEach(System.out::println)
    }

    override fun getStyle(): DialogWrapper.DialogStyle {
        return DialogWrapper.DialogStyle.COMPACT
    }

    override fun createSouthPanel(): JComponent {
        val component = super.createSouthPanel()
        component.border = JBUI.Borders.empty(8, 12)
        return component
    }

    override fun createActions(): Array<Action> {
        return arrayOf(PreviousTipAction(), NextTipAction(), cancelAction)
    }

    override fun createCenterPanel(): JComponent? {
        return myTipPanel
    }

    public override fun dispose() {
        super.dispose()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return myPreferredFocusedComponent
    }

    private inner class PreviousTipAction : AbstractAction(IdeBundle.message("action.previous.tip")) {

        override fun actionPerformed(e: ActionEvent) {
            myTipPanel!!.prevTip()
            UsageTrigger.trigger("tips.of.the.day.prev")
        }
    }

    private inner class NextTipAction : AbstractAction(IdeBundle.message("action.next.tip")) {
        init {
            putValue(DialogWrapper.DEFAULT_ACTION, java.lang.Boolean.TRUE)
            putValue(DialogWrapper.FOCUSED_ACTION, java.lang.Boolean.TRUE) // myPreferredFocusedComponent
        }

        override fun actionPerformed(e: ActionEvent) {
            myTipPanel!!.nextTip()
            UsageTrigger.trigger("tips.of.the.day.next")
        }
    }

    companion object {
        fun createForProject(project: Project): WritingTipDialog {
            val w = WindowManagerEx.getInstanceEx().suggestParentWindow(project)
            return if (w == null) WritingTipDialog() else WritingTipDialog(w)
        }
    }
}
