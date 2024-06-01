package io.github.bty834

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class MyIntentionExtension : PsiElementBaseIntentionAction(), HighPriorityAction {

    override fun getFamilyName(): String {
        TODO("一组extension共用的名称，我们这里定义的两个extension使用同一个familyName")
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        TODO("判断当前光标处是否可以展示该intention")
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        TODO("运行intention extension")
    }

    override fun getText(): String {
        TODO("intention展示时的名称")
    }

    override fun getPriority(): PriorityAction.Priority {
        // intention的优先级排序，com.intellij.codeInsight.intention.HighPriorityAction接口的方法
        return PriorityAction.Priority.TOP
    }
}