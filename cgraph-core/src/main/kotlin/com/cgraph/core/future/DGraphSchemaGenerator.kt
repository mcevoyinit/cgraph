package com.cgraph.core.future

import com.cgraph.core.states.GraphableState
import com.cgraph.core.support.MapOfMaps
import io.github.classgraph.ClassGraph

class DGraphSchemaGenerator : CGraphSchemaGenerator {

    override fun scanCordapps(): List<Class<*>>? {
        val detectedStates = ClassGraph()
            .enableClassInfo()
            .scan()
            .getClassesImplementing(GraphableState::class.qualifiedName)
            .loadClasses()

        /**
         * ClassGraph()
        .enableAllInfo()
        .scan()
        .getClassesImplementing(GraphableState::class.qualifiedName)
        .map {
        //val allFields =
        it::class.declaredMemberProperties
        .map {
        it.name to it.returnType.classifier as KClass<*>
        }.toMap()
        // allFields
        }
         */
        return detectedStates
    }

    override fun generateSchema(states: List<Class<*>>?): MapOfMaps? {
        /*      states.map {

        }
        val typeName = states?.simpleName
        val properties = clazz?.fields
        val entity = """
        type ${typeName} {
            ${properties?.map {
            "${it.name} : ${it.genericType}"
        }}
        } 
    """.trimIndent()
        return entity as MapOfMaps*

   */
        return emptyMap()
    }
}

/*
ClassGraph()
    .enableAllInfo()
    .scan()
    .getClassesImplementing(GraphableState::class.qualifiedName)
    .map { //val allFields =
            it::class.declaredMemberProperties
                .map {
                    it.name to it.returnType.classifier as KClass<*>
                }.toMap()
       // allFields
    }

[class kotlin.String, class kotlin.Int, class kotlin.Boolean, class kotlin.Boolean, class kotlin.Int, class kotlin.Int, class kotlin.String, class io.github.classgraph.ClassTypeSignature, class kotlin.String, class kotlin.Boolean, class kotlin.Boolean, class io.github.classgraph.ClasspathElement, class io.github.classgraph.Resource, class java.lang.ClassLoader, class io.github.classgraph.ModuleInfo, class io.github.classgraph.PackageInfo, class io.github.classgraph.AnnotationInfoList, class io.github.classgraph.FieldInfoList, class io.github.classgraph.MethodInfoList, class io.github.classgraph.AnnotationParameterValueList, class kotlin.collections.Set, class io.github.classgraph.ClassInfoList, class kotlin.Boolean, class kotlin.collections.Map, class kotlin.collections.List]
 */