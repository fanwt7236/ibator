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

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.ibator.api.IbatorPluginAdapter;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;

/**
 * 
 * @author fanwt7236@163.com
 *
 */
public class ConstantPlugin extends IbatorPluginAdapter {

	public ConstantPlugin() {
		super();
	}

	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@SuppressWarnings("unchecked")
	protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable table) {

		List<IntrospectedColumn> columns = table.getAllColumns();

		for (IntrospectedColumn column : columns) {
			Properties props = column.getCommentEnums();
			Enumeration<String> pNames = (Enumeration<String>) props.propertyNames();
			while(pNames.hasMoreElements()){
				String name = pNames.nextElement();
				Field field = new Field();
				field.setStatic(true);
				field.setFinal(true);
				field.setType(column.getFullyQualifiedJavaType()); // $NON-NLS-1$
				field.setName(column.getActualColumnName().toUpperCase() + "_" + name); //$NON-NLS-1$
				if (column.getFullyQualifiedJavaType().equals(FullyQualifiedJavaType.getStringInstance())) {
					field.setInitializationString("\"" + name + "\"");
				} else {
					field.setInitializationString(name);
				}
				field.setVisibility(JavaVisibility.PUBLIC);
				field.addJavaDocLine("/** " + column.getCommentWithoutEnums() + ":" + props.getProperty(name) + " */");
				topLevelClass.addField(field);
			}
		}

	}
	
}
