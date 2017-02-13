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

import org.apache.ibatis.ibator.api.DAOMethodNameCalculator;
import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.Interface;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.Parameter;
import org.apache.ibatis.ibator.api.dom.java.PrimitiveTypeWrapper;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;

/**
 * 
 * @author fanwt7236@163.com	
 *
 */
public class InsertBatchMethodGenerator extends AbstractDAOElementGenerator {

    public InsertBatchMethodGenerator() {
        super();
    }

    @Override
    public void addImplementationElements(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);
        
        FullyQualifiedJavaType returnType = method.getReturnType();

        StringBuilder sb = new StringBuilder();

        if (returnType != null) {
            sb.append("Object newKey = "); //$NON-NLS-1$
        }

        sb.append(daoTemplate.getInsertMethod(introspectedTable.getBaseRecordType().getFullyQualifiedName(),
        		getDAOMethodNameCalculator().getInsertBatchMethodName(introspectedTable), "list")); //$NON-NLS-1$
        method.addBodyLine(sb.toString());

        if (returnType != null) {
            if ("Object".equals(returnType.getShortName())) { //$NON-NLS-1$
                // no need to cast if the return type is Object
                method.addBodyLine("return newKey;"); //$NON-NLS-1$
            } else {
                sb.setLength(0);

                if (returnType.isPrimitive()) {
                    PrimitiveTypeWrapper ptw = returnType
                            .getPrimitiveTypeWrapper();
                    sb.append("return (("); //$NON-NLS-1$
                    sb.append(ptw.getShortName());
                    sb.append(") newKey"); //$NON-NLS-1$
                    sb.append(")."); //$NON-NLS-1$
                    sb.append(ptw.getToPrimitiveMethod());
                    sb.append(';');
                } else {
                    sb.append("return ("); //$NON-NLS-1$
                    sb.append(returnType.getShortName());
                    sb.append(") newKey;"); //$NON-NLS-1$
                }

                method.addBodyLine(sb.toString());
            }
        }
        
        if (ibatorContext.getPlugins().daoInsertBatchGenerated(method, topLevelClass, introspectedTable)) {
            topLevelClass.addImportedTypes(importedTypes);
            topLevelClass.addMethod(method);
        }
    }
    
    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);
        
        if (ibatorContext.getPlugins().daoInsertBatchGenerated(method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }
    
    private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        DAOMethodNameCalculator methodNameCalculator = getDAOMethodNameCalculator();
        method.setName(methodNameCalculator
                .getInsertBatchMethodName(introspectedTable));

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();
        FullyQualifiedJavaType lpt = new FullyQualifiedJavaType("java.util.List");
        lpt.setExtName(introspectedTable.getBaseRecordType().getShortName());
        importedTypes.add(new FullyQualifiedJavaType("java.util.List"));
        importedTypes.add(parameterType);
        method.addParameter(new Parameter(lpt, "list")); //$NON-NLS-1$

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            importedTypes.add(fqjt);
        }

        ibatorContext.getCommentGenerator().addGeneralMethodComment(method,
                table);

        return method;
    }
}
