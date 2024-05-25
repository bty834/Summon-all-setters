package io.github.bty834

import com.intellij.openapi.editor.Document
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil

class CommonUtil {

    companion object {

        fun getLocalVariableContainingClass(psiElement: PsiElement): PsiClass? {

            val psiLocalVar: PsiLocalVariable =
                PsiTreeUtil.getParentOfType(psiElement, PsiLocalVariable::class.java) ?: return null

            if (psiLocalVar.parent !is PsiDeclarationStatement) {
                return null
            }

            return PsiTypesUtil.getPsiClass(psiLocalVar.type)
        }

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

        fun getDefaultValueForType(type: String): String {
            if (type == "int" || type == "java.lang.Integer") {
                return "0"
            }
            if (type == "long" || type == "java.lang.Long") {
                return "0L"
            }
            if (type == "float" || type == "java.lang.Float") {
                return "0f"
            }
            if (type == "double" || type == "java.lang.Double") {
                return "0d"
            }
            if (type == "boolean" || type == "java.lang.Boolean") {
                return "false"
            }
            if (type == "char" || type == "java.lang.Character") {
                return "''"
            }
            if (type == "java.lang.String") {
                return "\"\""
            }
            return "null"
        }

        fun getSetterMethodName2Type(psiClass: PsiClass?): Map<String, String> {
            val methodName2Type: MutableMap<String, String> = HashMap()
            psiClass ?: return emptyMap()

            if (psiClass.hasAnnotation("lombok.Setter")
                || psiClass.hasAnnotation("lombok.Data")
            ) {
                psiClass.allFields
                    .forEach {
                        val name = "set" + it.name[0].uppercaseChar() + it.name.substring(1)
                        val type = it.type.canonicalText
                        methodName2Type[name] = type
                    }
            }
            val fields: Array<PsiField> = psiClass.allFields

            fields.filter { it.hasAnnotation("lombok.Setter") }
                .forEach {
                    val name = "set" + it.name[0].uppercaseChar() + it.name.substring(1)
                    val type = it.type.canonicalText
                    methodName2Type[name] = type
                }

            psiClass.allMethods.filter { it.name.startsWith("set") }
                .forEach {
                    val name = it.name
                    val type = it.parameterList.getParameter(0)?.type?.canonicalText ?: ""
                    methodName2Type[name] = type
                }

            return methodName2Type
        }


        fun getIndentSpaceNumsOfCurrentLine(document: Document, statementOffset: Int): Int {
            val lineNumber = document.getLineNumber(statementOffset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            return (statementOffset - lineStartOffset).coerceAtLeast(0)
        }

    }
}