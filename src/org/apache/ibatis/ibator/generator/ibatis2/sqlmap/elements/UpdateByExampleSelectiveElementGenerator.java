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
import org.apache.ibatis.ibator.generator.ibatis2.XmlConstants;

/**
 * 
 * @author Jeff Butler
 *
 */
public class UpdateByExampleSelectiveElementGenerator extends
        AbstractXmlElementGenerator {

    public UpdateByExampleSelectiveElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("update"); //$NON-NLS-1$
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();

        answer.addAttribute(new Attribute(
                "id", XmlConstants.UPDATE_BY_EXAMPLE_SELECTIVE_STATEMENT_ID)); //$NON-NLS-1$

        ibatorContext.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(table.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("dynamic"); //$NON-NLS-1$
        dynamicElement.addAttribute(new Attribute("prepend", "set")); //$NON-NLS-1$ //$NON-NLS-2$
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("isNotNull"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("prepend", ",")); //$NON-NLS-1$ //$NON-NLS-2$
            isNotNullElement.addAttribute(new Attribute("property", introspectedColumn.getJavaProperty("record."))); //$NON-NLS-1$ //$NON-NLS-2$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(introspectedColumn.getAliasedEscapedColumnName());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getIbatisFormattedParameterClause("record.")); //$NON-NLS-1$
            
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        XmlElement isParameterPresentElement =
            new XmlElement("isParameterPresent"); //$NON-NLS-1$
        answer.addElement(isParameterPresentElement);
        
        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
                table.getSqlMapNamespace() + "." + XmlConstants.EXAMPLE_WHERE_CLAUSE_ID)); //$NON-NLS-1$
        isParameterPresentElement.addElement(includeElement);

        if (ibatorContext.getPlugins().sqlMapUpdateByExampleSelectiveElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
