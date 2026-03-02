# High-Throughput Multithreaded TCP Server (Core Java)

A foundational backend project engineered entirely from scratch using Java's core networking (`java.net`) and concurrency (`java.util.concurrent`) libraries. 

This project was built to explore the absolute physical limits of the Thread-Per-Connection architecture over TCP/IP (OSI Layer 4) before introducing the parsing overhead of application-layer protocols like HTTP. It serves as a study in raw socket communication, thread pool starvation, and operating system network queueing.

## Core Architecture

* **Raw Socket Programming:** Utilizes `ServerSocket` and `Socket` to manually establish and manage raw TCP byte streams.
* **Concurrency & Thread Pooling:** Implements an `ExecutorService` fixed thread pool to handle concurrent client requests without exhausting system CPU resources via context switching.
* **TCP Backlog Tuning:** Configured the OS-level connection backlog to `10000` to handle instant micro-bursts of traffic, successfully preventing `Connection refused` errors under extreme load.

## Performance & Stress Testing
To validate the architecture's resilience, the server was stress-tested using **Apache JMeter (TCP Sampler)**, simulating massive concurrent connection spikes.

**Test Parameters:**
* **Concurrent Threads (Users):** 500,000
* **Ramp-up Period:** 60 seconds
* **Payload:** Plain text TCP transmission

**Results:**
* **Throughput:** ~378,000 Requests Per Minute (RPM) / **~6,300 Requests Per Second (RPS)**
* **Median Latency:** 1ms
* **Average Latency:** 310ms
* **Error Rate:** 0.02% 

![JMeter TCP Graph Results](<img width="1280" height="764" alt="Screenshot 2026-03-02 193641" src="https://github.com/user-attachments/assets/dbcd3866-fd03-4823-b6d0-d5431130ead1" />
<img width="1280" height="764" alt="Screenshot 2026-03-02 193716" src="https://github.com/user-attachments/assets/05868b54-1dcd-4843-831b-4a7342974c66" />

)

## Engineering Insights
* **The Thread Pool Queue Effect:** The massive delta between the median (1ms) and average (310ms) latency perfectly illustrates the `ExecutorService`'s unbounded queue in action. During traffic spikes, the core worker threads handled the first batches instantly (5ms), while the queue successfully held hundreds of thousands of overflow requests in memory until a worker thread became available, raising the average time without dropping a single connection.

## Getting Started
1. Clone the repository.
2. Compile the server: `javac Server.java`
3. Run the server: `java Server` (Defaults to port 8080)
4. Use a raw TCP client (like Telnet, netcat, or JMeter) to connect.

## 👨‍💻 Author
Sushanth Reddy Reguri
