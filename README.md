# Calculator in the Cloud 

**Computer Networking HW1 – Java Socket Calculator**

A client-server calculator application built with Java TCP sockets that demonstrates socket communication, multithreaded server design, and custom application-layer protocol implementation.

[![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://www.java.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Protocol Specification](#protocol-specification)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Screenshots](#screenshots)
- [Requirements](#requirements)
- [Author](#author)

## 🎯 Overview

This project implements an Internet calculator where:
- **Client** sends arithmetic expressions to the server
- **Server** interprets the expression, performs calculations, and sends results back
- Communication uses a custom **ASCII-based protocol** inspired by HTTP

## ✨ Features

### Core Functionality
- ✅ **Four Basic Operations**: Addition, Subtraction, Multiplication, Division
- ✅ **Multi-client Support**: ThreadPool (20 threads) handles concurrent connections
- ✅ **Custom Protocol**: HTTP-inspired request/response format with status codes
- ✅ **Error Handling**: Comprehensive error detection and reporting
- ✅ **Configuration File**: Client loads server info from `server_info.dat`
- ✅ **Graceful Shutdown**: QUIT command for clean disconnection

### Error Detection
- Division by zero
- Invalid number format
- Too many/few arguments
- Invalid command

## 🏗️ Architecture

```
┌─────────────────────┐                    ┌─────────────────────┐
│   ServerEx (Main)   │                    │   ClientEx (Main)   │
├─────────────────────┤                    ├─────────────────────┤
│ - PORT: 9999        │                    │ - Socket            │
│ - ThreadPool (20)   │◄──TCP Socket──────►│ - Scanner           │
│ - ServerSocket      │    Connection      │ - displayResponse() │
└──────────┬──────────┘                    └──────────┬──────────┘
           │ creates                                  │ uses
           ▼                                          ▼
┌─────────────────────┐                    ┌─────────────────────┐
│ ClientHandler       │                    │   ServerConfig      │
│ (Runnable)          │                    ├─────────────────────┤
├─────────────────────┤                    │ - host: String      │
│ - processRequest()  │                    │ - port: int         │
│ - calculate()       │                    │ - loadConfig()      │
└─────────────────────┘                    └──────────┬──────────┘
                                                      │ reads
                                                      ▼
                                            ┌─────────────────────┐
                                            │  server_info.dat    │
                                            ├─────────────────────┤
                                            │ host=localhost      │
                                            │ port=9999           │
                                            └─────────────────────┘
```

## 📡 Protocol Specification

### Request Format
```
<COMMAND> <OPERAND1> <OPERAND2>
```

**Commands:**
- `ADD` - Addition
- `SUB` - Subtraction
- `MUL` - Multiplication
- `DIV` - Division
- `QUIT` - Disconnect

**Example:**
```
ADD 10 20
DIV 100 4
QUIT
```

### Response Format
```
<RESPONSE_CODE> <DATA>
```

**Response Codes:**
- `200` - Success (followed by result)
- `400` - Error (followed by error type)
- `BYE` - Disconnect acknowledgment

**Examples:**
```
200 30          (Success: 10 + 20 = 30)
400 Divided_by_zero
BYE
```

### Error Types
- `Divided_by_zero` - Division by zero attempted
- `Too_many_arguments` - More than 2 operands
- `Too_few_arguments` - Less than 2 operands
- `INVALID` - Invalid command or number format

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Text editor or IDE (Eclipse, IntelliJ IDEA, VS Code)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/VictorKim00/calculator_in_the_cloud.git
cd calculator_in_the_cloud
```

2. **Create server configuration file** (optional)

Create `server_info.dat` in the project root:
```
host=localhost
port=9999
```

If this file is missing, the client will use default values (localhost:9999).

3. **Compile the source code**
```bash
javac myfirstnetworkapp/*.java
```

## 💻 Usage

### Running the Server

```bash
java myfirstnetworkapp.ServerEx
```

Output:
```
=================================
Calculator Server Started
Port: 9999
ThreadPool Size: 20
=================================

Waiting for client...
```

### Running the Client

```bash
java myfirstnetworkapp.ClientEx
```

Output:
```
=================================
Calculator Client
Server: localhost:9999
=================================

Connected to Server!
Commands: ADD, SUB, MUL, DIV, QUIT
Example: ADD 10 20

>>
```

### Example Session

```
>> ADD 10 20
Result: 30

>> MUL 7 8
Result: 56

>> DIV 25 0
Error: Cannot divide by zero 

>> MUL 5 2 1
Error: Too many arguments 

>> QUIT
Server: BYE
Connection closed
```

## 📁 Project Structure

```
calculator_in_the_cloud/
├── myfirstnetworkapp/
│   ├── ServerEx.java          # Server main class
│   ├── ClientEx.java          # Client main class
│   └── (compiled .class files)
├── server_info.dat            # Server configuration (optional)
├── README.md                  # This file
└── LICENSE                    # MIT License
```

### Key Classes

#### ServerEx.java
- Main server application
- Creates ServerSocket on port 9999
- Manages ThreadPool for concurrent client handling
- Contains `ClientHandler` class (implements Runnable)

#### ClientEx.java
- Main client application
- Connects to server using Socket
- Handles user input and displays responses
- Contains `ServerConfig` class for configuration loading

## 📸 Screenshots

### Normal Calculation
```
>> ADD 10 20
Result: 30
```

### Error Cases
```
>> DIV 10 0
Error: Cannot divide by zero

>> MUL 1 2 3
Error: Too many arguments

>> XYZ 10 20
Error: Invalid command
```

## 📋 Requirements

### Assignment Requirements
- ✅ Implement four basic arithmetic operations (ADD, SUB, MUL, DIV)
- ✅ Server handles multiple clients using ThreadPool & Runnable interface
- ✅ Define application-layer protocol with semantic codes
- ✅ Server reads configuration from `server_info.dat` (with defaults)
- ✅ Proper error handling and response codes

### Technical Requirements
- Java 8+
- TCP Socket API
- Multi-threading (ExecutorService)
- BufferedReader/BufferedWriter for I/O
- StringTokenizer for parsing

## 🎓 Learning Outcomes

Through this project, I learned:
- **Socket Programming**: Understanding TCP client-server communication
- **Concurrency**: Managing multiple clients with ThreadPool
- **Protocol Design**: Defining custom application-layer protocols
- **Error Handling**: Comprehensive exception management
- **Network Programming**: Practical experience with Java networking APIs

## 👨‍💻 Author

**Kim Yushin (김유신)**
- GitHub: [@VictorKim00](https://github.com/VictorKim00)
- Project: Computer Networking HW1
- Date: November 2025

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Computer Networking Course
- Java Socket Programming Documentation
- HTTP Protocol Specification (for inspiration)

  
### 🤖 AI Assistance Disclosure
Some parts of this project (such as error-handling logic and thread management) were reviewed and refined
with assistance from **Anthropic Claude**.  
All final implementations were written, tested, and verified by the author.  
This disclosure is made for transparency.
---

**⭐ If you found this project helpful, please consider giving it a star!**
