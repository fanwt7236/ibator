## 2017-02-19 在sqlMapGenerator上新增了配置项:namespaceGenerateBy，如果配置为entity，则sqlMap的namespace将会设置为实体类的全限定名；否则则会使用dao接口的全限定名。
## 2017-02-17 提出了BaseColumns 可配置不生成，默认生成
## 2017-02-14 丰富实现，自动生成更多的代码，减少工作量
    目前的插件已经能根据我们的配置生成sqlMap、dao、dao实现类、实体类文件了，我们不妨再尝试生成更多的代码，比如Service类、Controller类、html页面。
    因为本人使用SpringMVC+easyUI较多，因此我会以SpringMVC和easyUI为基础生成代码。
    生成Service、Controller、Html页面的控制权希望能够放在配置文件的table元素上，但是同时又需要一个用来配置文件生成package的公共配置；这样我们的需求大致就列出来了：
    1.可以配置serviceGenerator、controllerGenerator、htmlGenerator来控制service、controller、html文件的生成目录，同时要考虑html文件在maven项目中只在src/main/java目录下生成文件的不良体验，希望能配置生成的基础路径；配置方式可以使用baseFolder="java/resources/webapp"来配置
    2.table上可以配置该表是否生成配套的Service、Controller、Html，可以只生成Service或Service+Controller或Service、Controller、Html，配置方式可以使用generateModel="1/2/3"的方式来配置
    3.同时考虑sqlMap文件在maven工程下的生成路径，希望能够与需求1相同，可在sqlMapGenerator上配置生成的基础路径.
    4.页面中最好可以把字段上注释括号中的枚举值做成select下拉框.
    5.这部分新增的功能与个人习惯有很大关系，所以这部分配置不做成默认配置
    详细参见代码吧，基本都是参考其它原有实现写的，这里需要注意一点的是，如果需要根据数据库字段生成枚举的话，字段描述的格式需要这样写：是否被删除（0:否；1:是），其中括号需要成对出现，枚举值之间用分号隔开。
## 2017-02-13 扩展生成的方法
    今天主要修改的有以下几个方面:
    1.修改各种blob涉及的方法，ibator默认会将blob字段单独处理，但是这些字段不算常用（对我来说），但是又不想偶尔使用一个大文本数据类型造成生成多余的方法，因此计划重写一下IntrospectedColumn中的方法，忽略blob类型单独生成。好在ibator这次提供了扩展的地方，而且还挺合适，在ibator配置文件中可以通过给ibatorContext节点添加introspectedColumnImpl属性即可。修改内容如下:
    org.apache.ibatis.ibator.internal.IbatorObjectFactory.createIntrospectedColumn 修改默认选项
    org.apache.ibatis.ibator.api.DefaultIntrospectedColumn 新增并继承IntrospectedColumn，重写isBLOBColumn方法，直接返回false
    2.修改默认方法
	目前默认生成的方法有deleteByPrimaryKey、insert、insertSelective、selectByPrimaryKey、updateByPrimaryKeySelective、updateByPrimaryKey  
	按照个人习惯来讲的话:
	insert我需要的是insertSelective和insertBatch,少数场景会用到批量插入，批量插入很依赖数据库，我这里使用的是平时我最常用的mysql来实现的。
	update需要的是update和updateByEntity(保留原updateByPrimaryKeySelective方法；另外需要考虑条件字段和更新字段为同一个字段的场景，所以再提供一个updateByEntity方法；这样基本单表更新的场景就可以满足了。)
	delete需要的也是deleteByEntity
	select的话有两个常用的方法selectOne和selectList
	还有一个select方法:count用来统计数量
	这样的话大致的修改方案就出来了，由于updateByPrimaryKeySelective中已经封装了多条件更新，可以稍加修改变成多条件查询，当然这里一改就改两套（dao接口和sqlmap）。为了使得方法名简单明了，这里定义下面这些方法名来满足上面列出的需求。
	insert:实现按照原insertSelective实现，可减少sql长度，提升效率,但是要给个返回值类型int，用来表示该条记录影响的记录行数，这样做是为了在一些极端场景下判断insert动作是否成功。ibatis本身不支持insert返回影响行数，这个之后的一些代码中逐步把这个功能加上（使用ibatis的selectKey功能时，是可以给传入的对象进行复制的，不需要单独返回selectKey的查询结果，至少我遇到的场景都是这样的）
	insertBatch:批量新增
	update:实现按照原updateByPrimaryKeySelective实现，实现按照主键进行更新记录
	updateByEntity:传入两个实体对象实现条件更新，由于关系到了两个条件，因此这里给两个条件分别起个别名:s(set)和w(where) 
	delete:实现条件删除
	selectOne:条件查询单个对象
	selectList:条件查询列表，这里会涉及到泛型
	count:条件查询数量
	这次的代码基本都是参照其它实现类的代码去写的，新增了很多，就不一一叙述了。
	另外，我感觉dao具有很强的模板性和规律性，如之前提到过的dao实现非常模板化，可以通过公共或代理的方式完成，从而可以减少dao文件数量，使代码变得清晰美观；同时，dao接口也有很类似的特性，而且dao本身不关心任何业务，我们可以使用一个公共的包含泛型的dao接口充当门面，从而也减少dao接口文件的数量，大大减少非业务代码量；然而使用公共的dao接口还是使用各个表的dao接口，完全取决与程序员个人的习惯，因此在这里我让ibator同时支持了这两种情况，如果配置了daoGenerator的话，那么会按照table去生成dao接口，如果没有配置，则不生成dao接口，如果配置了daoGenerator且设置了implementationPackage属性的话，则会生成dao实现。
	3.添加了toString功能,详细请参见org.apache.ibatis.ibator.plugins.ToStringPlugin
	4.添加了枚举常量功能，详细请参见org.apache.ibatis.ibator.plugins.ConstantPlugin
## 2017-02-10 修改默认生成策略（默认是根据我的个人习惯来说的）
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
    修改sqlmap文件文件名规则，修改代码如下:
    org.apache.ibatis.ibator.api.IntrospectedTable 修改sqlmap文件名为实体类名.xml
## 2017-02-10 删除僵尸注释，添加有效注释
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