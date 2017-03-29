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
package org.apache.ibatis.ibator.generator.ibatis2.sqlmap;

import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.dom.xml.Attribute;
import org.apache.ibatis.ibator.api.dom.xml.Document;
import org.apache.ibatis.ibator.api.dom.xml.XmlElement;
import org.apache.ibatis.ibator.generator.AbstractXmlGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.XmlConstants;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.AbstractXmlElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.CountByExampleElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.CountElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.DeleteByExampleElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.DeleteByPrimaryKeyElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.DeleteElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.ExampleWhereClauseElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.IncludeColumnsSqlElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.IncludeCommonsWhereElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.InsertBatchElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.InsertElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.InsertSelectiveElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.ResultMapWithBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.ResultMapWithoutBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SelectByExampleWithBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SelectByExampleWithoutBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SelectByPrimaryKeyElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SelectListElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SelectOneElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.SimpleInsertElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByEntityElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByExampleSelectiveElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByExampleWithBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByExampleWithoutBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByPrimaryKeySelectiveElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByPrimaryKeyWithBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateByPrimaryKeyWithoutBLOBsElementGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.elements.UpdateElementGenerator;
import org.apache.ibatis.ibator.internal.util.messages.Messages;

/**
 * 
 * @author Jeff Butler
 *
 */
public class SqlMapGenerator extends AbstractXmlGenerator {

	public SqlMapGenerator() {
		super();
	}

	protected XmlElement getSqlMapElement() {
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
		progressCallback.startTask(Messages.getString("Progress.12", table.toString())); //$NON-NLS-1$
		XmlElement answer = new XmlElement("sqlMap"); //$NON-NLS-1$
		
		// TODO 修改namespace 为全限定名 2017-02-10
		// TODO 修改namespace 的默认生成规则，默认按照dao的全限定名来生成，同时也为了做公共dao方便，也提供了按照实体类全限定名进行扩展
		String namespace = introspectedTable.getDAOInterfaceType().getFullyQualifiedName();
		if("entity".equalsIgnoreCase(this.ibatorContext.getSqlMapGeneratorConfiguration().getNamespaceGenerateBy())){
			namespace = introspectedTable.getBaseRecordType().getFullyQualifiedName();
		}
		answer.addAttribute(new Attribute("namespace", namespace));

		ibatorContext.getCommentGenerator().addRootComment(answer);

		addResultMapWithoutBLOBsElement(answer);
		addResultMapWithBLOBsElement(answer);
	
		addExampleWhereClauseElement(answer);
		addSelectByExampleWithBLOBsElement(answer);
		addSelectByExampleWithoutBLOBsElement(answer);
		addSelectByPrimaryKeyElement(answer);
		addDeleteByPrimaryKeyElement(answer);
		addDeleteByExampleElement(answer);
		addInsertElement(answer);
		addInsertSelectiveElement(answer);
		addCountByExampleElement(answer);
		addUpdateByExampleSelectiveElement(answer);
		addUpdateByExampleWithBLOBsElement(answer);
		addUpdateByExampleWithoutBLOBsElement(answer);
		addUpdateByPrimaryKeySelectiveElement(answer);
		addUpdateByPrimaryKeyWithBLOBsElement(answer);
		addUpdateByPrimaryKeyWithoutBLOBsElement(answer);
		
		// TODO 新增方法 2017-02-13
		addColumnsSqlMethod(answer);
		addCommonsWhereMethod(answer);
		addInsertSimpleMethod(answer);// insert方法和原有的insertSelective方法一致，直接复用
		addInsertBatchMethod(answer);
		addUpdateMethod(answer);
		addUpdateByEntityMethod(answer);
		addDeleteMethod(answer);
		addCountMethod(answer);
		addSelectOneMethod(answer);
		addSelectListMethod(answer);

		return answer;
	}

	private void addCommonsWhereMethod(XmlElement answer) {
		if(introspectedTable.getRules().generateCommonsWhere()){
			IncludeCommonsWhereElementGenerator elementGenerator = new IncludeCommonsWhereElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, answer);
		}
	}

	private void addColumnsSqlMethod(XmlElement answer) {
		if(introspectedTable.getRules().generateIncludeColumns()){
			AbstractXmlElementGenerator elementGenerator = new IncludeColumnsSqlElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, answer);
		}
	}

	private void addUpdateMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdate()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addSelectListMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSelectList()) {
			AbstractXmlElementGenerator elementGenerator = new SelectListElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addSelectOneMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSelectOne()) {
			AbstractXmlElementGenerator elementGenerator = new SelectOneElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addUpdateByEntityMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByEntity()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByEntityElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addCountMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateCount()) {
			AbstractXmlElementGenerator elementGenerator = new CountElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addInsertBatchMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateInsertBatch()) {
			AbstractXmlElementGenerator elementGenerator = new InsertBatchElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addDeleteMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateDelete()) {
			AbstractXmlElementGenerator elementGenerator = new DeleteElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	private void addInsertSimpleMethod(XmlElement parentElement) {
		if (introspectedTable.getRules().generateInsertSimple()) {
			AbstractXmlElementGenerator elementGenerator = new SimpleInsertElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateBaseResultMap()) {
			AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addResultMapWithBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateResultMapWithBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new ResultMapWithBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addExampleWhereClauseElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSQLExampleWhereClause()) {
			AbstractXmlElementGenerator elementGenerator = new ExampleWhereClauseElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addSelectByExampleWithoutBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithoutBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addSelectByExampleWithBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSelectByExampleWithBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new SelectByExampleWithBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
			AbstractXmlElementGenerator elementGenerator = new SelectByPrimaryKeyElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addDeleteByExampleElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateDeleteByExample()) {
			AbstractXmlElementGenerator elementGenerator = new DeleteByExampleElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
			AbstractXmlElementGenerator elementGenerator = new DeleteByPrimaryKeyElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addInsertElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateInsert()) {
			AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addInsertSelectiveElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateInsertSelective()) {
			AbstractXmlElementGenerator elementGenerator = new InsertSelectiveElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addCountByExampleElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateCountByExample()) {
			AbstractXmlElementGenerator elementGenerator = new CountByExampleElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByExampleSelectiveElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByExampleSelective()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByExampleSelectiveElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByExampleWithBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByExampleWithBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByExampleWithoutBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByExampleWithoutBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByExampleWithoutBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByPrimaryKeySelectiveElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeySelectiveElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByPrimaryKeyWithBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void addUpdateByPrimaryKeyWithoutBLOBsElement(XmlElement parentElement) {
		if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithoutBLOBs()) {
			AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator();
			initializeAndExecuteGenerator(elementGenerator, parentElement);
		}
	}

	protected void initializeAndExecuteGenerator(AbstractXmlElementGenerator elementGenerator,
			XmlElement parentElement) {
		elementGenerator.setIbatorContext(ibatorContext);
		elementGenerator.setIntrospectedTable(introspectedTable);
		elementGenerator.setProgressCallback(progressCallback);
		elementGenerator.setWarnings(warnings);
		elementGenerator.addElements(parentElement);
	}

	@Override
	public Document getDocument() {
		Document document = new Document(XmlConstants.SQL_MAP_PUBLIC_ID, XmlConstants.SQL_MAP_SYSTEM_ID);
		document.setRootElement(getSqlMapElement());

		if (!ibatorContext.getPlugins().sqlMapDocumentGenerated(document, introspectedTable)) {
			document = null;
		}

		return document;
	}
}
