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
package org.apache.ibatis.ibator.generator.ibatis2;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.ibator.api.GeneratedHtmlFile;
import org.apache.ibatis.ibator.api.GeneratedJavaFile;
import org.apache.ibatis.ibator.api.GeneratedXmlFile;
import org.apache.ibatis.ibator.api.IntrospectedTable;
import org.apache.ibatis.ibator.api.ProgressCallback;
import org.apache.ibatis.ibator.api.dom.java.CompilationUnit;
import org.apache.ibatis.ibator.api.dom.xml.Document;
import org.apache.ibatis.ibator.generator.AbstractGenerator;
import org.apache.ibatis.ibator.generator.AbstractJavaGenerator;
import org.apache.ibatis.ibator.generator.AbstractXmlGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.dao.DAOGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.dao.templates.AbstractDAOTemplate;
import org.apache.ibatis.ibator.generator.ibatis2.dao.templates.GenericCIDAOTemplate;
import org.apache.ibatis.ibator.generator.ibatis2.dao.templates.GenericSIDAOTemplate;
import org.apache.ibatis.ibator.generator.ibatis2.dao.templates.IbatisDAOTemplate;
import org.apache.ibatis.ibator.generator.ibatis2.dao.templates.SpringDAOTemplate;
import org.apache.ibatis.ibator.generator.ibatis2.ext.ControllerGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.ext.HtmlGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.ext.ServiceGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.model.BaseRecordGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.model.ExampleGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.model.PrimaryKeyGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.model.RecordWithBLOBsGenerator;
import org.apache.ibatis.ibator.generator.ibatis2.sqlmap.SqlMapGenerator;
import org.apache.ibatis.ibator.internal.IbatorObjectFactory;
import org.apache.ibatis.ibator.internal.util.StringUtility;

/**
 * 
 * @author Jeff Butler
 *
 */
public class IntrospectedTableIbatis2Java2Impl extends IntrospectedTable {
    protected List<AbstractJavaGenerator> javaModelGenerators;
    protected List<AbstractJavaGenerator> daoGenerators;
    protected List<AbstractJavaGenerator> serviceGenerators;
    protected List<AbstractJavaGenerator> controllerGenerators;
    protected AbstractXmlGenerator sqlMapGenerator;
	protected HtmlGenerator htmlGenerator;

    public IntrospectedTableIbatis2Java2Impl() {
        super();
        javaModelGenerators = new ArrayList<AbstractJavaGenerator>();
        daoGenerators = new ArrayList<AbstractJavaGenerator>();
        serviceGenerators = new ArrayList<AbstractJavaGenerator>();
        controllerGenerators = new ArrayList<AbstractJavaGenerator>();
    }

    @Override
    public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
        calculateJavaModelGenerators(warnings, progressCallback);
        calculateDAOGenerators(warnings, progressCallback);
        calculateSqlMapGenerator(warnings, progressCallback);
        calculateServiceGenerator(warnings, progressCallback);
        calculateControllerGenerator(warnings, progressCallback);
        calculateHtmlGenerator(warnings, progressCallback);
    }
    
    private void calculateControllerGenerator(List<String> warnings, ProgressCallback progressCallback) {
    	if (ibatorContext.getControllerGeneratorConfiguration() == null) {
    		return;
    	}
    	AbstractJavaGenerator javaGenerator = new ControllerGenerator();
    	initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
    	controllerGenerators.add(javaGenerator);
	}

	private void calculateServiceGenerator(List<String> warnings, ProgressCallback progressCallback) {
		if (ibatorContext.getServiceGeneratorConfiguration() == null) {
            return;
        }
        AbstractJavaGenerator javaGenerator = new ServiceGenerator();
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        serviceGenerators.add(javaGenerator);
	}

	protected void calculateHtmlGenerator(List<String> warnings, ProgressCallback progressCallback) {
    	htmlGenerator = new HtmlGenerator();
    	initializeAbstractGenerator(htmlGenerator, warnings, progressCallback);
	}

	protected void calculateSqlMapGenerator(List<String> warnings, ProgressCallback progressCallback) {
        sqlMapGenerator = new SqlMapGenerator();
        initializeAbstractGenerator(sqlMapGenerator, warnings, progressCallback);
    }
    
    protected void calculateDAOGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (ibatorContext.getDaoGeneratorConfiguration() == null) {
            return;
        }
        
        String type = ibatorContext.getDaoGeneratorConfiguration().getConfigurationType();
        
        AbstractDAOTemplate abstractDAOTemplate;
        if ("IBATIS".equalsIgnoreCase(type)) { //$NON-NLS-1$
            abstractDAOTemplate = new IbatisDAOTemplate();
        } else if ("SPRING".equalsIgnoreCase(type)) { //$NON-NLS-1$
            abstractDAOTemplate = new SpringDAOTemplate();
        } else if ("GENERIC-CI".equalsIgnoreCase(type)) { //$NON-NLS-1$
            abstractDAOTemplate = new GenericCIDAOTemplate();
        } else if ("GENERIC-SI".equalsIgnoreCase(type)) { //$NON-NLS-1$
            abstractDAOTemplate = new GenericSIDAOTemplate();
        } else {
            abstractDAOTemplate = (AbstractDAOTemplate) IbatorObjectFactory.createInternalObject(type);
        }

        AbstractJavaGenerator javaGenerator = new DAOGenerator(abstractDAOTemplate, isJava5Targeted());
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        daoGenerators.add(javaGenerator);
    }
    
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator(isJava5Targeted());
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        
        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        
        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new BaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
        
        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
    }
    
    protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings, ProgressCallback progressCallback) {
        abstractGenerator.setIbatorContext(ibatorContext);
        abstractGenerator.setIntrospectedTable(this);
        abstractGenerator.setProgressCallback(progressCallback);
        abstractGenerator.setWarnings(warnings);
    }
    
    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
        
        for (AbstractJavaGenerator javaGenerator : javaModelGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, ibatorContext.getJavaModelGeneratorConfiguration().getTargetProject());
                answer.add(gjf);
            }
        }
        
        if(ibatorContext.getDaoGeneratorConfiguration() != null){
        	for (AbstractJavaGenerator javaGenerator : daoGenerators) {
        		List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
        		for (CompilationUnit compilationUnit : compilationUnits) {
        			GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, ibatorContext.getDaoGeneratorConfiguration().getTargetProject());
        			answer.add(gjf);
        		}
        	}
        	String generateModel = this.tableConfiguration.getGenerateModel();
        	if(!StringUtility.stringHasValue(generateModel)){
        		return answer;
        	}
        	if(!"1".equals(generateModel) && !"2".equals(generateModel) && !"3".equals(generateModel)){
        		return answer;
        	}
        	if(ibatorContext.getServiceGeneratorConfiguration() != null){
        		for(AbstractJavaGenerator javaGenerator : serviceGenerators){
        			List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
        			for (CompilationUnit compilationUnit : compilationUnits) {
        				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, ibatorContext.getServiceGeneratorConfiguration().getTargetProject());
        				answer.add(gjf);
        			}
        		}
        		if(ibatorContext.getControllerGeneratorConfiguration() != null && !"1".equals(generateModel)){
        			for(AbstractJavaGenerator javaGenerator : controllerGenerators){
        				List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
        				for (CompilationUnit compilationUnit : compilationUnits) {
        					GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, ibatorContext.getControllerGeneratorConfiguration().getTargetProject());
        					answer.add(gjf);
        				}
        			}
        		}
        	}
        }
        return answer;
    }

    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
        
        Document document = sqlMapGenerator.getDocument();
        GeneratedXmlFile gxf = new GeneratedXmlFile(document,
            getSqlMapFileName(),
            getSqlMapPackage(),
            ibatorContext.getSqlMapGeneratorConfiguration().getTargetProject(),
            ibatorContext.getSqlMapGeneratorConfiguration().getBaseFolder(),
            true);
        if (ibatorContext.getPlugins().sqlMapGenerated(gxf, this)) {
            answer.add(gxf);
        }
        
        return answer;
    }

    @Override
    public boolean isJava5Targeted() {
        return false;
    }

    @Override
    public int getGenerationSteps() {
        return javaModelGenerators.size()
            + daoGenerators.size()
            + serviceGenerators.size()
            + controllerGenerators.size()
            + 1   // 1 for the htmlGenerator
            + 1;  // 1 for the sqlMapGenerator
    }

	@Override
	public List<GeneratedHtmlFile> getGeneratedHtmlFiles(IntrospectedTable introspectedTable) {
		List<GeneratedHtmlFile> answer = new ArrayList<GeneratedHtmlFile>();
		if(ibatorContext.getHtmlGeneratorConfiguration() == null){
			return answer;
		}
		if(!StringUtility.stringHasValue(this.tableConfiguration.getGenerateModel())){
			return answer;
		}
		if(!"3".equalsIgnoreCase(this.tableConfiguration.getGenerateModel())){
			return answer;
		}
		String htmlText = htmlGenerator.getHtmlText();
		GeneratedHtmlFile gxf = new GeneratedHtmlFile(htmlText, format(introspectedTable.getBaseRecordType().getShortName()) + ".html", ibatorContext.getHtmlGeneratorConfiguration().getTargetPackage(), ibatorContext.getHtmlGeneratorConfiguration().getTargetProject(), ibatorContext.getHtmlGeneratorConfiguration().getBaseFolder());
		if (ibatorContext.getPlugins().htmlGenerated(gxf, this)) {
        	answer.add(gxf);
        }
    	return answer;
	}
	
	private static String format(String className){
    	return String.valueOf(className.charAt(0)).toLowerCase() + className.substring(1);
	}
	
}
