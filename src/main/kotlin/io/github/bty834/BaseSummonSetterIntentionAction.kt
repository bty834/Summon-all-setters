package io.github.bty834

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDeclarationStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.util.PsiTreeUtil
import io.github.bty834.CommonUtil.Companion.getLocalVariableContainingClass

abstract class BaseSummonSetterIntentionAction: PsiElementBaseIntentionAction() {

    override fun getFamilyName(): String {
        return "Summon all setters"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {

        val localVarClazz: PsiClass = getLocalVariableContainingClass(element) ?: return false

        return CommonUtil.checkClazzHasValidSetters(localVarClazz)

    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val psiLocalVar: PsiLocalVariable =
            PsiTreeUtil.getParentOfType(element, PsiLocalVariable::class.java) ?: return

        if (psiLocalVar.parent !is PsiDeclarationStatement) {
            return
        }

        handleLocalVariable(psiLocalVar, project)
    }

    abstract fun handleLocalVariable(localVariable: PsiLocalVariable, project: Project)
}