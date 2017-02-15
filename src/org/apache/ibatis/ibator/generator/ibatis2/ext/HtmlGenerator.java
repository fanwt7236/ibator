package org.apache.ibatis.ibator.generator.ibatis2.ext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.ibator.api.IntrospectedColumn;
import org.apache.ibatis.ibator.api.dom.java.Field;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.ibatis.ibator.api.dom.java.JavaVisibility;
import org.apache.ibatis.ibator.generator.AbstractGenerator;

public class HtmlGenerator extends AbstractGenerator {

	private static String htmlModel = null;

	static {
		InputStream in = HtmlGenerator.class.getClassLoader()
				.getResourceAsStream("org/apache/ibatis/ibator/generator/ibatis2/ext/template.html");
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			htmlModel = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String setDialogFields() {
		StringBuilder sb = new StringBuilder();
		List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
		boolean isFirst = true;
		for (IntrospectedColumn introspectedColumn : introspectedColumns) {
			Field field = getJavaBeansField(introspectedColumn);
			if(isFirst){
				sb.append(field.getName()).append(" : ").append("data.obj.").append(field.getName());
				isFirst = false;
			}else{
				sb.append(",").append("\n").append("					");
				sb.append(field.getName()).append(" : ").append("data.obj.").append(field.getName());
			}
		}
		return sb.toString();
	}

	private String setTableHeadFields() {
		StringBuilder sb = new StringBuilder();
		List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
		boolean isFirst = true;
		for (IntrospectedColumn introspectedColumn : introspectedColumns) {
			Field field = getJavaBeansField(introspectedColumn);
			if(isFirst){
				sb.append("<th field=\"").append(field.getName()).append("\">").append(introspectedColumn.getCommentWithoutEnums()).append("</th>");
				isFirst = false;
			}else{
				sb.append("\n");
				sb.append("				<th field=\"").append(field.getName()).append("\">").append(introspectedColumn.getCommentWithoutEnums()).append("</th>");
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private String dialogFrame() {
		StringBuilder sb = new StringBuilder();
		List<IntrospectedColumn> introspectedColumns = introspectedTable.getNonPrimaryKeyColumns();
		boolean isFirst = true;
		for (IntrospectedColumn introspectedColumn : introspectedColumns) {
			Field field = getJavaBeansField(introspectedColumn);
			Properties props = introspectedColumn.getCommentEnums();
			if(isFirst){
				if(props.size() < 1){
					sb.append("<tr>\n").append("					").append("<td align=\"right\">"+introspectedColumn.getCommentWithoutEnums()+":</td>\n").append("					").append("<td><input class=\"easyui-textbox\" style=\"width: 220px\" type=\"text\" name=\""+field.getName()+"\" ").append(introspectedColumn.isNullable() ? "" : "data-options=\"required:true\"").append(" /></td>\n").append("				").append("</tr>");
				}else{
					sb.append("<tr>\n").append("					").append("<td align=\"right\">"+introspectedColumn.getCommentWithoutEnums()+":</td>\n").append("					");
					sb.append("<td><select class=\"easyui-combobox\" name=\""+field.getName()+"\">\n");
					Enumeration<String> names = (Enumeration<String>) props.propertyNames();
					while(names.hasMoreElements()){
						String name = names.nextElement();
						String value = props.getProperty(name);
						sb.append("						").append("<option value=\""+name+"\">"+value+"</option>\n");
					}
					sb.append("					</select></td>");
				}
				isFirst = false;
			}else{
				sb.append("\n");
				if(props.size() < 1){
					sb.append("				<tr>\n").append("					").append("<td align=\"right\">"+introspectedColumn.getCommentWithoutEnums()+":</td>\n").append("					").append("<td><input class=\"easyui-textbox\" style=\"width: 220px\" type=\"text\" name=\""+field.getName()+"\" ").append(introspectedColumn.isNullable() ? "" : "data-options=\"required:true\"").append(" /></td>\n").append("				").append("</tr>");
				}else{
					sb.append("				<tr>\n").append("					").append("<td align=\"right\">"+introspectedColumn.getCommentWithoutEnums()+":</td>\n").append("					");
					sb.append("<td><select class=\"easyui-combobox\" name=\""+field.getName()+"\">\n");
					Enumeration<String> names = (Enumeration<String>) props.propertyNames();
					while(names.hasMoreElements()){
						String name = names.nextElement();
						String value = props.getProperty(name);
						sb.append("						").append("<option value=\""+name+"\">"+value+"</option>\n");
					}
					sb.append("					</select></td>");
				}
			}
		}
		return sb.toString();
	}

	public Field getJavaBeansField(IntrospectedColumn introspectedColumn) {
		FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
		String property = introspectedColumn.getJavaProperty();

		Field field = new Field();
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setType(fqjt);
		field.setName(property);
		ibatorContext.getCommentGenerator().addFieldComment(field, introspectedTable.getFullyQualifiedTable(),
				introspectedColumn);

		return field;
	}

	public String getHtmlText() {
		String htmlText = new String(htmlModel);
		htmlText = replace(htmlText, "${controller}", paramBaseRecord());
		htmlText = replace(htmlText, "${setDialogFields}", setDialogFields());
		htmlText = replace(htmlText, "${setTableHeadFields}", setTableHeadFields());
		htmlText = replace(htmlText, "${dialogFrame}", dialogFrame());
		htmlText = replace(htmlText, "${primaryFields}", primaryFields());
		return htmlText;
	}
	
	private String primaryFields() {
		StringBuilder sb = new StringBuilder();
		List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
		boolean isFirst = true;
		for (IntrospectedColumn introspectedColumn : introspectedColumns) {
			Field field = getJavaBeansField(introspectedColumn);
			if(isFirst){
				sb.append("<input type=\"hidden\" name=\""+field.getName()+"\" />");
				isFirst = false;
			}else{
				sb.append("\n");
				sb.append("			<input type=\"hidden\" name=\""+field.getName()+"\" />");
			}
		}
		return sb.toString();
	}

	public static String replace(String text, String searchString, String replacement) {
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= 16;
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

}
