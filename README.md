# SimpleMusic

SimpleMusic是一款简洁易用的Android音乐播放器应用，专注于提供流畅的音乐播放体验和直观的用户界面。

## 功能特点

- 🎵 **本地音乐播放**：扫描并播放设备中的本地音乐文件
- 📋 **歌单管理**：创建、编辑和管理自定义播放列表
- 👤 **用户系统**：支持用户注册、登录、找回密码等功能
- 🎼 **音乐信息展示**：显示歌曲标题、艺术家、专辑等详细信息
- ⏯️ **播放控制**：支持播放/暂停、上一曲/下一曲、进度调整等基本操作
- 🎚️ **音量调节**：提供便捷的音量控制功能
- 🌙 **深色模式**：支持系统深色模式，提供舒适的夜间使用体验

## 技术栈

- **开发语言**：Java
- **开发框架**：Android SDK
- **UI组件**：Material Design
- **数据库**：LitePal（SQLite操作框架）
- **架构组件**：AndroidX、ViewModel、LiveData
- **导航**：Android Navigation Component
- **适配器优化**：BaseRecyclerViewAdapterHelper
- **权限管理**：PermissionX
- **网络请求**：Retrofit（用于可能的网络音乐服务）

## 项目结构

```
app/
├── src/main/
│   ├── java/com/example/myapplication/
│   │   ├── MainActivity.java
│   │   └── ui/
│   │       ├── musichome/          # 音乐播放核心功能
│   │       ├── user/              # 用户管理相关功能
│   │       ├── setting/           # 应用设置
│   │       ├── developerdeclare/  # 开发者声明
│   │       ├── softversion/       # 软件版本信息
│   │       └── useragree/         # 用户协议
│   ├── res/                       # 资源文件
│   ├── assets/                    # 静态资源
│   └── AndroidManifest.xml        # 应用配置和权限声明
└── build.gradle                   # 应用级构建配置
```

## 安装说明

### 前提条件

- Android Studio 2020.3.1 或更高版本
- JDK 1.8 或更高版本
- Android SDK 30 或更高版本
- Gradle 7.4.1 或兼容版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/yourusername/SimpleMusic.git
   ```

2. **打开项目**
   - 启动Android Studio
   - 选择 "Open an existing Android Studio project"
   - 导航到项目目录并选择

3. **同步依赖**
   - Android Studio将自动检测Gradle配置
   - 点击 "Sync Now" 同步项目依赖

4. **构建并运行**
   - 连接Android设备或启动模拟器
   - 点击 "Run" 按钮构建并安装应用

## 权限说明

应用需要以下权限才能正常工作：

- **存储权限**：读取和播放本地音乐文件
- **媒体权限**：访问设备中的音频文件
- **唤醒锁定**：在后台播放音乐时保持设备唤醒
- **设置权限**：可能用于调整系统设置以优化播放体验

## 开发说明

### 数据库配置

应用使用LitePal作为ORM框架管理本地数据库。数据库配置文件位于 `assets/litepal.xml`。

### 音乐服务

音乐播放功能通过 `MusicService` 服务实现，支持在后台持续播放音乐。

### 用户界面

应用采用Fragment和Activity组合的方式构建用户界面，使用Navigation Component管理导航。

## 贡献指南

欢迎贡献代码或提出建议！请按照以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启Pull Request

## 许可证

本项目采用MIT许可证。详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系开发者：

- GitHub: [Onizian](https://github.com/yourusername)

---

感谢使用SimpleMusic！希望您享受愉快的音乐时光！🎵