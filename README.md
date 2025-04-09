# **XiangAnFruit_Server**
祥安水果營運管理系統

## **專案大綱**

本專案是一個基於 **Spring Boot** 的水果記帳系統，旨在提供方便的記錄、管理和查詢水果的購買和消費明細。通過 **REST API** 
提供後端服務，並支援資料的安全存取和高效操作。未來將持續擴展功能，提升系統的易用性和安全性。 </p>
Swagger Demo:http://localhost:8000/swagger-ui/index.html

## **我做到了什麼**

1. **RESTful API**
    - 隨著前後端分離開發成為趨勢，利用 Spring MVC 技術實現 RESTful API，以提升開發效率與系統維護性。
2. **資料庫功能**
    - 使用 **MySQL** 作為資料庫，並使用 **Criteria API** 進行條件查詢，實現包含分頁查詢在內的基本 CRUD 功能。
3. **Spring Security**
    - **用戶認證與授權**（Authentication & Authorization），確保 API 只有合法用戶能夠存取。
    - **角色管理**（如普通用戶與管理員），根據不同權限設定 API 存取規則。
    - 整合 **JWT（JSON Web Token）** 進行 API 認證，實現無狀態身份驗證，確保請求的安全性。
4. **全域異常處理**
   - 建立集中式例外處理機制，透過 `@ControllerAdvice` 攔截應用程式中可能發生的錯誤（如找不到資源、重複鍵、參數格式錯誤等），提升 API 回應一致性與系統健壯性。
   - 客製化回應格式 `ErrorResponse`，統一回傳結構化錯誤資訊（狀態碼、標題、訊息、路徑），方便前端處理錯誤顯示與日後除錯。
   - 支援輸入驗證錯誤（`HandlerMethodValidationException`）、查無資料（`NoSuchElementException`）與資料重複（`DuplicateKeyException`）等常見場景，讓 API 更具備容錯能力。
   - 所有錯誤皆紀錄詳細 log，方便在日誌中追蹤與調查問題來源。
5. **Docker 容器化**
    - 使用 **Docker** 部署應用，整合 MySQL 容器，實現快速部署和測試。
6. **Swagger 套件**
    - 整合 Swagger UI，直覺式 API 文件與即時測試，提升開發效率與前後端協作。

## **未來規劃**

1. **AOP**
    - **目的：**
        - 簡化程式碼管理，統一處理日誌記錄、異常處理和性能監控。
    - **應用場景：**
        - 記錄所有 API 請求的日誌。
        - 捕獲全局異常，回傳清楚的錯誤資訊。

2. **CORS（跨域）處理**
    - **目的：**
        - 支持跨來源請求，允許前端應用與後端服務交互。
    - **實作：**
        - 配置 **Spring** 的 CORS 規則，允許合法來源的請求。

3. **單元測試**
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
│   │       ├── configuration          # 設定相關（如 Swagger、Security）
│   │       ├── controller             # 控制層（處理 API 請求）
│   │       ├── entity                 # 資料庫實體類
│   │       ├── enumType               # 列舉型別（定義固定值）
│   │       ├── exception              # 自定義例外處理
│   │       ├── filter                 # 過濾器（如 JWT 驗證等）
│   │       ├── model                  # 請求與回應模型（DTO、VO）
│   │       ├── repository             # 資料層（資料庫操作）
│   │       ├── service                # 服務層（業務邏輯）
│   │       └── FruitShopWebApplication.java  # 主程序入口
│   └── resources
│       └── application.properties     # 配置文件
└── test
    └── java
        └── org.xiangan.fruitshopweb   # 單元測試與整合測試
```

