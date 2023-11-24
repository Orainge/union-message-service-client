# 统一推送通知服务客户端

## 1 客户端界面

### 1.1 Windows 系统

![MacOS 客户端界面](https://cdn.jsdelivr.net/gh/Orainge/union-message-service-client@master/pic/pic2.png)

### 1.2 MacOS 系统

![MacOS 客户端界面](https://cdn.jsdelivr.net/gh/Orainge/union-message-service-client@master/pic/pic1.png)

## 2 系统介绍

本项目为统一推送通知服务的客户端，实现即时的通知显示。

## 3 运行平台

- 已经支持且测试通过的平台：Windows、MacOS
- 已经支持但未测试的平台：Linux

## 4 技术介绍

- 客户端采用 Java 编写，最低 JRE版本为 1.8，
- 拥有基本的GUI界面，实现对通知内容的管理

- 通信协议使用 Websocket，基于 [Orainge/websocket-forward](https://github.com/Orainge/websocket-forward) 开发，可以解决客户端无公网IP的问题。
- 采用CS架构，客户端可以个性化开发，支持其它平台的客户端的二次开发，也可以采用其他语言进行编写，只要Client连接Server时提交必要的验证信息即可。
- 图形化界面采用 Java GUI 技术，实现跨平台使用，字体采用 [OPPO Sans](https://www.coloros.com/index/newsDetail?id=72)

### 4.1 通知使用技术

- Windows：默认使用 Java GUI
- MacOS: 默认通知使用第三方组件[terminal-notifier](https://github.com/julienXX/terminal-notifier)，也可使用[macos-alert](https://gitee.com/xiaozhuai/macos-alert)，可以根据需要进行修改。

- Linux: 默认使用 Java GUI

### 4.2 系统网络架构图

![系统网络架构图](https://cdn.jsdelivr.net/gh/Orainge/union-message-service-client@master/pic/pic3.png)

## 5 二次开发

### 5.1 项目依赖

本项目依赖 [websocket-forward-utils](https://github.com/Orainge/websocket-forward)，因此如果运行项目时提示找不到相应类时，需要手动安装依赖到本地 Maven 仓库。

```sh
cd /path/to/project # 进入项目目录
cd websocket-forward-utils # 进入工具包目录

# 以下安装方式二选一
# 安装到本地 Maven 仓库（同时安装源码）
mvn source:jar install 
# 安装到本地 Maven 仓库（不安装源码）
mvn install
```

### 5.2 macOS 通知

#### 5.2.1 浮窗通知

浮窗通知使用第三方组件[terminal-notifier](https://github.com/julienXX/terminal-notifier)

```shell
terminal-notifier -title "通知标题" -subtitle "副标题" -message "通知内容" -contentImage http://png地址.png
```

#### 5.2.2 弹窗通知

- 弹窗通知默认使用使用系统命令`osascript`

```shell
osascript -e 'display dialog "通知内容" buttons {"确定"} default button 1 with title "标题"'
```

- 也可以使用第三方组件[macos-alert](https://gitee.com/xiaozhuai/macos-alert/)，可在`com.orainge.union_message_service.client.util.macos.MacOSAlertUtil`修改
- 配置YML在 `client-config.yml` ，文件如下如下

```yaml
# 不同操作系统下的配置
operation-system-config:
  # MacOS 系统配置
  macos:
    # macos-alert 配置（目前使用，预留）
    macos-alert:
      # ICON 文件夹路径 (默认为程序文件夹下的 icon)
      icon-folder-path: ""
      # macos-alert 执行文件路径 (默认为 /usr/local/bin/alert)
      exec-path: "/usr/local/bin/alert"
```

## 6 其它参考资料

- [Apprise](https://github.com/caronc/apprise)

- [terminal-notifier](https://github.com/julienXX/terminal-notifier)
- [Notify_windows](https://github.com/caronc/apprise/wiki/Notify_windows)
