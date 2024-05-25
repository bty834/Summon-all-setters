package io.github.bty834

import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.util.PsiTypesUtil

class SummonSettersWithDefaultsIntentionAction : BaseSummonSetterIntentionAction() , HighPriorityAction {

    override fun getText(): String {
        return "Summon all setters with default value"
    }

    override fun getPriority(): PriorityAction.Priority {
        return PriorityAction.Priority.TOP
    }

    override fun handleLocalVariable(localVariable: PsiLocalVariable, project: Project) {
        val psiClass = PsiTypesUtil.getPsiClass(localVariable.type)
        val variableName: String = localVariable.name

        val setterMethodName2Type: Map<String, String> = CommonUtil.getSetterMethodName2Type(psiClass)

        val psiDocumentManager = PsiDocumentManager.getInstance(project)
        val containingFile: PsiFile = localVariable.containingFile
        val document = psiDocumentManager.getDocument(containingFile) ?: return

        val indentNum: Int = CommonUtil.getIndentSpaceNumsOfCurrentLine(document, localVariable.parent.textOffset)

        val insertSetterStr: StringBuilder = StringBuilder()
        setterMethodName2Type.forEach {

            val defaultValue = CommonUtil.getDefaultValueForType(it.value)
            // 缩进
            insertSetterStr.append(" ".repeat(indentNum))
            insertSetterStr.append("$variableName.${it.key}($defaultValue);\n")
        }

        document.insertString(localVariable.parent.textOffset + localVariable.parent.textLength + 1, insertSetterStr.toString())
        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
        psiDocumentManager.commitDocument(document)
        FileDocumentManager.getInstance().saveDocument(document)
    }
}