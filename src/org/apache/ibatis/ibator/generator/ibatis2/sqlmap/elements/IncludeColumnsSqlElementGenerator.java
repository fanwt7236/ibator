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

import java.util.List;

import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.TextElement;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;

/**
 * 
 * @author fanwt7236@163.com
 *
 */
public class IncludeColumnsSqlElementGenerator extends
        AbstractXmlElementGenerator {

    public IncludeColumnsSqlElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

        answer.addAttribute(new Attribute("id", "BaseColumns")); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();

        List<IntrospectedColumn> columns = this.introspectedTable.getAllColumns();
        boolean isFirst = true;
        for(IntrospectedColumn column : columns){
        	if(!isFirst){
        		sb.append(",");
        	}else{
        		isFirst = false;
        	}
        	sb.append(column.getEscapedColumnName());
        }
        
        answer.addElement(new TextElement(sb.toString()));
        parentElement.addElement(answer);
    }
}
