# 📘 Attendance Management System  

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)  
![Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=coffeescript&logoColor=white)  

---

## 📖 Overview
The **Attendance Management System** is a **Java-based desktop application** that helps educational institutions efficiently manage **student records and attendance**.  

It provides:  
✅ User-friendly **GUI (Java Swing)**  
✅ **Secure login system**  
✅ **Student record management**  
✅ **Attendance tracking & reporting**  
✅ **Data export (CSV, Excel)**  

---

## 🏗 System Architecture
The application follows a **modular architecture**:


```text
┌─────────────────┐
│   Login System  │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  Base Frame     │  ← Provides common UI components and navigation
└─────────────────┘
         │
         ├─────────────────┬─────────────────┬─────────────────┐
         ▼                 ▼                 ▼                 ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Dashboard      │ │ Student Mgmt    │ │ Attendance      │ │ Reports         │
└─────────────────┘ └─────────────────┘ └─────────────────┘ └─────────────────┘
         │
         ▼
┌─────────────────┐
│   Data Export   │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  MySQL Database │
└─────────────────┘
```
---

# ✨ Key Features
### 🔐 User Authentication
- Secure login system (database-backed)  
- Role-based access (Admin role)  

### 🖥 Dashboard
- Displays logged-in user info  
- Central navigation hub  

### 👨‍🎓 Student Management
- Full CRUD (Create, Read, Update, Delete)  
- Search and filter students  
- Input validation & duplicate prevention  

### 🗓 Attendance Tracking
- Status options: **Present, Absent, Late, Excused**  
- Course/date-based filtering  
- Bulk operations (Select All, Clear All)  
- Duplicate prevention for same day  

### 📊 Reporting
- Daily, weekly, monthly summaries  
- Course-based analytics  
- Attendance percentages  
- Printable reports  

### 📤 Data Export
- Export **students & attendance**  
- Formats: **CSV, Excel**  

### 🗄 Database Management
- Auto database initialization  
- Default admin account  
- Proper foreign key relationships  

---

## ⚙️ Technical Specifications

### 🖥 Technologies
- **Frontend:** Java Swing  
- **Backend:** Java JDBC  
- **Database:** MySQL  
- **Build Tool:** Standard Java Project  

### 🗃 Database Schema
- **students** → Student personal info  
- **attendance** → Attendance records (linked to students)  
- **users** → Authentication & roles  

---

## 🚀 Getting Started

### 🔧 Prerequisites
- Install **Java JDK 8+**  
- Install **MySQL XAMPP**  
- Install an IDE: IntelliJ IDEA / Eclipse / NetBeans 

<div align="center"> <img src="https://github.com/LikeNmuFF/muffy/blob/main/muffy.png" alt="Muffy Logo" /> <br> <em>Attendance Management System </em> </div>