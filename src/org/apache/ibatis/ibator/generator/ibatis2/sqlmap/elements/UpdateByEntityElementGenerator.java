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
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.TextElement;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;

/**
 * 
 * @author fanwt7236@163.com
 *
 */
public class UpdateByEntityElementGenerator extends
        AbstractXmlElementGenerator {

    public UpdateByEntityElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();

        answer.addAttribute(new Attribute(
                "id", "updateByEntity")); //$NON-NLS-1$

        answer.addAttribute(new Attribute("parameterClass", //$NON-NLS-1$
                "java.util.Map"));

        ibatorContext.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(table.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("dynamic"); //$NON-NLS-1$
        dynamicElement.addAttribute(new Attribute("prepend", "set")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement isNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("prepend", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            isNotNullElement.addAttribute(new Attribute("property", "s." + introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getEscapedColumnName());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getIbatisFormattedParameterClause("s."));
            
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        dynamicElement = new XmlElement("dynamic"); //$NON-NLS-1$
        dynamicElement.addAttribute(new Attribute("prepend", "where")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("prepend", "and")); //$NON-NLS-1$ //$NON-NLS-2$
            isNotNullElement.addAttribute(new Attribute("property", "w." + introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getEscapedColumnName());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getIbatisFormattedParameterClause("w."));
            
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        
        if (ibatorContext.getPlugins().sqlMapUpdateByEntityGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
