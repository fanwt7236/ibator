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
package org.apache.ibatis.ibator.generator.ibatis2.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.ibator.api.CommentGenerator;
import org.apache.ibatis.ibator.api.FullyQualifiedTable;
import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.dom.OutputUtilities;
import org.apache.ibatis.ibator.api.dom.java.CompilationUnit;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.InnerClass;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.api.dom.java.JavaWildcardType;
import org.apache.ibatis.ibator.api.dom.java.Method;
import org.apache.ibatis.ibator.api.dom.java.Parameter;
import org.apache.ibatis.ibator.api.dom.java.TopLevelClass;
import org.apache.ibatis.ibator.internal.rules.IbatorRules;
import org.apache.ibatis.ibator.internal.util.JavaBeansUtil;
import org.apache.ibatis.ibator.internal.util.StringUtility;
import org.apache.ibatis.ibator.internal.util.messages.Messages;

/**
 * 
 * @author Jeff Butler
 *
 */
public class ExampleGenerator extends BaseModelClassGenerator {

    private boolean generateForJava5;

    public ExampleGenerator(boolean generateForJava5) {
        super();
        this.generateForJava5 = generateForJava5;
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(
                Messages.getString("Progress.6", table.toString())); //$NON-NLS-1$
        CommentGenerator commentGenerator = ibatorContext.getCommentGenerator();

        FullyQualifiedJavaType type = introspectedTable.getExampleType();
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        // add default constructor
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(type.getShortName());
        if (generateForJava5) {
            method.addBodyLine("oredCriteria = new ArrayList<Criteria>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("oredCriteria = new ArrayList();"); //$NON-NLS-1$
            if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
                method.addSuppressTypeWarningsAnnotation();
            }
        }
        
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        // add shallow copy constructor if the update by
        // example methods are enabled - because the parameter
        // class for update by example methods will subclass this class
        IbatorRules rules = introspectedTable.getRules();
        if (rules.generateUpdateByExampleSelective()
                || rules.generateUpdateByExampleWithBLOBs()
                || rules.generateUpdateByExampleWithoutBLOBs()) {
            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setConstructor(true);
            method.setName(type.getShortName());
            method.addParameter(new Parameter(type, "example")); //$NON-NLS-1$
            method.addBodyLine("this.orderByClause = example.orderByClause;"); //$NON-NLS-1$
            method.addBodyLine("this.oredCriteria = example.oredCriteria;"); //$NON-NLS-1$
            commentGenerator.addGeneralMethodComment(method, table);
            topLevelClass.addMethod(method);
        }

        // add field, getter, setter for orderby clause
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getStringInstance());
        field.setName("orderByClause"); //$NON-NLS-1$
        commentGenerator.addFieldComment(field, table);
        topLevelClass.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setOrderByClause"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "orderByClause")); //$NON-NLS-1$
        method.addBodyLine("this.orderByClause = orderByClause;"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("getOrderByClause"); //$NON-NLS-1$
        method.addBodyLine("return orderByClause;"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        // add field and methods for the list of ored criteria
        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);

        FullyQualifiedJavaType fqjt = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            fqjt.addTypeArgument(new FullyQualifiedJavaType("Criteria")); //$NON-NLS-1$
        }

        field.setType(fqjt);
        if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
            field.addSuppressTypeWarningsAnnotation();
        }
        field.setName("oredCriteria"); //$NON-NLS-1$
        commentGenerator.addFieldComment(field, table);
        topLevelClass.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
            method.addSuppressTypeWarningsAnnotation();
        }
        method.setName("getOredCriteria"); //$NON-NLS-1$
        method.addBodyLine("return oredCriteria;"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
            method.addSuppressTypeWarningsAnnotation();
        }
        method.setName("or"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getCriteriaInstance(), "criteria")); //$NON-NLS-1$
        method.addBodyLine("oredCriteria.add(criteria);"); //$NON-NLS-1$

        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
            method.addSuppressTypeWarningsAnnotation();
        }
        method.setName("createCriteria"); //$NON-NLS-1$
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        method.addBodyLine("Criteria criteria = createCriteriaInternal();"); //$NON-NLS-1$
        method.addBodyLine("if (oredCriteria.size() == 0) {"); //$NON-NLS-1$
        method.addBodyLine("oredCriteria.add(criteria);"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        method.addBodyLine("return criteria;"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("createCriteriaInternal"); //$NON-NLS-1$
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        method.addBodyLine("Criteria criteria = new Criteria();"); //$NON-NLS-1$
        method.addBodyLine("return criteria;"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("clear"); //$NON-NLS-1$
        method.addBodyLine("oredCriteria.clear();"); //$NON-NLS-1$
        commentGenerator.addGeneralMethodComment(method, table);
        topLevelClass.addMethod(method);

        // now generate the inner class that holds the AND conditions
        topLevelClass.addInnerClass(getCriteriaInnerClass(topLevelClass,
                introspectedTable));

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (ibatorContext.getPlugins().modelExampleClassGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private InnerClass getCriteriaInnerClass(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        Field field;
        Method method;

        InnerClass answer = new InnerClass(FullyQualifiedJavaType
                .getCriteriaInstance());

        if (ibatorContext.getSuppressTypeWarnings(introspectedTable)) {
            answer.addSuppressTypeWarningsAnnotation();
        }
        answer.setVisibility(JavaVisibility.PUBLIC);
        answer.setStatic(true);
        ibatorContext.getCommentGenerator().addClassComment(answer,
                introspectedTable.getFullyQualifiedTable());

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("Criteria"); //$NON-NLS-1$
        method.setConstructor(true);
        method.addBodyLine("super();"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("criteriaWithoutValue = new ArrayList<String>();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithSingleValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithListValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithBetweenValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$
            
        } else {
            method.addBodyLine("criteriaWithoutValue = new ArrayList();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithSingleValue = new ArrayList();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithListValue = new ArrayList();"); //$NON-NLS-1$
            method.addBodyLine("criteriaWithBetweenValue = new ArrayList();"); //$NON-NLS-1$
        }
        answer.addMethod(method);

        List<String> criteriaLists = new ArrayList<String>();
        criteriaLists.add("criteriaWithoutValue"); //$NON-NLS-1$
        criteriaLists.add("criteriaWithSingleValue"); //$NON-NLS-1$
        criteriaLists.add("criteriaWithListValue"); //$NON-NLS-1$
        criteriaLists.add("criteriaWithBetweenValue"); //$NON-NLS-1$

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
            if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
                criteriaLists.addAll(addtypeHandledObjectsAndMethods(introspectedColumn,
                        method, answer));
            }
        }

        // now generate the isValid method
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("isValid"); //$NON-NLS-1$
        method.setReturnType(FullyQualifiedJavaType
                .getBooleanPrimitiveInstance());
        StringBuilder sb = new StringBuilder();
        Iterator<String> strIter = criteriaLists.iterator();
        sb.append("return "); //$NON-NLS-1$
        sb.append(strIter.next());
        sb.append(".size() > 0"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        while (strIter.hasNext()) {
            sb.setLength(0);
            OutputUtilities.javaIndent(sb, 1);
            sb.append("|| "); //$NON-NLS-1$
            sb.append(strIter.next());
            sb.append(".size() > 0"); //$NON-NLS-1$
            if (!strIter.hasNext()) {
                sb.append(';');
            }
            method.addBodyLine(sb.toString());
        }
        answer.addMethod(method);

        // now we need to generate the methods that will be used in the SqlMap
        // to generate the dynamic where clause
        topLevelClass.addImportedType(FullyQualifiedJavaType
                .getNewMapInstance());
        topLevelClass.addImportedType(FullyQualifiedJavaType
                .getNewListInstance());
        topLevelClass.addImportedType(FullyQualifiedJavaType
                .getNewHashMapInstance());
        topLevelClass.addImportedType(FullyQualifiedJavaType
                .getNewArrayListInstance());

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        FullyQualifiedJavaType listOfStrings = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            listOfStrings.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
        }
        field.setType(listOfStrings);
        field.setName("criteriaWithoutValue"); //$NON-NLS-1$
        answer.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        method.addBodyLine("return criteriaWithoutValue;"); //$NON-NLS-1$
        answer.addMethod(method);

        FullyQualifiedJavaType listOfMaps = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            FullyQualifiedJavaType mapStringObject = FullyQualifiedJavaType.getNewMapInstance();
            mapStringObject.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
            mapStringObject.addTypeArgument(FullyQualifiedJavaType.getObjectInstance());
            listOfMaps.addTypeArgument(mapStringObject);
        }

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName("criteriaWithSingleValue"); //$NON-NLS-1$
        answer.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        method.addBodyLine("return criteriaWithSingleValue;"); //$NON-NLS-1$
        answer.addMethod(method);

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName("criteriaWithListValue"); //$NON-NLS-1$
        answer.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        method.addBodyLine("return criteriaWithListValue;"); //$NON-NLS-1$
        answer.addMethod(method);

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName("criteriaWithBetweenValue"); //$NON-NLS-1$
        answer.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        method.addBodyLine("return criteriaWithBetweenValue;"); //$NON-NLS-1$
        answer.addMethod(method);

        // now add the methods for simplifying the individual field set methods
        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("addCriterion"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addBodyLine("if (condition == null) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Value for condition cannot be null\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        method.addBodyLine("criteriaWithoutValue.add(condition);"); //$NON-NLS-1$
        answer.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("addCriterion"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getObjectInstance(), "value")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"value\", value);"); //$NON-NLS-1$
        method.addBodyLine("criteriaWithSingleValue.add(map);"); //$NON-NLS-1$
        answer.addMethod(method);

        FullyQualifiedJavaType listOfObjects = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            JavaWildcardType extendsObject = new JavaWildcardType("java.lang.Object", true); //$NON-NLS-1$
            listOfObjects.addTypeArgument(extendsObject);
        }

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("addCriterion"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(listOfObjects, "values")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"values\", values);"); //$NON-NLS-1$
        method.addBodyLine("criteriaWithListValue.add(map);"); //$NON-NLS-1$
        answer.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName("addCriterion"); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getObjectInstance(), "value1")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getObjectInstance(), "value2")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("List<Object> list = new ArrayList<Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("List list = new ArrayList();"); //$NON-NLS-1$
        }
        
        method.addBodyLine("list.add(value1);"); //$NON-NLS-1$
        method.addBodyLine("list.add(value2);"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"values\", list);"); //$NON-NLS-1$
        method.addBodyLine("criteriaWithBetweenValue.add(map);"); //$NON-NLS-1$
        answer.addMethod(method);

        FullyQualifiedJavaType listOfDates = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            listOfDates.addTypeArgument(FullyQualifiedJavaType.getDateInstance());
        }

        if (introspectedTable.hasJDBCDateColumns()) {
            topLevelClass.addImportedType(FullyQualifiedJavaType
                    .getDateInstance());
            topLevelClass.addImportedType(FullyQualifiedJavaType
                    .getNewIteratorInstance());
            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method
                    .addBodyLine("addCriterion(condition, new java.sql.Date(value.getTime()), property);"); //$NON-NLS-1$
            answer.addMethod(method);

            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(listOfDates, "values")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
            method
                    .addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
            method.addBodyLine("}"); //$NON-NLS-1$
            if (generateForJava5) {
                method.addBodyLine("List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();"); //$NON-NLS-1$
                method.addBodyLine("Iterator<Date> iter = values.iterator();"); //$NON-NLS-1$
                method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
                method.addBodyLine("dateList.add(new java.sql.Date(iter.next().getTime()));"); //$NON-NLS-1$
                method.addBodyLine("}"); //$NON-NLS-1$
            } else {
                method.addBodyLine("List dateList = new ArrayList();"); //$NON-NLS-1$
                method.addBodyLine("Iterator iter = values.iterator();"); //$NON-NLS-1$
                method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
                method.addBodyLine("dateList.add(new java.sql.Date(((Date)iter.next()).getTime()));"); //$NON-NLS-1$
                method.addBodyLine("}"); //$NON-NLS-1$
            }
            method.addBodyLine("addCriterion(condition, dateList, property);"); //$NON-NLS-1$
            answer.addMethod(method);

            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCDate"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value1")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value2")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
            method
                    .addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
            method.addBodyLine("}"); //$NON-NLS-1$
            method
                    .addBodyLine("addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);"); //$NON-NLS-1$
            answer.addMethod(method);
        }

        if (introspectedTable.hasJDBCTimeColumns()) {
            topLevelClass.addImportedType(FullyQualifiedJavaType
                    .getDateInstance());
            topLevelClass.addImportedType(FullyQualifiedJavaType
                    .getNewIteratorInstance());
            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method
                    .addBodyLine("addCriterion(condition, new java.sql.Time(value.getTime()), property);"); //$NON-NLS-1$
            answer.addMethod(method);

            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(listOfDates, "values")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
            method
                    .addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
            method.addBodyLine("}"); //$NON-NLS-1$
            if (generateForJava5) {
                method.addBodyLine("List<java.sql.Time> timeList = new ArrayList<java.sql.Time>();"); //$NON-NLS-1$
                method.addBodyLine("Iterator<Date> iter = values.iterator();"); //$NON-NLS-1$
                method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
                method
                    .addBodyLine("timeList.add(new java.sql.Time(iter.next().getTime()));"); //$NON-NLS-1$
                method.addBodyLine("}"); //$NON-NLS-1$
            } else {
                method.addBodyLine("List timeList = new ArrayList();"); //$NON-NLS-1$
                method.addBodyLine("Iterator iter = values.iterator();"); //$NON-NLS-1$
                method.addBodyLine("while (iter.hasNext()) {"); //$NON-NLS-1$
                method
                    .addBodyLine("timeList.add(new java.sql.Time(((Date)iter.next()).getTime()));"); //$NON-NLS-1$
                method.addBodyLine("}"); //$NON-NLS-1$
            }
            method.addBodyLine("addCriterion(condition, timeList, property);"); //$NON-NLS-1$
            answer.addMethod(method);

            method = new Method();
            method.setVisibility(JavaVisibility.PROTECTED);
            method.setName("addCriterionForJDBCTime"); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "condition")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value1")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getDateInstance(), "value2")); //$NON-NLS-1$
            method.addParameter(new Parameter(FullyQualifiedJavaType
                    .getStringInstance(), "property")); //$NON-NLS-1$
            method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
            method
                    .addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
            method.addBodyLine("}"); //$NON-NLS-1$
            method
                    .addBodyLine("addCriterion(condition, new java.sql.Time(value1.getTime()), new java.sql.Time(value2.getTime()), property);"); //$NON-NLS-1$
            answer.addMethod(method);
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());

            // here we need to add the individual methods for setting the
            // conditions for a field
            answer.addMethod(getSetNullMethod(introspectedColumn));
            answer.addMethod(getSetNotNullMethod(introspectedColumn));
            answer.addMethod(getSetEqualMethod(introspectedColumn));
            answer.addMethod(getSetNotEqualMethod(introspectedColumn));
            answer.addMethod(getSetGreaterThanMethod(introspectedColumn));
            answer.addMethod(getSetGreaterThenOrEqualMethod(introspectedColumn));
            answer.addMethod(getSetLessThanMethod(introspectedColumn));
            answer.addMethod(getSetLessThanOrEqualMethod(introspectedColumn));

            if (introspectedColumn.isJdbcCharacterColumn()) {
                answer.addMethod(getSetLikeMethod(introspectedColumn));
                answer.addMethod(getSetNotLikeMethod(introspectedColumn));
            }

            answer.addMethod(getSetInOrNotInMethod(introspectedColumn, true));
            answer.addMethod(getSetInOrNotInMethod(introspectedColumn, false));
            answer.addMethod(getSetBetweenOrNotBetweenMethod(introspectedColumn, true));
            answer.addMethod(getSetBetweenOrNotBetweenMethod(introspectedColumn, false));
        }

        return answer;
    }

    /**
     * This method adds all the extra methods and fields required to support a
     * user defined type handler on some column.
     * 
     * @param introspectedColumn
     * @param constructor
     * @param innerClass
     * @return a list of the names of all Lists added to the class by this
     *         method
     */
    private List<String> addtypeHandledObjectsAndMethods(IntrospectedColumn introspectedColumn,
            Method constructor, InnerClass innerClass) {
        List<String> answer = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();

        // add new private fields and public accessors in the class
        FullyQualifiedJavaType listOfMaps = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            FullyQualifiedJavaType mapStringObject = FullyQualifiedJavaType.getNewMapInstance();
            mapStringObject.addTypeArgument(FullyQualifiedJavaType.getStringInstance());
            mapStringObject.addTypeArgument(FullyQualifiedJavaType.getObjectInstance());
            listOfMaps.addTypeArgument(mapStringObject);
        }

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithSingleValue"); //$NON-NLS-1$
        answer.add(sb.toString());

        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName(sb.toString());
        innerClass.addField(field);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        sb.insert(0, "return "); //$NON-NLS-1$
        sb.append(';');
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithListValue"); //$NON-NLS-1$
        answer.add(sb.toString());

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName(sb.toString());
        innerClass.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        sb.insert(0, "return "); //$NON-NLS-1$
        sb.append(';');
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithBetweenValue"); //$NON-NLS-1$
        answer.add(sb.toString());

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(listOfMaps);
        field.setName(sb.toString());
        innerClass.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(field.getType());
        method.setName(JavaBeansUtil.getGetterMethodName(field.getName(), field
                .getType()));
        sb.insert(0, "return "); //$NON-NLS-1$
        sb.append(';');
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        // add constructor initialization
        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        if (generateForJava5) {
            sb.append("CriteriaWithSingleValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$;
        } else {
            sb.append("CriteriaWithSingleValue = new ArrayList();"); //$NON-NLS-1$;
        }
        constructor.addBodyLine(sb.toString());

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        if (generateForJava5) {
            sb.append("CriteriaWithListValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$
        } else {
            sb.append("CriteriaWithListValue = new ArrayList();"); //$NON-NLS-1$
        }
        constructor.addBodyLine(sb.toString());

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        if (generateForJava5) {
            sb.append("CriteriaWithBetweenValue = new ArrayList<Map<String, Object>>();"); //$NON-NLS-1$
        } else {
            sb.append("CriteriaWithBetweenValue = new ArrayList();"); //$NON-NLS-1$
        }
        constructor.addBodyLine(sb.toString());

        // now add the methods for simplifying the individual field set methods
        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        sb.setLength(0);
        sb.append("add"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        sb.append("Criterion"); //$NON-NLS-1$

        method.setName(sb.toString());
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (value == null) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Value for \" + property + \" cannot be null\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"value\", value);"); //$NON-NLS-1$

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithSingleValue.add(map);"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        FullyQualifiedJavaType listOfObjects = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            listOfObjects.addTypeArgument(introspectedColumn.getFullyQualifiedJavaType());
        }

        sb.setLength(0);
        sb.append("add"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        sb.append("Criterion"); //$NON-NLS-1$

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName(sb.toString());
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(listOfObjects, "values")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (values == null || values.size() == 0) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Value list for \" + property + \" cannot be null or empty\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"values\", values);"); //$NON-NLS-1$

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithListValue.add(map);"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        sb.setLength(0);
        sb.append("add"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
        sb.append("Criterion"); //$NON-NLS-1$

        method = new Method();
        method.setVisibility(JavaVisibility.PROTECTED);
        method.setName(sb.toString());
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "condition")); //$NON-NLS-1$
        method.addParameter(new Parameter(introspectedColumn
                .getFullyQualifiedJavaType(), "value1")); //$NON-NLS-1$
        method.addParameter(new Parameter(introspectedColumn
                .getFullyQualifiedJavaType(), "value2")); //$NON-NLS-1$
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getStringInstance(), "property")); //$NON-NLS-1$
        method.addBodyLine("if (value1 == null || value2 == null) {"); //$NON-NLS-1$
        method
                .addBodyLine("throw new RuntimeException(\"Between values for \" + property + \" cannot be null\");"); //$NON-NLS-1$
        method.addBodyLine("}"); //$NON-NLS-1$
        if (generateForJava5) {
            sb.setLength(0);
            sb.append("List<"); //$NON-NLS-1$
            sb.append(introspectedColumn.getFullyQualifiedJavaType().getShortName());
            sb.append("> list = new ArrayList<"); //$NON-NLS-1$
            sb.append(introspectedColumn.getFullyQualifiedJavaType().getShortName());
            sb.append(">();"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        } else {
            method.addBodyLine("List list = new ArrayList();"); //$NON-NLS-1$
        }
        method.addBodyLine("list.add(value1);"); //$NON-NLS-1$
        method.addBodyLine("list.add(value2);"); //$NON-NLS-1$
        if (generateForJava5) {
            method.addBodyLine("Map<String, Object> map = new HashMap<String, Object>();"); //$NON-NLS-1$
        } else {
            method.addBodyLine("Map map = new HashMap();"); //$NON-NLS-1$
        }
        method.addBodyLine("map.put(\"condition\", condition);"); //$NON-NLS-1$
        method.addBodyLine("map.put(\"values\", list);"); //$NON-NLS-1$

        sb.setLength(0);
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("CriteriaWithBetweenValue.add(map);"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        innerClass.addMethod(method);

        return answer;
    }

    private Method getSetNullMethod(IntrospectedColumn introspectedColumn) {
        return getNoValueMethod(introspectedColumn, "IsNull", "is null"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetNotNullMethod(IntrospectedColumn introspectedColumn) {
        return getNoValueMethod(introspectedColumn, "IsNotNull", "is not null"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetEqualMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "EqualTo", "="); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetNotEqualMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "NotEqualTo", "<>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetGreaterThanMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "GreaterThan", ">"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetGreaterThenOrEqualMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "GreaterThanOrEqualTo", ">="); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetLessThanMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "LessThan", "<"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetLessThanOrEqualMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "LessThanOrEqualTo", "<="); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetLikeMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "Like", "like"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSetNotLikeMethod(IntrospectedColumn introspectedColumn) {
        return getSingleValueMethod(introspectedColumn, "NotLike", "not like"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private Method getSingleValueMethod(IntrospectedColumn introspectedColumn,
            String nameFragment, String operator) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(introspectedColumn
                .getFullyQualifiedJavaType(), "value")); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and"); //$NON-NLS-1$
        sb.append(nameFragment);
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);

        if (introspectedColumn.isJDBCDateColumn()) {
            sb.append("addCriterionForJDBCDate(\""); //$NON-NLS-1$
        } else if (introspectedColumn.isJDBCTimeColumn()) {
            sb.append("addCriterionForJDBCTime(\""); //$NON-NLS-1$
        } else if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append("add"); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
            sb.append("Criterion(\""); //$NON-NLS-1$
        } else {
            sb.append("addCriterion(\""); //$NON-NLS-1$
        }

        sb.append(introspectedColumn.getAliasedActualColumnName());
        sb.append(' ');
        sb.append(operator);
        sb.append("\", "); //$NON-NLS-1$

        if (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            sb.append("new "); //$NON-NLS-1$
            sb.append(introspectedColumn.getFullyQualifiedJavaType()
                    .getPrimitiveTypeWrapper().getShortName());
            sb.append("(value)"); //$NON-NLS-1$
        } else {
            sb.append("value"); //$NON-NLS-1$
        }

        sb.append(", \""); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        method.addBodyLine("return this;"); //$NON-NLS-1$

        return method;
    }

    /**
     * Generates methods that set between and not between conditions
     * 
     * @param introspectedColumn
     * @param betweenMethod
     * @return a generated method for the between or not between method
     */
    private Method getSetBetweenOrNotBetweenMethod(IntrospectedColumn introspectedColumn,
            boolean betweenMethod) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type = introspectedColumn
                .getFullyQualifiedJavaType();

        method.addParameter(new Parameter(type, "value1")); //$NON-NLS-1$
        method.addParameter(new Parameter(type, "value2")); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and"); //$NON-NLS-1$
        if (betweenMethod) {
            sb.append("Between"); //$NON-NLS-1$
        } else {
            sb.append("NotBetween"); //$NON-NLS-1$
        }
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);

        if (introspectedColumn.isJDBCDateColumn()) {
            sb.append("addCriterionForJDBCDate(\""); //$NON-NLS-1$
        } else if (introspectedColumn.isJDBCTimeColumn()) {
            sb.append("addCriterionForJDBCTime(\""); //$NON-NLS-1$
        } else if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append("add"); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
            sb.append("Criterion(\""); //$NON-NLS-1$
        } else {
            sb.append("addCriterion(\""); //$NON-NLS-1$
        }

        sb.append(introspectedColumn.getAliasedActualColumnName());
        if (betweenMethod) {
            sb.append(" between"); //$NON-NLS-1$
        } else {
            sb.append(" not between"); //$NON-NLS-1$
        }
        sb.append("\", "); //$NON-NLS-1$
        if (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
            sb.append("new "); //$NON-NLS-1$
            sb.append(introspectedColumn.getFullyQualifiedJavaType()
                    .getPrimitiveTypeWrapper().getShortName());
            sb.append("(value1), "); //$NON-NLS-1$
            sb.append("new "); //$NON-NLS-1$
            sb.append(introspectedColumn.getFullyQualifiedJavaType()
                    .getPrimitiveTypeWrapper().getShortName());
            sb.append("(value2)"); //$NON-NLS-1$
        } else {
            sb.append("value1, value2"); //$NON-NLS-1$
        }

        sb.append(", \""); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        method.addBodyLine("return this;"); //$NON-NLS-1$

        return method;
    }

    /**
     * 
     * @param introspectedColumn
     * @param inMethod
     *            if true generates an "in" method, else generates a "not in"
     *            method
     * @return a generated method for the in or not in method
     */
    private Method getSetInOrNotInMethod(IntrospectedColumn introspectedColumn, boolean inMethod) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type = FullyQualifiedJavaType
                .getNewListInstance();
        if (generateForJava5) {
            if (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                type.addTypeArgument(introspectedColumn
                        .getFullyQualifiedJavaType().getPrimitiveTypeWrapper());
            } else {
                type.addTypeArgument(introspectedColumn
                        .getFullyQualifiedJavaType());
            }
        }
        
        method.addParameter(new Parameter(type, "values")); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and"); //$NON-NLS-1$
        if (inMethod) {
            sb.append("In"); //$NON-NLS-1$
        } else {
            sb.append("NotIn"); //$NON-NLS-1$
        }
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);

        if (introspectedColumn.isJDBCDateColumn()) {
            sb.append("addCriterionForJDBCDate(\""); //$NON-NLS-1$
        } else if (introspectedColumn.isJDBCTimeColumn()) {
            sb.append("addCriterionForJDBCTime(\""); //$NON-NLS-1$
        } else if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append("add"); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
            sb.append("Criterion(\""); //$NON-NLS-1$
        } else {
            sb.append("addCriterion(\""); //$NON-NLS-1$
        }

        sb.append(introspectedColumn.getAliasedActualColumnName());
        if (inMethod) {
            sb.append(" in"); //$NON-NLS-1$
        } else {
            sb.append(" not in"); //$NON-NLS-1$
        }
        sb.append("\", values, \""); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        method.addBodyLine("return this;"); //$NON-NLS-1$

        return method;
    }

    private Method getNoValueMethod(IntrospectedColumn introspectedColumn, String nameFragment,
            String operator) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and"); //$NON-NLS-1$
        sb.append(nameFragment);
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
        sb.setLength(0);
        sb.append("addCriterion(\""); //$NON-NLS-1$
        sb.append(introspectedColumn.getAliasedActualColumnName());
        sb.append(' ');
        sb.append(operator);
        sb.append("\");"); //$NON-NLS-1$
        method.addBodyLine(sb.toString());
        method.addBodyLine("return this;"); //$NON-NLS-1$

        return method;
    }
}
