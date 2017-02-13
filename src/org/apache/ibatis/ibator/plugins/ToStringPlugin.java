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

import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.ibator.api.IbatorPluginAdapter;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;

/**
 * 
 * @author fanwt7236@163.com
 */
public class ToStringPlugin extends IbatorPluginAdapter {

	public ToStringPlugin() {

	}

	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		List<IntrospectedColumn> columns;
		if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
			columns = introspectedTable.getNonBLOBColumns();
		} else {
			columns = introspectedTable.getAllColumns();
		}
		generateToString(topLevelClass, columns, introspectedTable);
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		generateToString(topLevelClass, introspectedTable.getPrimaryKeyColumns(), introspectedTable);
		return true;
	}

	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		generateToString(topLevelClass, introspectedTable.getAllColumns(), introspectedTable);
		return true;
	}

	protected void generateToString(TopLevelClass topLevelClass, List<IntrospectedColumn> introspectedColumns,
			IntrospectedTable introspectedTable) {
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.setName("toString"); //$NON-NLS-1$
		if (introspectedTable.isJava5Targeted()) {
			method.addAnnotation("@Override"); //$NON-NLS-1$
		}
		ibatorContext.getCommentGenerator().addGeneralMethodComment(method, introspectedTable.getFullyQualifiedTable());

		Iterator<IntrospectedColumn> it = introspectedColumns.iterator();
		StringBuilder sb = new StringBuilder("return \""+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+" [");
		boolean isFirst = true;
		while(it.hasNext()){
			IntrospectedColumn ic = it.next();
			String p = ic.getJavaProperty();
			if(!isFirst){
				sb.append(", ");
			}else{
				isFirst = false;
			}
			sb.append(p).append("=\" + ").append(p).append(" + \"");
		}
		sb.append("]\";");
		method.addBodyLine(sb.toString());
		topLevelClass.addMethod(method);
	}

}
