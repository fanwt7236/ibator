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

import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;
import org.apache.ibatis.ibator.config.PropertyRegistry;
import org.apache.ibatis.ibator.generator.ibatis2.XmlConstants;
import org.apache.ibatis.ibator.internal.util.StringUtility;

/**
 * 
 * @author Jeff Butler
 *
 */
public class ResultMapWithoutBLOBsElementGenerator extends AbstractXmlElementGenerator {

    public ResultMapWithoutBLOBsElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        boolean useColumnIndex =
            StringUtility.isTrue(introspectedTable.getTableConfigurationProperty(PropertyRegistry.TABLE_USE_COLUMN_INDEXES));
        XmlElement answer = new XmlElement("resultMap"); //$NON-NLS-1$
        answer.addAttribute(new Attribute("id", //$NON-NLS-1$
                XmlConstants.BASE_RESULT_MAP_ID));

        FullyQualifiedJavaType returnType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            returnType = introspectedTable.getBaseRecordType();
        } else {
            returnType = introspectedTable.getPrimaryKeyType();
        }
        
        answer.addAttribute(new Attribute("class", //$NON-NLS-1$
                returnType.getFullyQualifiedName()));

        ibatorContext.getCommentGenerator().addComment(answer);

        int i = 1;
        if (StringUtility.stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId())
                || StringUtility.stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
            i++;
        }
        
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
            XmlElement resultElement = new XmlElement("result"); //$NON-NLS-1$

            if (useColumnIndex) {
                resultElement.addAttribute(new Attribute(
                        "columnIndex", Integer.toString(i++))); //$NON-NLS-1$
            } else {
                resultElement.addAttribute(new Attribute(
                    "column", introspectedColumn.getRenamedColumnNameForResultMap())); //$NON-NLS-1$
            }
            
            resultElement.addAttribute(new Attribute(
                    "property", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
            resultElement.addAttribute(new Attribute("jdbcType", //$NON-NLS-1$
                    introspectedColumn.getJdbcTypeName()));

            if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
                resultElement.addAttribute(new Attribute(
                        "typeHandler", introspectedColumn.getTypeHandler())); //$NON-NLS-1$
            }

            answer.addElement(resultElement);
        }

        if (ibatorContext.getPlugins().sqlMapResultMapWithoutBLOBsElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
