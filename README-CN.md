#   Jdbc-Template-Plus

[![codebeat badge](https://codebeat.co/badges/2bb6c4b8-0c08-4cb7-a51e-5f041402357e)](https://codebeat.co/projects/github-com-skycloud-cn-jdbctemplateplus-master)
[![Build Status](https://travis-ci.org/skyCloud-CN/jdbcTemplatePlus.svg?branch=master)](https://travis-ci.org/skyCloud-CN/jdbcTemplatePlus)

面向Java web开发者的基于SpringJdbcTemplate的 ORM 框架, 使用 Jdbc-Template-Plus 你可以

- **无需再写SQL**
- **实现DAO层仅需1分钟**
- **使用ReflectASM和缓存, 比Mybatis快3倍**
- **通过流式API写动态Sql,免于if else 地狱**
- **通过注解支持分表,逻辑删除,类型转换等功能, 支持自定义插件**



## 目录


- [背景](#背景)
- [快速开始](#快速开始)
- [使用指南](#使用指南)
- [版权](#版权)




## 背景

如果你是一个JavaWeb开发者, 那么你一定也会觉得Dao层的开发是个**非常非常无聊**的工作, 千篇一律的CRUD会浪费大量的时间.

目前也存在很多比较好用且被广泛使用的框架来解决这一问题, 比如 Mybatis, JdbcTemplate, SpringDataJPA, 但是对于互联网公司99%的SQL操作都是单表查询的情况, 这些框架多少都有一些缺陷 

Mybatis 开创性的实现了对动态SQL的支持, 但是写xml真的会写到吐血. 虽然xml可以通过强大的MybatisGenerator来生成, 但是项目里一堆xml看着也很烦也难以维护 ,而且每次改字段都要重新生成一遍很麻烦

JdbcTemplate 更加专注于底层, 提供了比较强大的API供我们实现DAO层,  但是还是需要自己去写SQL, 并且对动态SQL的支持也不是很好, 尤其是当执行的时候因为缺"\`"或者","或者空格等情况导致程序抛出SQL Syntax Error的情况下,找bug的过程真的是生无可恋, 相信有相同经历的同学都能懂.

SpringDataJPA是一个我很喜欢的框架, 也从中吸取了很多的灵感, 但是JPA太重了, 生成的SQL不可控且可读性差也广为诟病, 不是很适用于互联网公司仅用单表查询的场景,并且动态SQL的API也不够优美.

JdbcTemplatePlus就是为了解决以上问题, 专为简化开发而生



**当然, 目前框架才刚完成开发不久, 希望大家多多提 Issue, 当然更希望各位客官能给个STAR啦**




## 快速开始

**这里将会使用一个用户表的例子来教会你如何使用该框架**

首先我们的用户Model定义如下

> 这里用了@Data 和 @ToString 两个 lombok 注解,但是他们并不是必须的, 你也可以手动写getter和setter

```java
@Data
@ToString
public class User {
    private Long id;
    private String name;
    private Date updated;
    private Date created;
    private Boolean deleted;
}
```

### 1. 添加Maven依赖

> 目前该项目还在测试和完善阶段, 没有上传到Maven中央仓库, 不过应该快了~嗯

```xml
<dependency>
  <groupId>io.github.skycloud-cn</groupId>
  <artifactId>jdbc-template-plus</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 2. 添加注解

像下面展示的一样添加两个注解, `@Table` 和 `@PrimaryKey`, 分别指定表名和主键

```java
@Data
@ToString
@Table(tableName = "user")
public class User {
    @PrimaryKey
    private Long id;
    private String name;
    private Date updated;
    private Date created;
    private Boolean deleted;
}
```

### 3. 定义查询帮助类

这个类是用来构造查询条件的,,  **需要确保`Column`类的构造参数和数据库字段名完全一致**

```java
public class Columns {
    public static final Column ID = new Column("id");
    public static final Column NAME = new Column("name");
    public static final Column UPDATED = new Column("updated");
    public static final Column CREATED = new Column("created");
    public static final Column DELETED = new Column("deleted");
}
```

### 4. 定义Dao层

所有的基本查询方法都由`BaseStorage`提供,  仅需指定使用的`NamedParameterJdbcOperator`, 这个类在spring配好`dataSource`之后就可以自动注入啦

```java
@Service
public class UserDAO extends BaseStorage<User, Long>{
    @Autowired
    NamedParameterJdbcOperations db;

    @Override
    protected NamedParameterJdbcOperations getJdbcTemplate() {
        return db;
    }
}
```

### 5.字段名映射检查(可选)

这一步取决于Java Model 和数据库的字段名的映射, 如果两者(包括类型和命名)完全一致, 那么这一步可以跳过

由于目前绝大多数情况下, Java Model采用驼峰命名, 而数据库表使用下划线命名, 所以对这种情况下进行了全局的配置支持,配置方法如下,注意,**由于框架里用了缓存, 所以这个配置需要在第一次执行SQL之前设置**, 目前可以在Spring启动时加入代码执行
```
FastDaoConfig.setMapUnderscoreToCamelCase(true)
```

如果你的数据库字段和Java Model的命名有一些字段之间的映射(包括类型,命名)不符合以上两种, 你可以选择改一下你的字段, 或者通过注解来解决这个问题, 注解使用方式如下(这个不用解释了吧)

```java
@Data
@ToString
@Table(tableName = "user")
public class User {
    @PrimaryKey
    private Long id;
    private String name;
    @ColumnMap(jdbcType = JDBCType.TIMESTAMP)
    private Long updated;
    private Date created;
    @ColumnMap(column = "deleted")
    private Boolean exist;
}
```


## 使用指南

### BaseStorage类提供的基本方法
>`insertSelective`和`updateSelective`会检查你的参数Model的各个字段是否为`null`,如果为`null`则不会被更新到数据库

```java

    /**
     * insert DATA to db, all field will be set include null
     * if you want to update only when none of field is null you can use this method
     */
    int insert(DATA model);

    /**
     * insert DATA to db, only non-null field will be insert
     * if you want null field to be default value in db ,you can use this method
     */
    int insertSelective(DATA model);

    int insert(InsertRequest insertRequest, Consumer<Number> doWithGeneratedKeyOnSuccess);

    /**
     * update by UpdateRequest
     * if you want to update only a few field, or want to update by condition, you can use this method
     */
    int update(UpdateRequest updateRequest);

    /**
     * update DATA by primaryKey, all field will be set include null
     * make sure primaryKey in DATA is set
     */
    int updateByPrimaryKey(DATA model);

    /**
     * update DATA by primaryKey, only non-null field will be updated
     */
    int updateByPrimaryKeySelective(DATA model);

    /**
     * delete by condition
     */
    int delete(DeleteRequest deleteRequest);

    /**
     * delete by primaryKey
     */
    int deleteByPrimaryKey(PRIM_KEY primaryKey);

    /**
     * count by condition
     */
    int count(CountRequest countRequest);

    /**
     * select by primaryKey
     */
    DATA selectByPrimaryKey(PRIM_KEY primaryKey);

    /**
     * select by QueryRequest
     * this method doesn't support extra function
     */
    List<DATA> select(QueryRequest queryRequest);

    /**
     * select by QueryRequest
     * if has multiple result, only return first row
     */
    DATA selectOne(QueryRequest queryRequest);

    /**
     * select by
     */
    <T> List<T> selectSingleField(QueryRequest queryRequest, Class<T> clazz);

    /**
     * support SqlFunction as request field
     */
    List<QueryResult<DATA>> selectAdvance(QueryRequest queryRequest);

    /**
     * multiple selection
     */
    List<DATA> selectByPrimaryKeys(Collection<PRIM_KEY> primaryKeys);

    /**
     * multiple selection
     */
    List<DATA> selectByPrimaryKeys(PRIM_KEY... primaryKeys);

    /**
     * select by a page, and count result will set to page
     */
    List<DATA> selectPage(QueryRequest request, Page page);


```



### 动态SQL API

这里用之前的用户类举个实际场景作为例子, 产品提需求说, 要做个后台用户查询页面, 需要可以根据id进行查询, 并可用用户名进行模糊查询, 还可以查询某段时间之内创建的用户, 以上条件都是可选项,并且我们还有逻辑删除字段, 按照id排序, 分页,一页20条. 

这个查询只需一个流式语句就可以搞定了~,并且可读性高,比较容易修改和维护

> 这里用了`import static` 依赖了之前创建的查询类


```java
public void selectUser(List<Long> ids, String name, Date dateBegin, Date dateEnd) {
        QueryRequest request = Request.queryRequest()
                .beginAndCondition()
                .andOptional(ID.equal(ids))
                .andOptional(NAME.like(name).matchLeft().matchRight())
                .andOptional(CREATED.gt(dateBegin).lt(dateEnd))
                .and(DELETED.equal(false))
                .endCondition()
                .addSort(ID, OrderEnum.ASC)
                .limit(20)
                .offset(0);
        dao.select(request);
}
```



### 插件

JdbcTemplate-Plus的很多功能都是通过插件的形式来实现的, 目前都**仅需一个注解**就可以实现, 包括自动注入, 逻辑删除等的支持

#### 自动注入

当表中包含`updated`,`created`字段的时候, 每次都要手动赋值非常的麻烦而且容易忘, 这里可以通过`@AutoFill`注解标注在字段名上, 通过注解可以实现在插入, 更新的时候自动赋值, 并且仅当字段中没有值的时候才会自动赋值(TODO:这个之后可能会加入强制更新的选项)

使用示例如下:
```java
@AutoFill(fillValue = AutoFillValueEnum.NOW,onOperation = {AutoFillOperation.INSERT,AutoFillOperation.UPDATE})
private Long updated;
@AutoFill(fillValue = AutoFillValueEnum.NOW,onOperation= AutoFillOperation.INSERT)
private Date created;
```

#### 逻辑删除
逻辑删除注解`@LogicDelete`会在`Query`,`Update`,`Count`等操作中的条件语句不包含逻辑删除字段的情况下自动加入`'logicDeleteField'=0`的条件. 并在`Update`,`Insert`等操作的时候如果逻辑删除字段为`null`则会自动赋值.
目前逻辑删除注解仅支持字段为 `Boolean`, `Integer`, `Long`的情况.

使用示例如下


```java
@LogicDelete
private Boolean deleted;
```

#### 分表

在执行SQL之前, 执行 `ShardUtil.setShardSuffixOnce(suffixHere)` 语句,则会在下一条SQL中的表名中加入`suffixHere`的后缀哦

#### 其他

如果有什么额外功能觉得比较好或者比较常用, 可以通过issue提给我~我会考虑添加的

#### do to list

- 支持乐观锁注解,提供一键乐观锁功能
- shard插件支持多数据源分库配置
- 支持批量插入功能
- 强化基于spring的配置功能
- 支持更多的jdbcType
## 版权

JdbcTemplatePlus is under the Apache 2.0 license. See the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0) file for details.
