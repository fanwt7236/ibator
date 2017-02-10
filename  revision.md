2017-02-10 修改默认生成策略（默认是根据我的个人习惯来说的）
    1.Example文件和很多example方法。修改文件如下：
    org.apache.ibatis.ibator.config.TableConfiguration 修改包含Example的初始值为false
    2.删除生成sql时的statementId前缀“ibatorgenerated_”，
    org.apache.ibatis.ibator.config.MergeConstants 删除“ibatorgenerated_”
    3.修改生成的sqlmap文件的namespace为实体类的全限定名，这样修改是因为dao实现的操作步骤完全一致，可以使用公共的方法或者是动态代理（类似Spring+MyBatis）的方法减少编码，修改代码如下:
    org.apache.ibatis.ibator.generator.ibatis2.sqlmap.SqlMapGenerator 修改namespace生成规则
    org.apache.ibatis.ibator.generator.ibatis2.dao.elements.DeleteByExampleMethodGenerator
    org.apache.ibatis.ibator.generator.ibatis2.dao.elements.DeleteByPrimaryKeyMethodGenerator
    取消默认生成DAO实现,修改代码如下：
    org.apache.ibatis.ibator.generator.ibatis2.dao.DAOGenerator 复用implementationPackage属性，如果有定义则生成dao实现，如果没有就不生成
    修改sqlmap文件明规则，修改代码如下:
    org.apache.ibatis.ibator.api.IntrospectedTable 修改sqlmap文件名为实体类名.xml
--------------------------------------------------------------------------------------------------------------------------------------------------
2017-02-10 删除僵尸注释，添加有效注释
	ibator中有注释生成器接口org.apache.ibatis.ibator.api.CommentGenerator，并且在org/apache/ibatis/ibator/config/xml/ibator-config_1_0.dtd可以发现ibatorContext下可以配置commentGenerator，并且commentGenerator中有一个属性type,这个type就是CommentGenerator的实现类的qualifiedName,也就是说我们可以通过实现CommentGenerator来自由订制注释声称规则;然而在查看过CommentGenerator接口后，发现原有接口在打印字段描述上比较乏力，需要扩展，因此采用直接修改DefaultCommentGenerator的方式来实现功能。修改原有接口CommentGenerator，修改public void addFieldComment(Field field, FullyQualifiedTable table, String columnName)为public void addFieldComment(Field field, FullyQualifiedTable table, IntrospectedColumn introspectedColumn)，并且修改IntrospectedColumn文件，添加用来记录字段描述的属性:comment，解决掉编译错误，实现功能。另外，由于生成了注释，涉及到了字符集的问题，因此在最后生成文件的时候还要指定编码，为了方便的指定编码，我修改了ibator-config_1_0.dtd,在ibatorConfiguration下可以添加property,用charset来设置编码,如果不设置会使用utf-8。
	修改的文件及摘要如下：
	org/apache/ibatis/ibator/config/xml/ibator-config_1_0.dtd 修改了 ibatorConfiguration 的规则，添加property*
	org.apache.ibatis.ibator.api.CommentGenerator 修改了方法addFieldComment的参数，将String columnName 变为IntrospectedColumn introspectedColumn
	org.apache.ibatis.ibator.internal.DefaultCommentGenerator 删除了无用的方法体和属性，修改了方法addFieldComment，消除编译错误。
	org.apache.ibatis.ibator.generator.ibatis2.model.BaseModelClassGenerator 消除编译错误。
	org.apache.ibatis.ibator.api.IntrospectedColumn 添加变量comment及get/set方法
	org.apache.ibatis.ibator.internal.db.DatabaseIntrospector 添加获取字段注释的代码
	org.apache.ibatis.ibator.config.IbatorConfiguration 添加了继承关系继承于PropertyHolder，使其具有处理property的能力
	org.apache.ibatis.ibator.config.xml.IbatorConfigurationParser 添加了ibatorConfiguration解析property的代码
	org.apache.ibatis.ibator.api.Ibator 修改了写文件的方法writeFile，使其能根据配置文件进行修改
	org.apache.ibatis.ibator.config.PropertyRegistry 添加静态变量，IGNORE_FIELD和DEFAULT_CHARSET