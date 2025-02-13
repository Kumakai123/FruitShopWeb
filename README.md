# **XiangAnFruit_Server**

祥安水果記帳系統

## **專案大綱**

本專案是一個基於 **Spring Boot** 的水果記帳系統，旨在提供方便的記錄、管理和查詢水果的購買和消費明細。通過 **REST API** 
提供後端服務，並支援資料的安全存取和高效操作。未來將持續擴展功能，提升系統的易用性和安全性。

## **我做到了什麼**

1. **RESTful API**
    - 隨著前後端分離開發成為趨勢，利用 Spring MVC 技術實現 RESTful API，以提升開發效率與系統維護性。
2. **資料庫功能**
    - 使用 **MySQL** 作為資料庫，並使用 **Criteria API** 作為查詢語言，實現包含分頁查詢在內的基本 CRUD 功能。
3. **Docker 容器化**
    - 使用 **Docker** 部署應用，整合 MySQL 容器，實現快速部署和測試。

## **未來規劃**

1. **Swagger 套件**
    - **目的：**
        - 提供 REST API 的可視化文件，方便開發者快速測試和理解 API。
    - **計畫：**
        - 引入 **Swagger UI**，生成自動化的 API 文檔。

2. **Spring Security**
    - **目的：**
        - 實現用戶認證和授權（Authentication & Authorization）。
        - 提升系統的數據安全性。
    - **功能：**
        - 支持用戶角色管理（如普通用戶和管理員）。
        - 整合 **JWT（JSON Web Token）** 或 **OAuth2** 用於 API 認證。

3. **AOP**
    - **目的：**
        - 簡化程式碼管理，統一處理日誌記錄、異常處理和性能監控。
    - **應用場景：**
        - 記錄所有 API 請求的日誌。
        - 捕獲全局異常，回傳清楚的錯誤資訊。

4. **CORS（跨域）處理**
    - **目的：**
        - 支持跨來源請求，允許前端應用與後端服務交互。
    - **實作：**
        - 配置 **Spring** 的 CORS 規則，允許合法來源的請求。

5. **單元測試**
    - **目的：**
        - 確保程式穩定，避免功能迭代導致的潛在問題。
    - **工具與框架：**
        - 使用 **JUnit 5** 進行單元測試。
        - 使用 **Mockito** 模擬服務與依賴。
        - 測試範圍包括控制層（Controller）、服務層（Service）和數據層（Repository）。

## **環境**

- **開發與運行環境：** JDK 21
- **資料庫：** MySQL 8.0
- **框架：** Spring Boot 3.2.5
- **容器化技術：** Docker

## **專案目錄結構**

```plaintext
src
├── main
│   ├── java
│   │   └── org.xiangan.fruitshopweb
│   │       ├── controller       # 控制層（處理 API 請求）
│   │       ├── entity           # 資料庫實體類
│   │       ├── repository       # 資料層（資料庫操作）
│   │       ├── service          # 服務層（業務邏輯）
│   │       └── FruitShopWebApplication.java  # 主程序入口
│   └── resources
│       ├── application.properties  # 配置文件
│       └── static                 # 靜態資源（未來用於前端整合）
└── test
    ├── java                       # 測試代碼目錄
```

## TODO

1. add the "person" table.
2. Optimize the foreign key relationship between the "consignor" and "person" tables.
3. 使用排程計算營運狀況(視情況再加入Line Message API 通知手機功能)
