package io.github.bty834

import com.intellij.codeInsight.intention.FileModifier
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil

class SummonSettersIntentionAction : PsiElementBaseIntentionAction() , HighPriorityAction {
    override fun getFamilyName(): String {
        return "Summon all setters"
    }

    override fun getText(): String {
        return "Summon all setters without value"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {

        val localVarClazz: PsiClass = getLocalVariableContainingClass(element) ?: return false

        return PsiClazzUtil.checkClazzHasValidSetters(localVarClazz)

    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {

        val psiLocalVar: PsiLocalVariable =
            PsiTreeUtil.getParentOfType(element, PsiLocalVariable::class.java) ?: return

        if (psiLocalVar.parent !is PsiDeclarationStatement) {
            return
        }

        handleWithLocalVariable(psiLocalVar, project)

    }

    private fun handleWithLocalVariable(localVariable: PsiLocalVariable, project: Project) {
        val psiClass = PsiTypesUtil.getPsiClass(localVariable.type)
        val variableName: String = localVariable.name

        val setterMethodNames: List<String> = PsiClazzUtil.getSetterMethodNames(psiClass)

        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        val containingFile: PsiFile = localVariable.containingFile
        val document = psiDocumentManager.getDocument(containingFile) ?: return

        val indentNum: Int = PsiClazzUtil.getIndentSpaceNumsOfCurrentLine(document, localVariable.parent.textOffset)

        val insertSetterStr: StringBuilder = StringBuilder()
        setterMethodNames.forEach {
            // 缩进
            insertSetterStr.append(" ".repeat(indentNum))
            insertSetterStr.append("$variableName.$it();\n")
        }

        document.insertString(localVariable.parent.textOffset + localVariable.parent.textLength + 1, insertSetterStr.toString())
        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
        psiDocumentManager.commitDocument(document)
        FileDocumentManager.getInstance().saveDocument(document)
    }


    private fun getLocalVariableContainingClass(psiElement: PsiElement): PsiClass? {

        val psiLocalVar: PsiLocalVariable =
            PsiTreeUtil.getParentOfType(psiElement, PsiLocalVariable::class.java) ?: return null

        if (psiLocalVar.parent !is PsiDeclarationStatement) {
            return null
        }

        return PsiTypesUtil.getPsiClass(psiLocalVar.type)
    }

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.TOP
    }
}