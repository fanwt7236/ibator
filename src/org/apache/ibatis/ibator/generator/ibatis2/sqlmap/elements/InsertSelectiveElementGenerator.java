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
package org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements;

import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.TextElement;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;
import org.apache.ibatis.ibator.config.GeneratedKey;
import org.apache.ibatis.ibator.generator.ibatis2.XmlConstants;

/**
 * 
 * @author Jeff Butler
 *
 */
public class InsertSelectiveElementGenerator extends AbstractXmlElementGenerator {

    public InsertSelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        answer.addAttribute(new Attribute("id", XmlConstants.INSERT_SELECTIVE_STATEMENT_ID)); //$NON-NLS-1$
        
        FullyQualifiedJavaType parameterType =
            introspectedTable.getRules().calculateAllFieldsClass();
        
        answer.addAttribute(new Attribute("parameterClass", //$NON-NLS-1$
                parameterType.getFullyQualifiedName()));

        ibatorContext.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();

        if (gk != null && gk.isBeforeInsert()) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                // pre-generated key
                answer.addElement(getSelectKey(introspectedColumn, gk));
            }
        }
        
        StringBuilder sb = new StringBuilder();

        sb.append("insert into "); //$NON-NLS-1$
        sb.append(table.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        
        XmlElement insertElement = new XmlElement("dynamic"); //$NON-NLS-1$
        insertElement.addAttribute(new Attribute("prepend", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(insertElement);
        
        answer.addElement(new TextElement("values")); //$NON-NLS-1$

        XmlElement valuesElement = new XmlElement("dynamic"); //$NON-NLS-1$
        valuesElement.addAttribute(new Attribute("prepend", "(")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(valuesElement);
        
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (introspectedColumn.isIdentity()) {
                // cannot set values on identity fields
                continue;
            }
            
            XmlElement insertNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
            insertNotNullElement.addAttribute(new Attribute("prepend", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            insertNotNullElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            insertNotNullElement.addElement(new TextElement(introspectedColumn.getEscapedColumnName()));
            insertElement.addElement(insertNotNullElement);
            
            XmlElement valuesNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
            valuesNotNullElement.addAttribute(new Attribute("prepend", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            valuesNotNullElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            valuesNotNullElement.addElement(new TextElement(introspectedColumn.getIbatisFormattedParameterClause()));
            valuesElement.addElement(valuesNotNullElement);
        }
        
        insertElement.addElement(new TextElement(")")); //$NON-NLS-1$
        valuesElement.addElement(new TextElement(")")); //$NON-NLS-1$
        
        if (gk != null && !gk.isBeforeInsert()) {
            IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                // pre-generated key
                answer.addElement(getSelectKey(introspectedColumn, gk));
            }
        }

        if (ibatorContext.getPlugins().sqlMapInsertSelectiveElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
