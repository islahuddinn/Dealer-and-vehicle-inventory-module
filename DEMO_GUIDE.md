# Project Presentation & Demo Guide

This guide covers everything you need to prepare for a deep-dive presentation of the **Dealer & Vehicle Inventory Module**.

---

## 1. Project Folder Structure Overview

Understanding the layout is key to explaining the project's modular architecture.

| Folder / File | Description |
|:--- |:--- |
| **`src/`** | **The Source Code.** Contains the main logic (`main/java`), configuration (`main/resources`), and automated tests (`test/java`). |
| **`target/`** | **Build Output.** Created by Maven when you run `mvn install`. It contains compiled `.class` files and the final runnable `.jar` file. |
| **`.mvn/`** | **Maven Wrapper Config.** Contains the JAR and properties that allow the project to run Maven without you having to install it globally. |
| **`scripts/`** | **Demo & Utility Scripts.** Contains helper scripts like `run-demo.ps1` to showcase core features quickly. |
| **`mvnw.cmd`** | **Maven Wrapper (Windows).** The executable you use to run Maven commands (e.g., `.\mvnw.cmd clean install`). |
| **`mvnw`** | **Maven Wrapper (Linux/macOS).** The shell script version of `mvnw.cmd`. |
| **`pom.xml`** | **Project Object Model.** The single source of truth for dependencies, build plugins, and project metadata (version, name, etc.). |
| **`README.md`** | **Quick Start Guide.** High-level overview, run instructions, and API summaries. |

---

## 2. Multi-Tenancy & Security Deep Dive

### The Role of UUIDs
*   **`X-Tenant-Id`**: In this system, a UUID represents a **Tenant** (e.g., a specific dealer group or organization). It is **NOT** a user ID or a super-admin ID.
*   **Isolation**: Every dealer and vehicle is tied to a `tenant_id`. The system uses a `TenantContextFilter` to extract this UUID from the request header and ensure users only see data belonging to their own tenant.

### Authentication vs. Multi-tenancy
1.  **Authentication (Who are you?)**: Handled via HTTP Basic Auth.
    *   `tenant_user`: Regular user who operates within a specific tenant.
    *   `global_admin`: A system-wide admin who can see cross-tenant analytics.
2.  **Multi-tenancy (Which organization are you acting for?)**: Handled via the `X-Tenant-Id` header.

---

## 3. Core Business Rules (New!)

I have implemented additional safety and validation features:
*   **Email Uniqueness**: The system now prevents creating two dealers with the same email address globally. This is enforced at both the **Service Layer** (with a friendly error message) and the **Database Layer** (via a unique constraint).
*   **Spring Boot 3 compatibility**: All API endpoints have been refined to use explicit parameter naming, ensuring compatibility with the latest Spring reflection requirements.

---

## 4. Demonstration Script

To present the project effectively, follow these steps:

### Step A: Preparation
1.  **Build the project**: 
    ```powershell
    .\mvnw.cmd clean install
    ```
2.  **Start the application**:
    ```powershell
    .\mvnw.cmd spring-boot:run
    ```

### Step B: Feature Walkthrough
1.  **Show the Swagger UI**: Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).
2.  **Demonstrate Multi-tenancy**: 
    *   Create a dealer for **Tenant A**.
    *   Try to fetch it using **Tenant B**'s ID → Observe the **403 Forbidden** or **404 Not Found** isolation.
3.  **Show Email Uniqueness**:
    *   Attempt to create a second dealer with the same email → Observe the **400 Bad Request** with message: `"Dealer with this email already exists"`.
4.  **Admin Overview**:
    *   Login as `global_admin` and call `GET /admin/dealers/countBySubscription` to show system-wide analytics.

---

## 5. Technical Presentation Tips
*   **Architecture**: Highlight the **Modular Monolith** approach. We use vertical slicing (`dealer`, `vehicle`, `admin`) to keep code maintainable.
*   **Clean Layers**: Mention the flow: `Controller` (API) → `ApplicationService` (Logic) → `Repository` (Data).
*   **Resilience**: Point out the `ApiExceptionHandler` which ensures all errors (400, 403, 404) return consistent, readable JSON.
