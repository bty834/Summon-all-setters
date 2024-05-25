package io.github.bty834

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField

class PsiClazzUtil {

    companion object {


        fun checkClazzHasValidSetters(psiClass: PsiClass?): Boolean {
            psiClass ?: return false

            if (psiClass.hasAnnotation("lombok.Setter")
                || psiClass.hasAnnotation("lombok.Data")
            ) return true

            val fields: Array<PsiField> = psiClass.allFields

            if (fields.any { it.hasAnnotation("lombok.Setter") }) {
                return true
            }

            if (psiClass.allMethods.any { it.name.startsWith("set") }) {
                return true
            }

            return false
        }

        fun getSetterMethodNames(psiClass: PsiClass?): List<String> {
            val methodNameSet: MutableSet<String> = HashSet()
            psiClass ?: return emptyList()

            if (psiClass.hasAnnotation("lombok.Setter")
                || psiClass.hasAnnotation("lombok.Data")
            ) {
                psiClass.allFields
                    .map { "set" + it.name[0].uppercaseChar() + it.name.substring(1) }
                    .forEach { methodNameSet.add(it) }
            }
            val fields: Array<PsiField> = psiClass.allFields

            fields.filter { it.hasAnnotation("lombok.Setter") }
                .map { "set" + it.name[0].uppercaseChar() + it.name.substring(1) }
                .forEach { methodNameSet.add(it) }

            psiClass.allMethods.filter { it.name.startsWith("set") }
                .forEach { methodNameSet.add(it.name) }

            return methodNameSet.toList()
        }


        fun getIndentSpaceNumsOfCurrentLine(document: Document, statementOffset: Int): Int {
            val lineNumber = document.getLineNumber(statementOffset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            return (statementOffset - lineStartOffset).coerceAtLeast(0)
        }

    }
}