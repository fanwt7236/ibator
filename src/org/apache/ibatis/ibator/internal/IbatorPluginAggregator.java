/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.ibatis.ibator.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.ibator.api.GeneratedJavaFile;
import org.apache.ibatis.ibator.api.GeneratedXmlFile;
import org.apache.ibatis.ibator.api.IbatorPlugin;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.Interface;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;
import org.apache.ibatis.ibator.api.dom.xml.Document;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;
import org.apache.ibatis.ibator.config.IbatorContext;

/**
 * This class is for internal use only. It contains a list of plugins for the
 * current context and is used to aggregate plugins together. This class
 * implements the rule that if any plugin returns "false" from a method, then no
 * other plugin is called.
 * <p>
 * This class does not follow the normal plugin lifecycle and should not be
 * subclassed by clients.
 * 
 * @author Jeff Butler
 * 
 */
public final class IbatorPluginAggregator implements IbatorPlugin {
    private List<IbatorPlugin> plugins;

    public IbatorPluginAggregator() {
        plugins = new ArrayList<IbatorPlugin>();
    }

    public void addPlugin(IbatorPlugin plugin) {
        plugins.add(plugin);
    }

    public void setIbatorContext(IbatorContext ibatorContext) {
        throw new UnsupportedOperationException();
    }

    public void setProperties(Properties properties) {
        throw new UnsupportedOperationException();
    }

    public boolean validate(List<String> warnings) {
        throw new UnsupportedOperationException();
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass tlc,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelBaseRecordClassGenerated(tlc, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass tlc,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelRecordWithBLOBsClassGenerated(tlc,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapCountByExampleElementGenerated(XmlElement element,
            IntrospectedTable table) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapCountByExampleElementGenerated(element, table)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element,
            IntrospectedTable table) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapDeleteByExampleElementGenerated(element, table)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
            IntrospectedTable table) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin
                    .sqlMapDeleteByPrimaryKeyElementGenerated(element, table)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean modelExampleClassGenerated(TopLevelClass tlc,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelExampleClassGenerated(tlc, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
        for (IbatorPlugin plugin : plugins) {
            List<GeneratedJavaFile> temp = plugin
                    .contextGenerateAdditionalJavaFiles(introspectedTable);
            if (temp != null) {
                answer.addAll(temp);
            }
        }
        return answer;
    }

    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(
            IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
        for (IbatorPlugin plugin : plugins) {
            List<GeneratedXmlFile> temp = plugin
                    .contextGenerateAdditionalXmlFiles(introspectedTable);
            if (temp != null) {
                answer.addAll(temp);
            }
        }
        return answer;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass tlc,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelPrimaryKeyClassGenerated(tlc, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin
                    .sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapExampleWhereClauseElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapInsertElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin
                    .sqlMapInsertElementGenerated(element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapResultMapWithBLOBsElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapSelectByExampleWithBLOBsElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapSelectByPrimaryKeyElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapGenerated(sqlMap, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByExampleSelectiveElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByExampleWithBLOBsElementGenerated(element,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
                    element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
                    element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
                    element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
                    element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoCountByExampleMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoCountByExampleMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoCountByExampleMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoCountByExampleMethodGenerated(method, topLevelClass,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoDeleteByExampleMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoDeleteByExampleMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoDeleteByExampleMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoDeleteByExampleMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoDeleteByPrimaryKeyMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoDeleteByPrimaryKeyMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoDeleteByPrimaryKeyMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoDeleteByPrimaryKeyMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoImplementationGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoImplementationGenerated(topLevelClass,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoInsertMethodGenerated(Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoInsertMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoInsertMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoInsertMethodGenerated(method, topLevelClass,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoInterfaceGenerated(Interface interfaze,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoInterfaceGenerated(interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByExampleWithBLOBsMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByExampleWithBLOBsMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByExampleWithBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByExampleWithBLOBsMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByExampleWithoutBLOBsMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByExampleWithoutBLOBsMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByExampleWithoutBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByExampleWithoutBLOBsMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByPrimaryKeyMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByPrimaryKeyMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoSelectByPrimaryKeyMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoSelectByPrimaryKeyMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleSelectiveMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleSelectiveMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleSelectiveMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleSelectiveMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleWithBLOBsMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleWithBLOBsMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleWithBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleWithBLOBsMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleWithoutBLOBsMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleWithoutBLOBsMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByExampleWithoutBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByExampleWithoutBLOBsMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeySelectiveMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeySelectiveMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeySelectiveMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeySelectiveMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method,
            Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeyWithBLOBsMethodGenerated(method,
                    interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method,
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeyWithBLOBsMethodGenerated(method,
                    topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
            Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
                    method, interfaze, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
            Method method, TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
                    method, topLevelClass, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
        for (IbatorPlugin plugin : plugins) {
            List<GeneratedJavaFile> temp = plugin
                    .contextGenerateAdditionalJavaFiles();
            if (temp != null) {
                answer.addAll(temp);
            }
        }
        return answer;
    }

    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
        for (IbatorPlugin plugin : plugins) {
            List<GeneratedXmlFile> temp = plugin
                    .contextGenerateAdditionalXmlFiles();
            if (temp != null) {
                answer.addAll(temp);
            }
        }
        return answer;
    }

    public boolean sqlMapDocumentGenerated(Document document,
            IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.sqlMapDocumentGenerated(document, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
            IbatorPlugin.ModelClassType modelClassType) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
            IbatorPlugin.ModelClassType modelClassType) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
            IbatorPlugin.ModelClassType modelClassType) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin
                    .sqlMapInsertSelectiveElementGenerated(element, introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoInsertSelectiveMethodGenerated(method, interfaze,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public boolean daoInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean rc = true;

        for (IbatorPlugin plugin : plugins) {
            if (!plugin.daoInsertSelectiveMethodGenerated(method, topLevelClass,
                    introspectedTable)) {
                rc = false;
                break;
            }
        }

        return rc;
    }

    public void initialized(IntrospectedTable introspectedTable) {
        for (IbatorPlugin plugin : plugins) {
            plugin.initialized(introspectedTable);
        }
    }
}
