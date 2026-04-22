# 项目代码规范

> 本规范基于项目现有代码模式提炼，所有新代码生成前必须严格遵循。每次生成代码前需先阅读本规范并按规范执行。

---

## 一、项目结构规范

### 1.1 Maven 模块分层

```
hc-spring-cloud-demo/                          # 根项目（pom）
├── hc-spring-common/                           # 通用模块（枚举、常量、工具类）
├── hc-spring-user-context-core/                # 用户上下文核心模块
├── hc-spring-gateway-demo/                     # 网关模块
├── hc-spring-user-demo/                        # 用户服务（pom）
│   ├── hc-spring-user-api/                     # Feign DTO + Feign Client 接口
│   ├── hc-spring-user-client/                  # Feign Client 实现 + FallbackFactory
│   └── hc-spring-user-biz/                     # 业务实现
└── hc-spring-order-demo/                       # 订单服务（pom）
    ├── hc-spring-order-api/
    ├── hc-spring-order-client/
    └── hc-spring-order-biz/
```

### 1.2 biz 模块包结构

```
com.hnhegui.hc
├── entity/                  # 实体类（按业务子包分组）
│   ├── user/                #   User.java, UserRole.java
│   ├── role/
│   ├── permission/
│   ├── cuser/               #   CUser.java, CUserThirdParty.java
│   ├── enterprise/          #   Enterprise.java, EnterpriseUser.java
│   ├── log/                 #   LoginLog.java, OperationLog.java
│   └── verify/              #   VerificationCode.java, AccountLock.java
├── mapper/                  # Mapper接口（与entity子包一一对应）
│   ├── user/                #   UserMapper.java, UserRoleMapper.java
│   ├── role/
│   ├── permission/
│   ├── cuser/
│   ├── enterprise/
│   ├── log/
│   └── verify/
├── bo/                      # 业务对象（按业务子包分组）
│   └── user/                #   UserBO.java, UserCreateBO.java, UserPageQueryBO.java
├── service/                 # 服务接口 + 实现（按业务子包分组）
│   ├── user/                #   UserService.java（接口）
│   │   └── impl/            #   UserServiceImpl.java（实现）
│   ├── role/
│   │   └── impl/
│   ├── permission/
│   │   └── impl/
│   ├── auth/                #   PasswordService.java（无接口，直接类）
│   ├── cuser/               #   CUserService.java
│   ├── enterprise/          #   EnterpriseService.java, EnterpriseUserService.java
│   ├── log/                 #   LoginLogService.java
│   └── verify/              #   AccountLockService.java, VerificationCodeService.java
├── controller/              # 控制器（按业务子包分组）
│   ├── user/                #   UserController.java
│   │   ├── converter/       #   UserConverter.java（MapStruct转换器）
│   │   ├── request/         #   UserRequest.java, UserPageRequest.java, AssignRolesRequest.java
│   │   └── response/        #   UserResponse.java, UserExportResponse.java
│   ├── role/
│   │   ├── converter/
│   │   ├── request/
│   │   └── response/
│   ├── permission/
│   │   ├── converter/
│   │   ├── request/
│   │   └── response/
│   ├── auth/                #   AuthController.java
│   ├── cuser/               #   CUserController.java
│   ├── enterprise/          #   EnterpriseController.java
│   └── log/                 #   LogController.java
├── internal/                # 内部Feign实现（供跨服务调用）
│   └── user/
│       └── converter/       #   UserDTOConverter.java
├── config/                  # 配置类
└── publisher/               # 消息发布
```

### 1.3 Mapper XML 目录结构

```
src/main/resources/
├── mapper/                  # Mapper XML（与mapper包子包一一对应）
│   ├── user/                #   UserMapper.xml, UserRoleMapper.xml
│   ├── role/                #   RoleMapper.xml, RolePermissionMapper.xml
│   ├── permission/          #   PermissionMapper.xml
│   ├── cuser/               #   CUserMapper.xml, CUserThirdPartyMapper.xml
│   ├── enterprise/          #   EnterpriseMapper.xml, EnterpriseUserMapper.xml
│   ├── log/                 #   LoginLogMapper.xml, OperationLogMapper.xml
│   └── verify/              #   VerificationCodeMapper.xml, AccountLockMapper.xml
└── sql/                     # SQL 脚本
```

---

## 二、Entity 实体类规范

### 2.1 基本规则

- **所有实体类必须继承 `BaseEntity`**，`BaseEntity` 已包含 `id, creator, updater, createTime, updateTime, deleted` 字段，实体类中**禁止重复定义**这些字段
- 使用 `@TableName("表名")` 注解指定表名
- 使用 `@EqualsAndHashCode(callSuper = true)` + `@Data` + `@EqualsAndHashCode(callSuper = true)`
- 每个字段必须有 Javadoc 注释
- 字段类型使用包装类型（`Integer` 而非 `int`，`Long` 而非 `long`）

### 2.2 模板

```java
package com.hnhegui.hc.entity.{子包};

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("{表名}")
public class {ClassName} extends BaseEntity {

    /**
     * 字段注释
     */
    private {Type} fieldName;
}
```

### 2.3 现有示例

```java
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Integer status;
}
```

---

## 三、Mapper 规范

### 3.1 基本规则

- 继承 `BaseMapper<Entity>`，泛型为对应实体类
- Mapper 接口与 Entity 在对应的同名子包下
- **所有查询逻辑（包括 `LambdaQueryWrapper`、条件构造）必须在 Mapper 中定义**，Service 层禁止出现 `LambdaQueryWrapper` / `QueryWrapper` / `Wrappers`
- 简单的条件查询在 Mapper 中用 `default` 方法实现，使用 `LambdaQueryWrapper` + `Wrappers`
- 复杂查询、多表关联查询在 Mapper 中声明抽象方法，在对应 XML 中手写 SQL
- `insertBatch`、`insertOrUpdateBatch` 等批量操作必须在 Mapper 中声明，在对应 XML 中手写 SQL

### 3.2 Mapper 接口模板

```java
package com.hnhegui.hc.mapper.{子包};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.{子包}.{Entity}PageQueryBO;
import com.hnhegui.hc.entity.{子包}.{Entity};
import org.apache.ibatis.annotations.Param;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

public interface {Entity}Mapper extends BaseMapper<{Entity}> {

    /**
     * 批量插入
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<{Entity}> list);

    /**
     * 批量插入或更新（存在则更新，不存在则插入）
     *
     * @param list 实体集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<{Entity}> list);

    /**
     * 分页查询{实体名}列表
     *
     * @param queryBO 分页查询参数
     * @return 分页结果
     */
    default Page<{Entity}> select{Entity}sByPage({Entity}PageQueryBO queryBO) {
        LambdaQueryWrapper<{Entity}> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StrUtil.isNotBlank(queryBO.getXxx()), {Entity}::getXxx, queryBO.getXxx());
        return this.selectPage(queryBO.toPage(), wrapper);
    }

    /**
     * 根据XXX查询（简单条件查询用 default 方法）
     *
     * @param xxx 查询条件
     * @return 实体
     */
    default {Entity} selectByXxx(String xxx) {
        return this.selectOne(Wrappers.<{Entity}>lambdaQuery()
            .eq({Entity}::getXxx, xxx));
    }
}
```

### 3.3 Mapper XML 模板

- XML 文件放在 `src/main/resources/mapper/{子包}/` 下
- 文件名与 Mapper 接口同名：`{Entity}Mapper.xml`
- 批量插入、批量更新必须手写 SQL

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.hnhegui.hc.mapper.{子包}.{Entity}Mapper">

    <!-- 批量插入 -->
    <insert id="insertBatch">
        INSERT INTO {表名} (field1, field2, creator, updater, create_time, update_time, deleted)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.field1}, #{item.field2}, #{item.creator}, #{item.updater}, NOW(), NOW(), 0)
        </foreach>
    </insert>

    <!-- 批量插入或更新 -->
    <insert id="insertOrUpdateBatch">
        INSERT INTO {表名} (field1, field2, creator, updater, create_time, update_time, deleted)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.field1}, #{item.field2}, #{item.creator}, #{item.updater}, NOW(), NOW(), 0)
        </foreach>
        ON DUPLICATE KEY UPDATE
            field1 = VALUES(field1),
            field2 = VALUES(field2),
            updater = VALUES(updater),
            update_time = NOW()
    </insert>

</mapper>
```

### 3.4 现有示例（UserMapper）

```java
public interface UserMapper extends BaseMapper<User> {

    int insertBatch(@Param("list") List<User> list);

    int insertOrUpdateBatch(@Param("list") List<User> list);

    default Page<User> selectUsersByPage(UserPageQueryBO queryBO) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StrUtil.isNotBlank(queryBO.getUsername()), User::getUsername, queryBO.getUsername())
            .eq(StrUtil.isNotBlank(queryBO.getPhone()), User::getPhone, queryBO.getPhone())
            .eq(StrUtil.isNotBlank(queryBO.getName()), User::getName, queryBO.getName());
        return this.selectPage(queryBO.toPage(), wrapper);
    }
}
```

---

## 四、BO（业务对象）规范

### 4.1 基本规则

- BO 是 Service 层与 Controller 层之间的数据传输对象
- 使用 `@Data` 注解
- 每个字段必须有 Javadoc 注释
- BO 按用途分为：
  - **{Entity}BO** - 通用业务对象（查询结果）
  - **{Entity}CreateBO** - 创建/更新业务对象
  - **{Entity}PageQueryBO** - 分页查询参数，继承 `PageParam`

### 4.2 模板

```java
// 通用BO
@Data
public class UserBO {
    /**
     * ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
}

// 创建BO
@Data
public class UserCreateBO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
}

// 分页查询BO
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageQueryBO extends PageParam {
    /**
     * 用户名
     */
    private String username;
}
```

---

## 五、Service 规范

### 5.1 接口规范

- Service 接口继承 `BaseService<Entity>`（框架提供，包含 `insertBatch`、`insertOrUpdateBatch` 等）
- 方法返回 BO 对象，不直接返回 Entity
- 每个方法必须有 Javadoc 注释（`@param`、`@return`）
- 方法命名：`getXxx`、`listXxx`、`saveXxx`、`updateXxx`、`deleteXxx`

### 5.2 实现类规范

- 继承 `BaseServiceImpl<Mapper, Entity>` 并实现对应 Service 接口
- 使用 `@Service` + `@RequiredArgsConstructor`
- 依赖注入使用 `private final` + 构造器注入（`@RequiredArgsConstructor`）
- 使用 `// ====================== 分组名 ======================` 分隔注释区分方法分组
- 对象转换统一使用 `{Entity}Converter.INSTANCE.{方法名}()`
- 事务操作使用 `TransactionTemplate`（编程式事务），不使用 `@Transactional`
- **同一 Service 方法中有多次增删改操作时，必须使用 `TransactionTemplate` 保证事务一致性**
- **跨服务调用（Feign）必须在设计方案中明确一致性要求**：强一致性 or 最终一致性
  - 强一致性：通过分布式事务（Seata TCC / Saga）保证
  - 最终一致性：通过消息队列（事件驱动）+ 补偿机制保证
- 缓存使用 Spring Cache 注解：`@Cacheable`、`@CachePut`、`@CacheEvict`、`@Caching`
- 缓存 key 格式：`{业务名}#{时间}`，如 `user#1h`、`user#10m`
- **Service 层禁止出现 `LambdaQueryWrapper` / `QueryWrapper` / `Wrappers`**，所有查询条件构造必须放在 Mapper 中
- Service 层只调用 Mapper 方法，不自行构造查询条件
- **禁止在循环中执行数据库查询**，应先批量查出数据再用 Map 分组匹配
- **禁止在循环中执行远程调用（Feign）**，应先批量查出数据再用 Map 分组匹配

### 5.3 模板

```java
@Service
@RequiredArgsConstructor
public class {Entity}ServiceImpl extends BaseServiceImpl<{Entity}Mapper, {Entity}> implements {Entity}Service {

    private final {Entity}Mapper {entity}Mapper;
    private final TransactionTemplate transactionTemplate;

    // ====================== 查询 ======================
    @Override
    @Cacheable(value = "{entity}#1h", key = "#id")
    public {Entity}BO get{Entity}ById(Long id) {
        {Entity} entity = {entity}Mapper.selectById(id);
        return {Entity}Converter.INSTANCE.entityToBo(entity);
    }

    // ====================== 保存 ======================
    @Override
    @CachePut(value = "{entity}#1h", key = "#result.id")
    public {Entity}BO save{Entity}({Entity}CreateBO createBO) {
        {Entity} entity = {Entity}Converter.INSTANCE.createBoToEntity(createBO);
        {entity}Mapper.insert(entity);
        return {Entity}Converter.INSTANCE.entityToBo(entity);
    }

    // ====================== 多次增删改（需事务） ======================
    @Override
    public void save{Entity}WithDetail({Entity}CreateBO createBO) {
        transactionTemplate.executeWithoutResult(status -> {
            {Entity} entity = {Entity}Converter.INSTANCE.createBoToEntity(createBO);
            {entity}Mapper.insert(entity);
            // 第二次增删改操作
            {entity}Mapper.insertXxx(entity.getId());
        });
    }

    // ====================== 删除 ======================
    @Override
    @Caching(evict = {
        @CacheEvict(value = "{entity}#1h", key = "#id"),
        @CacheEvict(value = "{entity}#10m", key = "'list'")
    })
    public boolean delete{Entity}(Long id) {
        return {entity}Mapper.deleteById(id) > 0;
    }

    @Override
    public int insertBatch(List<{Entity}> list) {
        return {entity}Mapper.insertBatch(list);
    }

    @Override
    public int insertOrUpdateBatch(List<{Entity}> list) {
        return {entity}Mapper.insertOrUpdateBatch(list);
    }
}
```

---

## 六、Converter（MapStruct 转换器）规范

### 6.1 基本规则

- 使用 `@Mapper` 注解（MapStruct 的注解，非 Spring 的）
- 通过 `Mappers.getMapper()` 创建单例实例：`INSTANCE`
- 转换器放在 `controller/{子包}/converter/` 下
- 方法命名统一规范：
  - `entityToBo` - Entity → BO
  - `entityToBoList` - List<Entity> → List<BO>
  - `createBoToEntity` - CreateBO → Entity
  - `toResponse` - BO → Response
  - `toResponseList` - List<BO> → List<Response>
  - `toResponsePage` - Page<BO> → Page<Response>
  - `entityPageToBoPage` - Page<Entity> → Page<BO>
  - `requestToCreateBo` - Request → CreateBO
  - `requestToPageBo` - PageRequest → PageQueryBO
  - `toExportResponseList` - List<BO> → List<ExportResponse>
- 调用方式：`{Entity}Converter.INSTANCE.{方法名}()`

### 6.2 模板

```java
package com.hnhegui.hc.controller.{子包}.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnhegui.hc.bo.{子包}.{Entity}BO;
import com.hnhegui.hc.bo.{子包}.{Entity}CreateBO;
import com.hnhegui.hc.bo.{子包}.{Entity}PageQueryBO;
import com.hnhegui.hc.controller.{子包}.request.{Entity}PageRequest;
import com.hnhegui.hc.controller.{子包}.request.{Entity}Request;
import com.hnhegui.hc.controller.{子包}.response.{Entity}Response;
import com.hnhegui.hc.entity.{子包}.{Entity};
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface {Entity}Converter {
    {Entity}Converter INSTANCE = Mappers.getMapper({Entity}Converter.class);

    {Entity}Response toResponse({Entity}BO bo);
    List<{Entity}Response> toResponseList(List<{Entity}BO> list);
    {Entity}BO entityToBo({Entity} entity);
    List<{Entity}BO> entityToBoList(List<{Entity}> list);
    {Entity} createBoToEntity({Entity}CreateBO createBO);
    {Entity}CreateBO requestToCreateBo({Entity}Request request);
    {Entity}PageQueryBO requestToPageBo({Entity}PageRequest request);
    Page<{Entity}Response> toResponsePage(Page<{Entity}BO> boPage);
    Page<{Entity}BO> entityPageToBoPage(Page<{Entity}> entityPage);
}
```

---

## 七、Request / Response 规范

### 7.1 Request

- 使用 `@Data` 注解
- 分页查询 Request 继承 `PageParam`
- 放在 `controller/{子包}/request/` 下
- 类名格式：`{Entity}Request`、`{Entity}PageRequest`、`{Action}Request`

### 7.2 Response

- 使用 `@Data` 注解
- 导出专用 Response 使用 `@ExcelProperty` 注解，类名格式：`{Entity}ExportResponse`
- 放在 `controller/{子包}/response/` 下

### 7.3 模板

```java
// Request
@Data
public class UserRequest {
    private String username;
    private String password;
    private String name;
}

// PageRequest
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageRequest extends PageParam {
    private String username;
    private String name;
    private String phone;
}

// Response
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private Integer status;
    private LocalDateTime createTime;
}

// ExportResponse
@Data
public class UserExportResponse {
    private Long id;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("姓名")
    private String name;
}
```

---

## 八、Controller 规范

### 8.1 基本规则

- 使用 `@RestController` + `@RequestMapping("/{路径}")` + `@RequiredArgsConstructor`
- 依赖注入使用 `private final` + 构造器注入
- 返回值统一使用 `Result<T>` 包装
- 分页返回使用 `Result<PageData<T>>`，通过 `PageData.of(page)` 转换
- 每个方法必须有 Javadoc 注释
- Controller 中**不做业务逻辑**，仅做参数接收、Converter 转换、Service 调用、Result 返回
- 禁止在 Controller 中使用 `Map<String, String>` 接收参数，必须使用强类型 Request 对象
- 禁止在 Controller 中直接构造 `Map<String, Object>` 返回数据，必须使用强类型 Response 对象

### 8.2 URL 命名规范

| 操作 | HTTP方法 | URL格式 | 示例 |
|---|---|---|---|
| 列表查询 | GET | /{module}/list | `@GetMapping("/list")` |
| 分页查询 | GET | /{module}/page | `@GetMapping("/page")` |
| 详情查询 | GET | /{module}/get/{id} | `@GetMapping("/get/{id}")` |
| 新增 | POST | /{module}/add | `@PostMapping("/add")` |
| 修改 | PUT | /{module}/edit/{id} | `@PutMapping("/edit/{id}")` |
| 删除 | DELETE | /{module}/delete/{id} | `@DeleteMapping("/delete/{id}")` |
| 特殊操作 | POST | /{module}/{action} | `@PostMapping("/assign-roles")` |

### 8.3 模板

```java
@RestController
@RequestMapping("/{module}")
@RequiredArgsConstructor
public class {Entity}Controller {
    private final {Entity}Service {entity}Service;

    /**
     * 获取列表
     */
    @GetMapping("/list")
    public Result<List<{Entity}Response>> list() {
        List<{Entity}Response> list = {Entity}Converter.INSTANCE.toResponseList({entity}Service.list{Entity}s());
        return Result.success(list);
    }

    /**
     * 添加
     */
    @PostMapping("/add")
    public Result<{Entity}Response> add(@RequestBody {Entity}Request request) {
        {Entity}Response response = {Entity}Converter.INSTANCE.toResponse(
            {entity}Service.save{Entity}({Entity}Converter.INSTANCE.requestToCreateBo(request)));
        return Result.success(response);
    }

    /**
     * 编辑
     */
    @PutMapping("/edit/{id}")
    public Result<{Entity}Response> edit(@PathVariable Long id, @RequestBody {Entity}Request request) {
        {Entity}Response response = {Entity}Converter.INSTANCE.toResponse(
            {entity}Service.update{Entity}(id, {Entity}Converter.INSTANCE.requestToCreateBo(request)));
        return Result.success(response);
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = {entity}Service.delete{Entity}(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<PageData<{Entity}Response>> page(@Validated {Entity}PageRequest request) {
        Page<{Entity}Response> responsePage = {Entity}Converter.INSTANCE.toResponsePage(
            {entity}Service.list{Entity}sByPage({Entity}Converter.INSTANCE.requestToPageBo(request)));
        return Result.success(PageData.of(responsePage));
    }
}
```

---

## 九、Feign 跨服务调用规范

### 9.1 api 模块（hc-spring-{module}-api）

- 定义 Feign Client 接口和 DTO
- DTO 命名：`{Entity}DTO`，使用 `@Data`
- Feign Client 接口使用 `@FeignClient(name = "{service-name}", fallbackFactory = ...)` 
- 内部接口路径前缀：`/api/internal/{module}/`

### 9.2 client 模块（hc-spring-{module}-client）

- 实现 `FallbackFactory`，降级返回空集合/null
- FallbackFactory 使用 `@Component` + `@Slf4j`

### 9.3 biz 模块中的 internal 包

- 实现 Feign Client 接口，使用 `@RestController`
- 放在 `internal/{子包}/` 下
- 使用独立的 DTOConverter（MapStruct），放在 `internal/{子包}/converter/`
- **内部接口返回原始数据，不使用 Result 包装**

---

## 十、通用编码规范

### 10.1 依赖注入

- **统一使用构造器注入**：`private final` + `@RequiredArgsConstructor`
- 禁止使用 `@Autowired` 字段注入

### 10.2 密码加密

- 使用 `BCryptPasswordEncoder`（或 `PasswordEncoder` 接口）
- Bean 在 `PasswordEncoderConfig` 中配置

### 10.3 异常处理

- 业务异常使用 `RuntimeException` 抛出，带明确错误信息
- 由全局异常处理器统一捕获返回 `Result.error()`
- **禁止在 Service 层返回 null 表示失败**，应抛出异常

### 10.4 日志

- 使用 `@Slf4j` + Lombok
- 关键操作记录日志：`log.info("操作描述：param={}", param)`

### 10.5 数据层操作

- **所有查询条件构造（`LambdaQueryWrapper` 等）必须在 Mapper 层完成**，Service 层禁止出现
- Service 层只调用 Mapper 方法获取数据，不自行拼接查询条件
- 简单条件查询在 Mapper 中用 `default` 方法，复杂查询在 XML 中手写 SQL
- 逻辑删除由 BaseEntity + MyBatis-Plus 全局配置自动处理
- 物理删除需自定义 Mapper 方法（如 `deletePhysicalByRoleId`）

### 10.6 循环内操作规范

- **禁止在 for/while/foreach 循环中执行数据库查询**（N+1 问题）
  - 错误示例：`ids.forEach(id -> mapper.selectById(id))`
  - 正确做法：`mapper.selectBatchIds(ids)` 一次批量查出
- **禁止在 for/while/foreach 循环中执行远程调用（Feign）**
  - 错误示例：`users.forEach(u -> feignClient.getXxx(u.getId()))`
  - 正确做法：先收集所有需要的 ID，一次性批量查询，再用 Map 分组匹配
- 需要循环内关联数据时，先批量查出，转 `Map<key, value>` 后在循环中从 Map 取值

### 10.6 对象转换链路

```
Request → Converter.requestToCreateBo() → CreateBO → Converter.createBoToEntity() → Entity → DB
DB → Entity → Converter.entityToBo() → BO → Converter.toResponse() → Response
```

### 10.7 缓存规范

- 缓存注解加在 Service 实现类方法上
- key 格式：`{业务名}#{过期时间}`，如 `user#1h`、`user#10m`
- 保存用 `@CachePut`，查询用 `@Cacheable`，删除/更新用 `@CacheEvict`
- 多缓存操作用 `@Caching` 组合

---

## 十一、数据库规范

### 11.1 表命名

- 平台表前缀：`sys_`（如 `sys_user`, `sys_role`）
- 业务表前缀：`biz_`（如 `biz_enterprise`, `biz_enterprise_user`）
- C端表前缀：`c_`（如 `c_user`, `c_user_third_party`）
- 日志表前缀：`sys_`（如 `sys_login_log`, `sys_operation_log`）

### 11.2 基础字段

所有表必须包含 BaseEntity 对应的字段：

```sql
id          BIGINT AUTO_INCREMENT PRIMARY KEY,
creator     VARCHAR(50)  NULL COMMENT '创建人',
updater     VARCHAR(50)  NULL COMMENT '更新人',
create_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
update_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
deleted     TINYINT      DEFAULT 0 NOT NULL COMMENT '逻辑删除：0-未删除，1-已删除'
```

### 11.3 字段规范

- 状态字段使用 `TINYINT`，必须定义状态值的注释说明
- 使用 `utf8mb4` 字符集
- 唯一约束命名：`uk_{字段名}`
- 外键约束命名：`{子表}_ibfk_{序号}`
- 索引命名：`idx_{表名简写}_{字段名}`

---

## 十二、枚举与常量规范

### 12.1 基本规则

- **禁止在代码中出现魔法值**（硬编码的数字或字符串），必须抽取为枚举或常量
- 枚举统一放在 `hc-spring-common` 的 `com.hnhegui.hc.common.enums` 包下
- 常量类统一放在 `hc-spring-common` 的 `com.hnhegui.hc.common.constant` 包下
- **优先使用枚举**，仅当值过多或不适合枚举时才使用常量类

### 12.2 枚举模板

```java
package com.hnhegui.hc.common.enums;

import lombok.Getter;

/**
 * {枚举描述}
 */
@Getter
public enum {EnumName}Enum {

    /**
     * {枚举项描述}
     */
    {ITEM}({code}, "{desc}");

    private final {int|String} code;
    private final String desc;

    {EnumName}Enum({int|String} code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static {EnumName}Enum getByCode({int|String} code) {
        for ({EnumName}Enum e : values()) {
            if (e.code.equals(code) || e.code == code) {
                return e;
            }
        }
        return null;
    }
}
```

### 12.3 已定义的枚举

| 枚举类 | 说明 | 枚举值 |
|--------|------|--------|
| `UserTypeEnum` | 用户类型 | C(C端), B(B端), P(平台) |
| `CUserStatusEnum` | C端用户状态 | NORMAL(1), LOCKED(2), DISABLED(3) |
| `EnterpriseStatusEnum` | 企业状态 | NORMAL(1), EXPIRED(2), DISABLED(3) |
| `EnterpriseUserStatusEnum` | B端用户状态 | NORMAL(1), LOCKED(2), DISABLED(3), INACTIVE(4), RESIGNED(5) |
| `GenderEnum` | 性别 | UNKNOWN(0), MALE(1), FEMALE(2) |
| `VerificationCodeSceneEnum` | 验证码场景 | LOGIN, REGISTER, RESET |
| `VerificationCodeStatusEnum` | 验证码状态 | VALID(1), USED(2), EXPIRED(3) |
| `ThirdPartyPlatformEnum` | 第三方平台 | WECHAT, ALIPAY, QQ |
| `LockReasonEnum` | 锁定原因 | PASSWORD_ERROR, ABNORMAL_LOGIN |
| `AccountLockStatusEnum` | 锁定状态 | LOCKED(1), UNLOCKED(2) |
| `LoginStatusEnum` | 登录状态 | FAIL(0), SUCCESS(1) |
| `StatusEnum` | 通用启用/禁用 | ENABLED(1), DISABLED(0) |

### 12.4 使用规范

- **禁止** 在 Service/Controller 中直接写 `1`, `2`, `3` 表示状态，必须用 `XxxStatusEnum.NORMAL.getCode()`
- **禁止** 直接写 `"C"`, `"B"`, `"P"` 表示用户类型，必须用 `UserTypeEnum.C.getCode()`
- **禁止** 直接写 `"login"`, `"register"` 表示场景，必须用 `VerificationCodeSceneEnum.LOGIN.getCode()`
- Entity 的 `status` 字段类型保持为 `Integer`，存枚举的 `code` 值
- BO 的 `status` 字段类型保持为 `Integer`，与 Entity 一致
- **禁止** 在 Service 中定义 `public static final int STATUS_XXX = N` 常量，统一使用枚举

---

## 十三、禁止事项

1. **禁止** Controller 中用 `Map<String, String>` / `Map<String, Object>` 接收/返回数据
2. **禁止** Service 直接返回 Entity 给 Controller，必须经过 BO 转换
3. **禁止** 在 Entity 中重复定义 BaseEntity 已有的字段
4. **禁止** 使用 `@Autowired` 字段注入
5. **禁止** Service 返回 null 表示失败，应抛异常
6. **禁止** 使用 `@Transactional` 注解式事务，统一使用 `TransactionTemplate` 编程式事务
7. **禁止** Service 不继承 `BaseService` / `BaseServiceImpl`（除非是不需要 CRUD 的纯工具服务）
8. **禁止** 在 Converter 中手写转换逻辑，仅使用 MapStruct 声明式接口
9. **禁止** Service 层出现 `LambdaQueryWrapper` / `QueryWrapper` / `Wrappers`，所有查询条件必须在 Mapper 中定义
10. **禁止** 在循环中执行数据库查询（N+1 问题），必须先批量查出再 Map 分组匹配
11. **禁止** 在循环中执行远程调用（Feign），必须先批量查出再 Map 分组匹配
12. **禁止** 批量操作（insertBatch / insertOrUpdateBatch）依赖 MyBatis-Plus 自动生成，必须在 XML 中手写 SQL
13. **禁止** 在代码中使用魔法值（硬编码数字/字符串表示状态、类型等），必须使用枚举
