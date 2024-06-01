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

            if (psiClass.allMethods
                    .filter {
                        it.hasModifierProperty(PsiModifier.PUBLIC)
                                && it.name.startsWith("set")
                                && !it.hasModifierProperty(PsiModifier.STATIC)
                                && !it.hasModifierProperty(PsiModifier.ABSTRACT)
                                && !it.hasModifierProperty(PsiModifier.DEFAULT)
                                && !it.hasModifierProperty(PsiModifier.NATIVE)
                    }
                    .any { it.name.startsWith("set") }) {
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
                    .filter { !it.hasModifierProperty(PsiModifier.STATIC) }
                    .map { "set" + it.name[0].uppercaseChar() + it.name.substring(1) }
                    .forEach { methodNameSet.add(it) }
            }
            val fields: Array<PsiField> = psiClass.allFields

            fields.filter { it.hasAnnotation("lombok.Setter") }
                .filter { !it.hasModifierProperty(PsiModifier.STATIC) }
                .map { "set" + it.name[0].uppercaseChar() + it.name.substring(1) }
                .forEach { methodNameSet.add(it) }

            psiClass.allMethods.filter {
                it.hasModifierProperty(PsiModifier.PUBLIC)
                        && it.name.startsWith("set")
                        && !it.hasModifierProperty(PsiModifier.STATIC)
                        && !it.hasModifierProperty(PsiModifier.ABSTRACT)
                        && !it.hasModifierProperty(PsiModifier.DEFAULT)
                        && !it.hasModifierProperty(PsiModifier.NATIVE)
            }
                .forEach { methodNameSet.add(it.name) }

            return methodNameSet.toList()
        }

        private val type2DefaultValue: MutableMap<String, String> = HashMap()

        init {
            type2DefaultValue["int"] = "0"
            type2DefaultValue["java.lang.Integer"] = "0"

            type2DefaultValue["long"] = "0L"
            type2DefaultValue["java.lang.Long"] = "0L"

            type2DefaultValue["float"] = "0f"
            type2DefaultValue["java.lang.Float"] = "0f"

            type2DefaultValue["double"] = "0d"
            type2DefaultValue["java.lang.Double"] = "0d"

            type2DefaultValue["char"] = "' '"
            type2DefaultValue["java.lang.Character"] = "' '"

            type2DefaultValue["byte"] = "0"
            type2DefaultValue["java.lang.Byte"] = "0"

            type2DefaultValue["boolean"] = "false"
            type2DefaultValue["java.lang.Boolean"] = "false"

            type2DefaultValue["java.lang.String"] = "\"\""
        }

        fun getDefaultValueForType(type: String): String {
            return type2DefaultValue.getOrDefault(type, "null")
        }

        fun getSetterMethodName2Type(psiClass: PsiClass?): Map<String, String> {
            val methodName2Type: MutableMap<String, String> = HashMap()
            psiClass ?: return emptyMap()

            if (psiClass.hasAnnotation("lombok.Setter")
                || psiClass.hasAnnotation("lombok.Data")
            ) {
                psiClass.allFields
                    .filter { !it.hasModifierProperty(PsiModifier.STATIC) }
                    .forEach {
                        val name = "set" + it.name[0].uppercaseChar() + it.name.substring(1)
                        val type = it.type.canonicalText
                        methodName2Type[name] = type
                    }
            }
            val fields: Array<PsiField> = psiClass.allFields

            fields.filter { it.hasAnnotation("lombok.Setter") }
                .filter { !it.hasModifierProperty(PsiModifier.STATIC) }
                .forEach {
                    val name = "set" + it.name[0].uppercaseChar() + it.name.substring(1)
                    val type = it.type.canonicalText
                    methodName2Type[name] = type
                }

            psiClass.allMethods.filter {
                it.hasModifierProperty(PsiModifier.PUBLIC)
                        && it.name.startsWith("set")
                        && !it.hasModifierProperty(PsiModifier.STATIC)
                        && !it.hasModifierProperty(PsiModifier.ABSTRACT)
                        && !it.hasModifierProperty(PsiModifier.DEFAULT)
                        && !it.hasModifierProperty(PsiModifier.NATIVE)
            }
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