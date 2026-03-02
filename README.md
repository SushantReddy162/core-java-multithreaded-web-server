# High-Performance Multithreaded Web Server (Core Java)

A raw, custom-built HTTP web server engineered entirely from scratch using Java's core networking and concurrency libraries (`java.net` and `java.util.concurrent`). 

This project was developed to master the fundamental mechanics of TCP/IP, blocking I/O, and thread management before relying on high-level enterprise frameworks like Spring Boot. It demonstrates how to manually handle socket connections, parse HTTP protocols, and efficiently manage extreme traffic spikes using thread pooling.



## 🚀 Core Architecture

* **Raw Socket Programming:** Utilizes `ServerSocket` and `Socket` to manually establish and manage TCP connections.
* **Concurrency & Thread Pooling:** Implements an `ExecutorService` fixed thread pool (100 concurrent workers) to handle multiple client requests simultaneously without exhausting system CPU resources via context switching.
* **Unbounded Task Queueing:** Leverages the Executor's internal queue to safely hold excess incoming requests when the worker pool is saturated.
* **TCP Backlog Tuning:** Configured the OS-level connection backlog to handle instant micro-bursts of traffic, preventing `Connection Refused` errors under heavy load.

##Performance & Stress Testing

To validate the architecture's resilience, the server was stress-tested using **Apache JMeter**, simulating thousands of concurrent users. 

**Test Parameters:**
* **Concurrent Threads (Users):** 6,000
* **Ramp-up Period:** 60 seconds
* **Payload:** Plain text HTTP GET request

**Results:**
* **Throughput:** ~482,000 Requests Per Minute (RPM) / **~8,000 Requests Per Second (RPS)**
* **Median Latency:** 5ms
* **Average Latency:** 249ms
* **Error Rate:** 0.00% 

The significant delta between the median (5ms) and average (249ms) latency perfectly illustrates the thread-pool queueing mechanism successfully holding requests in memory during traffic spikes until a worker thread becomes available.

## 🛠️ Tech Stack
* **Language:** Java (Core)
* **Libraries:** `java.net`, `java.io`, `java.util.concurrent`
* **Testing:** Apache JMeter (TCP Sampler)

##Getting Started

1. Clone the repository.
2. Compile the server: `javac Server.java`
3. Run the server: `java Server`
4. The server will start listening on port `8080`.
5. Open a web browser and navigate to `http://localhost:8080` to see the response.

##Author
**Sushanth Reddy Reguri**
