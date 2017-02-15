package org.apache.ibatis.ibator.generator.ibatis2.ext;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.ibator.api.dom.java.CompilationUnit;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.Parameter;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;
import org.apache.ibatis.ibator.generator.AbstractJavaGenerator;

public class ServiceGenerator extends AbstractJavaGenerator{

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		TopLevelClass topLevelClass = new TopLevelClass(this.introspectedTable.getServiceType());
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.addAnnotation("@Service");
		topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.List"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
		topLevelClass.addImportedType(this.introspectedTable.getBaseRecordType());
		topLevelClass.addImportedType(this.introspectedTable.getDAOInterfaceType());
		
		//添加DAO属性
		Field daoField = addDaoField(topLevelClass);
		//添加各种方法
		addAddMethod(topLevelClass, daoField);
		addModifyMethod(topLevelClass, daoField);
		addQueryByIdMethod(topLevelClass, daoField);
		addQueryMethod(topLevelClass, daoField);
		addDeleteMethod(topLevelClass, daoField);
		
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (ibatorContext.getPlugins().serviceClassGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
		return answer;
	}

	private void addDeleteMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(null);
		method.setName("delete");
		Parameter parameter = new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord());
		method.addParameter(parameter);
		method.addBodyLine("this." + daoField.getName() + ".delete("+paramBaseRecord()+");");
		topLevelClass.addMethod(method);
	}

	private void addQueryByIdMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(this.introspectedTable.getBaseRecordType());
		method.setName("queryById");
		Parameter parameter = new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord());
		method.addParameter(parameter);
		method.addBodyLine("return this." + daoField.getName() + ".selectOne("+paramBaseRecord()+");");
		topLevelClass.addMethod(method);
	}

	private void addQueryMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		FullyQualifiedJavaType r = new FullyQualifiedJavaType("java.util.List");
		r.setExtName(this.introspectedTable.getBaseRecordType().getShortName());
		method.setReturnType(r);
		method.setName("query");
		Parameter parameter = new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord());
		method.addParameter(parameter);
		method.addBodyLine("return this." + daoField.getName() + ".selectList("+paramBaseRecord()+");");
		topLevelClass.addMethod(method);
	}

	private void addModifyMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(null);
		method.setName("modify");
		Parameter parameter = new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord());
		method.addParameter(parameter);
		method.addBodyLine("this." + daoField.getName() + ".update("+paramBaseRecord()+");");
		topLevelClass.addMethod(method);
	}

	private void addAddMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(null);
		method.setName("add");
		Parameter parameter = new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord());
		method.addParameter(parameter);
		method.addBodyLine("this." + daoField.getName() + ".insert("+paramBaseRecord()+");");
		topLevelClass.addMethod(method);
	}

	private Field addDaoField(TopLevelClass topLevelClass) {
		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(this.introspectedTable.getDAOInterfaceType());
		field.setName(format(this.introspectedTable.getDAOInterfaceType().getShortName()));
		field.addAnnotation("@Autowired");
		topLevelClass.addField(field);
		return field;
	}
	
}
