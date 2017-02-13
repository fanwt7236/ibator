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
 * @author Jeff Butler
 *
 */
public class InsertBatchElementGenerator extends AbstractXmlElementGenerator {
    
    public InsertBatchElementGenerator() {
        super();
    }
    
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        answer.addAttribute(new Attribute("id", "insertBatch")); //$NON-NLS-1$
        
        answer.addAttribute(new Attribute("parameterClass", "java.util.List"));
        
        ibatorContext.getCommentGenerator().addComment(answer);

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into "); //$NON-NLS-1$
        insertClause.append(table.getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" ("); //$NON-NLS-1$


        boolean comma = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            if (comma) {
                insertClause.append(", "); 
            } else {
                comma = true; 
            }

            insertClause.append(introspectedColumn.getEscapedColumnName());
        }
        insertClause.append(')');
        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append(" values "); //$NON-NLS-1$
        
        XmlElement ixe = new XmlElement("iterate");
        ixe.addAttribute(new Attribute("conjunction", ","));
        StringBuilder iv = new StringBuilder("(");
        comma = false;
        for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()){
        	if (comma) {
        		iv.append(", "); 
            } else {
                comma = true; 
            }
        	iv.append(introspectedColumn.getIbatisFormattedParameterClause("[]."));
        }
        iv.append(")");
        ixe.addElement(new TextElement(iv.toString()));
        answer.addElement(new TextElement(insertClause.toString()));
        answer.addElement(new TextElement(valuesClause.toString()));
        answer.addElement(ixe);

        if (ibatorContext.getPlugins().sqlMapInsertBatchGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
