package io.pcp.parfait

import org.apache.log4j.Logger
import javax.management.MBeanServerConnection
import javax.management.ObjectInstance
import javax.management.openmbean.CompositeData

private val logger = Logger.getLogger(Specification::class.java)

class SpecificationMBeans {
    companion object {

        @JvmStatic
        fun gatherMBeansData(server: MBeanServerConnection): List<Specification> {
            val specifications = mutableListOf<Specification>()

            val allMBeans = server.queryMBeans(null, null)
            val iterator: Iterator<ObjectInstance> = allMBeans.iterator()

            while (iterator.hasNext()) {
                val mBean = iterator.next()
                val mBeanInfo = server.getMBeanInfo(mBean.objectName)
                val parfaitFormatMBeanName = canonicalMBeanNameToParfaitSpecification(mBean.objectName.canonicalName)

                for (attribute in mBeanInfo.attributes) {
                    if (attribute.type.contains("CompositeData")) {
                        try {
                            val compositeAttribute = server.getAttribute(mBean.objectName, attribute.name) as CompositeData
                            compositeAttribute.compositeType.keySet().forEach {
                                specifications.add(
                                    Specification(
                                        pcpMetricNameFromMBean(mBean.className, attribute.name, attribute.name),
                                        true,
                                        pcpMetricNameFromMBean(mBean.className, attribute.name, attribute.name),
                                        null,
                                        "value",
                                        parfaitFormatMBeanName,
                                        attribute.name,
                                        it
                                    )
                                )
                            }
                        } catch (ex: Exception) {
                            logger.warn("Unsupported CompositeData Type - ${attribute.type}")
                        }
                    } else {
                        specifications.add(
                            Specification(
                                pcpMetricNameFromMBean(mBean.className, attribute.name, null),
                                true,
                                pcpMetricNameFromMBean(mBean.className, attribute.name, null),
                                null,
                                "value",
                                parfaitFormatMBeanName,
                                attribute.name,
                                null
                            )
                        )
                    }
                }
            }
            return specifications
        }

        private fun canonicalMBeanNameToParfaitSpecification(canonicalMBeanName: String) =
            if (canonicalMBeanName.contains("type=") &&
                canonicalMBeanName.contains("name=") &&
                (canonicalMBeanName.lastIndexOf("type=") > canonicalMBeanName.lastIndexOf("name="))
            ) {
                val (mBeanClassName, mBeanProps) = canonicalMBeanName.split(":")
                val mBeanPropsElements = mBeanProps.split(",")
                mBeanClassName + ":" +
                        mBeanPropsElements.find { it.startsWith("type") } + "," +
                        mBeanPropsElements.find { it.startsWith("name") }
            } else {
                canonicalMBeanName
            }

        private fun pcpMetricNameFromMBean(
            mBeanClassName: String,
            mBeanAttribute: String,
            mBeanAttributeCompositeElement: String?
        ): String {
            val compositeDataElement = mBeanAttributeCompositeElement?.let { "[$it]" } ?: ""
            return "${mBeanClassName.lowercase()}.${mBeanAttribute.lowercase()}$compositeDataElement"
        }
    }

}

