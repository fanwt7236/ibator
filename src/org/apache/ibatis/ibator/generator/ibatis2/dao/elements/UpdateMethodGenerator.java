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
package org.apache.ibatis.ibator.generator.ibatis2.dao.elements;

import java.util.Set;
import java.util.TreeSet;

import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.Interface;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.Parameter;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;

/**
 * 
 * @author Jeff Butler
 *
 */
public class UpdateMethodGenerator extends AbstractDAOElementGenerator {

    public UpdateMethodGenerator() {
        super();
    }

    @Override
    public void addImplementationElements(TopLevelClass topLevelClass) {
    	 Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
         Method method = getMethodShell(importedTypes);

         StringBuilder sb = new StringBuilder();
         sb.setLength(0);
         sb.append("int rows = "); //$NON-NLS-1$
         sb.append(daoTemplate.getUpdateMethod(introspectedTable.getBaseRecordType().getFullyQualifiedName(), getDAOMethodNameCalculator()
                 .getUpdateMethodName(introspectedTable), paramBaseRecord())); //$NON-NLS-1$
         method.addBodyLine(sb.toString());
         method.addBodyLine("return rows;"); //$NON-NLS-1$

         if (ibatorContext.getPlugins().daoUpdateGenerated(method, topLevelClass, introspectedTable)) {
             topLevelClass.addImportedTypes(importedTypes);
             topLevelClass.addMethod(method);
         }
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
    	Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);
        
        if (ibatorContext.getPlugins().daoUpdateGenerated(method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
    	FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(getDAOMethodNameCalculator()
                .getUpdateMethodName(introspectedTable));
        method.addParameter(new Parameter(introspectedTable.getBaseRecordType(), paramBaseRecord()));

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            importedTypes.add(fqjt);
        }
        
        importedTypes.add(introspectedTable.getBaseRecordType());

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method,
                table);

        return method;
    }
}
