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

public class ControllerGenerator extends AbstractJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		TopLevelClass topLevelClass = new TopLevelClass(this.introspectedTable.getControllerType());
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.addAnnotation("@Controller");
		topLevelClass.addAnnotation("@RequestMapping(\"/" + paramBaseRecord() + "\")");
		topLevelClass
				.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Controller"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		topLevelClass
				.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping"));
		topLevelClass.addImportedType(this.introspectedTable.getBaseRecordType());
		topLevelClass.addImportedType(this.introspectedTable.getServiceType());

		// 添加Service属性
		Field serviceField = addDaoField(topLevelClass);
		// 添加各种方法
		addAddMethod(topLevelClass, serviceField);
		addModifyMethod(topLevelClass, serviceField);
		addQueryByIdMethod(topLevelClass, serviceField);
		addQueryMethod(topLevelClass, serviceField);
		addDeleteMethod(topLevelClass, serviceField);

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (ibatorContext.getPlugins().controllerClassGenerated(topLevelClass, introspectedTable)) {
			answer.add(topLevelClass);
		}
		return answer;
	}

	private void addDeleteMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		method.addAnnotation("@RequestMapping(\"/delete\")");
		method.setName("delete");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"), "map"));
		method.addParameter(new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord()));
		method.addBodyLine("this." + daoField.getName() + ".delete(" + paramBaseRecord() + ");");
		method.addBodyLine("return map;");
		topLevelClass.addMethod(method);
	}

	private void addQueryByIdMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		method.addAnnotation("@RequestMapping(\"/queryById\")");
		method.setName("queryById");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"), "map"));
		method.addParameter(new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord()));
		method.addBodyLine("return map.addAttribute(\"obj\", this." + daoField.getName() + ".queryById("
				+ paramBaseRecord() + "));");
		topLevelClass.addMethod(method);
	}

	private void addQueryMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		method.setName("query");
		method.addAnnotation("@RequestMapping({ \"/query\", \"/queryPage\" })");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"), "map"));
		method.addParameter(new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord()));
		method.addBodyLine(
				"return map.addAttribute(\"list\", this." + daoField.getName() + ".query(" + paramBaseRecord() + "));");
		topLevelClass.addMethod(method);
	}

	private void addModifyMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		method.setName("modify");
		method.addAnnotation("@RequestMapping(\"/modify\")");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"), "map"));
		method.addParameter(new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord()));
		method.addBodyLine("this." + daoField.getName() + ".modify(" + paramBaseRecord() + ");");
		method.addBodyLine("return map;");
		topLevelClass.addMethod(method);
	}

	private void addAddMethod(TopLevelClass topLevelClass, Field daoField) {
		Method method = new Method();
		method.setConstructor(false);
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"));
		method.setName("add");
		method.addAnnotation("@RequestMapping(\"/add\")");
		method.addParameter(new Parameter(new FullyQualifiedJavaType("org.springframework.ui.ModelMap"), "map"));
		method.addParameter(new Parameter(this.introspectedTable.getBaseRecordType(), paramBaseRecord()));
		method.addBodyLine("this." + daoField.getName() + ".add(" + paramBaseRecord() + ");");
		method.addBodyLine("return map;");
		topLevelClass.addMethod(method);
	}

	private Field addDaoField(TopLevelClass topLevelClass) {
		Field field = new Field();
		field.setType(this.introspectedTable.getServiceType());
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setName(format(this.introspectedTable.getServiceType().getShortName()));
		field.addAnnotation("@Autowired");
		topLevelClass.addField(field);
		return field;
	}

}
