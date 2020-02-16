虚拟主播目录数据
======

## 用途

为应用内添加订阅界面提供虚拟主播目录，方便用户寻找想看的虚拟主播。

## 数据来源

目录数据目前为人工采集，采集目标为在哔哩哔哩拥有官方直播账号的虚拟主播，不限语言地区，非官方认证的转播帐号不予收集。

采集到的虚拟主播以企业/团体分类，个人势的虚拟主播会一并分到一个名为 “个人势” 的团体。

## 数据接口

### 团体目录

**API 路径**：`/room/vtubers_catalog.json`

**返回值**：参考 https://danmaqua-cn.api.feng.moe/room/vtubers_catalog.json

### 团体内主播列表

**API 路径**：`/room/vtubers/[org_name].json`

**`org_name` 参数**：在团体目录中获取到的团体的 `name` 值（仅由小写英文数字和下划线构成）

**返回值**：参考 https://danmaqua-cn.api.feng.moe/room/vtubers/hololive.json
