# 第一階段：構建應用程式
# 使用帶有 JDK 的基底映像來進行應用構建
FROM eclipse-temurin:21-jdk AS builder

# 設置工作目錄為 /api，所有後續操作都將基於此目錄執行
WORKDIR /api

# 複製 Maven 配置文件並下載依賴（優化構建緩存）
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:resolve

# 複製其餘專案文件
COPY . .

# 執行 Maven 指令來清理舊文件並重新打包應用程式
RUN ./mvnw clean package -DskipTests

# 第二階段：運行應用程式
# 使用輕量級的基底映像（JRE）作為運行時環境，減少映像大小
FROM eclipse-temurin:21-jre-alpine

# 設置工作目錄為 /api，方便應用執行時管理文件
WORKDIR /api

# 從構建階段中複製已經打包好的 JAR 文件到運行環境
COPY --from=builder /api/target/fruitShopWeb-0.0.1.jar api.jar

# 外部訪問應用 8080 埠
EXPOSE 8080

# 容器啟動時執行的命令，這裡啟動 Spring Boot 應用
CMD ["java", "-jar", "api.jar"]
