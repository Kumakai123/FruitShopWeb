FROM eclipse-temurin:21-jre

# 作者資訊（可選）
LABEL authors="kyle.xiao"

# 設定容器內的工作目錄
WORKDIR /app

# 複製應用程式的 JAR 檔案到容器內
COPY target/fruitShopWeb-0.0.1.jar app.jar

# 暴露應用執行的埠，假設你的應用在 8000 埠運行
EXPOSE 8000

# 設定容器啟動時的命令
CMD ["java", "-jar", "app.jar"]