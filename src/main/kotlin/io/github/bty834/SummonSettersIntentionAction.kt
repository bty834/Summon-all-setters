package io.github.bty834

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.util.PsiTypesUtil

class SummonSettersIntentionAction : BaseSummonSetterIntentionAction() , HighPriorityAction {

    override fun getText(): String {
        return "Summon all setters without value"
    }

    override fun handleLocalVariable(localVariable: PsiLocalVariable, project: Project) {
        val psiClass = PsiTypesUtil.getPsiClass(localVariable.type)
        val variableName: String = localVariable.name

        val setterMethodNames: List<String> = CommonUtil.getSetterMethodNames(psiClass)

        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        val containingFile: PsiFile = localVariable.containingFile
        val document = psiDocumentManager.getDocument(containingFile) ?: return

        val indentNum: Int = CommonUtil.getIndentSpaceNumsOfCurrentLine(document, localVariable.parent.textOffset)

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

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.TOP
    }
}