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

import java.util.List;

import org.apache.ibatis.ibator.api.IbatorPluginAdapter;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;
import org.apache.ibatis.ibator.internal.util.StringUtility;

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

	protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable table) {

		List<IntrospectedColumn> columns = table.getAllColumns();

		for (IntrospectedColumn column : columns) {
			String comment = column.getComment();
			if (!StringUtility.stringHasValue(comment)) {
				continue;
			}
			if ((comment.indexOf('(') > -1 && comment.indexOf(')') > -1)
					|| (comment.indexOf('（') > -1 && comment.indexOf('）') > -1)) {
				if ((comment.indexOf('(') == comment.lastIndexOf('(')
						&& comment.indexOf(')') == comment.lastIndexOf(')'))
						|| (comment.indexOf('（') == comment.lastIndexOf('（')
								&& comment.indexOf('）') == comment.lastIndexOf('）'))) {
					// 中英文括号成对出现时生效
					String enums = null;
					String desc = null;
					if (comment.indexOf('(') == -1) {
						enums = comment.substring(comment.indexOf('（') + 1, comment.indexOf('）'));
						desc = comment.substring(0, comment.indexOf('（'));
					} else {
						enums = comment.substring(comment.indexOf('(') + 1, comment.indexOf(')'));
						desc = comment.substring(0, comment.indexOf('('));
					}
					String split = ";";
					if (enums.indexOf('；') != -1) {
						split = "；";
					}
					String[] es = enums.split(split);
					for (String e : es) {
						String sp = ":";
						if (e.indexOf("：") != -1) {
							sp = "：";
						}
						String[] eds = e.split(sp);
						Field field = new Field();
						field.setStatic(true);
						field.setFinal(true);
						field.setType(column.getFullyQualifiedJavaType()); // $NON-NLS-1$
						field.setName(column.getActualColumnName().toUpperCase() + "_" + eds[0]); //$NON-NLS-1$
						if (column.getFullyQualifiedJavaType().equals(FullyQualifiedJavaType.getStringInstance())) {
							field.setInitializationString("\"" + eds[0] + "\"");
						} else {
							field.setInitializationString(eds[0]);
						}
						field.setVisibility(JavaVisibility.PUBLIC);
						field.addJavaDocLine("/** " + desc + ":" + eds[1] + " */");
						topLevelClass.addField(field);
					}
				}
			}
		}

	}
}
