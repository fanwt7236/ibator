/*
 *  Copyright 2005 The Apache Software Foundation
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
package org.apache.ibatis.ibator.api;

/**
 * @author fanwt7236@163.com
 */
public class GeneratedHtmlFile extends GeneratedFile {
	
	private String htmlText;
	private String fileName;
	private String targetPackage;
	private String baseFolder;

	public GeneratedHtmlFile(String htmlText, String fileName, String targetPackage, String targetProject, String baseFolder) {
		super(targetProject);
		this.htmlText = htmlText;
		this.fileName = fileName;
		this.targetPackage = targetPackage;
		this.baseFolder = baseFolder;
	}
	
	public String getBaseFolder() {
		return baseFolder;
	}

	@Override
	public String getFormattedContent() {
		return this.htmlText;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public String getTargetPackage() {
		return this.targetPackage;
	}

	@Override
	public boolean isMergeable() {
		return false;
	}
}
