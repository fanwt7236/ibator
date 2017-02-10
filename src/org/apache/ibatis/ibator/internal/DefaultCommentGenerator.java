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

package org.apache.ibatis.ibator.internal;

import java.util.Properties;

import org.apache.ibatis.ibator.api.CommentGenerator;
import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.java.CompilationUnit;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.InnerClass;
import org.apache.ibatis.ibator.api.dom.java.InnerEnum;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;
import org.apache.ibatis.ibator.config.PropertyRegistry;
import org.apache.ibatis.ibator.internal.util.StringUtility;

/**
 * 定制化的注释生成器
 * 参照DefaultCommentGenerator，这里也创建一个properties用来存放配置文件中配置的propertity，有了这个propertity我们就可以通过在配置文件中配置属性来控制注释的生成规则。
 * 在生成的文件中，很多方法的意图可以通过方法明很直观的看出来，比如说:insert,update等。经常让我纠结、记不清，或者是需要查数据库设计才能知道的往往是字段描述，
 * 所以这里我的默认生成规则就出来了:默认给实体属性添加描述。
 * 当然为了给记忆里超群的小伙伴一个机会，同时测试下properties这个属性，会内置key:ignoreField来表示是否忽略注释,当然默认配置还是生成。
 * //TODO 删除了大部分注释，添加了字段描述注释
 * @author Jeff Butler
 * @author fanwt7236@163.com
 */
public class DefaultCommentGenerator implements CommentGenerator {
    
    private Properties properties;

    public DefaultCommentGenerator() {
        properties = new Properties();
    }

    public void addFieldComment(Field field, FullyQualifiedTable table) {
    }

    public void addClassComment(InnerClass innerClass, FullyQualifiedTable table) {
    }

    public void addEnumComment(InnerEnum innerEnum, FullyQualifiedTable table) {
    }

    public void addGetterComment(Method method, FullyQualifiedTable table, String columnName) {
    }

    public void addSetterComment(Method method, FullyQualifiedTable table, String columnName) {
    }

    public void addGeneralMethodComment(Method method, FullyQualifiedTable table) {
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
    }

    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    @Override
    public void addFieldComment(Field field, FullyQualifiedTable table, IntrospectedColumn introspectedColumn) {
    	String fc = this.properties.getProperty(PropertyRegistry.IGNORE_FIELD);
		//尽量使用ibator自有的一些工具
		if(StringUtility.isTrue(fc)){
			return ;
		}
    	field.addJavaDocLine("/** "+table+"."+introspectedColumn.getActualColumnName()+"->"+introspectedColumn.getComment()+" */");
    }
}
