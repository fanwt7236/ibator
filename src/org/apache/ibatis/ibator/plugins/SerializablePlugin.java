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
package org.apache.ibatis.ibator.plugins;

import java.util.List;

import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.IbatorPluginAdapter;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;

/**
 * This plugin adds the java.io.Serializable marker interface to all generated
 * model objects.
 * <p>
 * This plugin demonstrates adding capabilities to generated Java artifacts,
 * and shows the proper way to add imports to a compilation unit.
 * <p>
 * Important: This is a simplistic implementation of serializable and does
 * not attempt to do any versioning of classes.
 * 
 * @author Jeff Butler
 *
 */
public class SerializablePlugin extends IbatorPluginAdapter {
    
    private FullyQualifiedJavaType serializable;

    public SerializablePlugin() {
        super();
        serializable = new FullyQualifiedJavaType("java.io.Serializable"); //$NON-NLS-1$

    }

    public boolean validate(List<String> warnings) {
        // this plugin is always valid
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable.getFullyQualifiedTable());
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable.getFullyQualifiedTable());
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeSerializable(topLevelClass, introspectedTable.getFullyQualifiedTable());
        return true;
    }
    
    protected void makeSerializable(TopLevelClass topLevelClass, FullyQualifiedTable table) {
        topLevelClass.addImportedType(serializable);
        topLevelClass.addSuperInterface(serializable);

        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L"); //$NON-NLS-1$
        field.setName("serialVersionUID"); //$NON-NLS-1$
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
        field.setVisibility(JavaVisibility.PRIVATE);
        ibatorContext.getCommentGenerator().addFieldComment(field, table);

        topLevelClass.addField(field);
    }
}
