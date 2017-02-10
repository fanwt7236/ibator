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

import org.apache.ibatis.ibator.api.dom.xml.Document;

/**
 * @author Jeff Butler
 */
public class GeneratedXmlFile extends GeneratedFile {
    private Document document;

    private String fileName;

    private String targetPackage;
    
    private boolean isMergeable;

    /**
     * 
     * @param document
     * @param fileName
     * @param targetPackage
     * @param targetProject
     * @param isMergeable true if the file can be merged by the built in
     *   XML file merger.  Files are mergeable if all mergeable elements contain
     *   id's with the "ibatorgenerated_" prefix.
     */
    public GeneratedXmlFile(Document document, String fileName, String targetPackage, String targetProject, boolean isMergeable) {
        super(targetProject);
        this.document = document;
        this.fileName = fileName;
        this.targetPackage = targetPackage;
        this.isMergeable = isMergeable;
    }

    /*
     *  (non-Javadoc)
     * @see org.apache.ibatis.ibator.api.GeneratedFile#getFormattedContent()
     */
	@Override
    public String getFormattedContent() {
        return document.getFormattedContent();
    }

    /**
     * @return Returns the fileName.
     */
	@Override
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Returns the targetPackage.
     */
	@Override
    public String getTargetPackage() {
        return targetPackage;
    }

    @Override
    public boolean isMergeable() {
        return isMergeable;
    }
}
