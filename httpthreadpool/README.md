# Core Java HTTP Web Server & Network Limits Analysis

An evolution of my raw TCP server into a fully functional HTTP/1.1 web server, built entirely without external frameworks (like Spring Boot or Tomcat). 

This project demonstrates how to manually read blocking I/O streams, parse the HTTP protocol, and serve HTML payloads over raw sockets. More importantly, it serves as a deep dive into the physical limitations of the Thread-Per-Connection model, Ephemeral Port Exhaustion, and single-machine networking ceilings.



## Core Architecture

* **HTTP Protocol Parsing:** Manually reads and buffers incoming `InputStream` byte streams, identifying the `\r\n\r\n` terminator to handle browser requests.
* **Optimized Blocking I/O (`java.io`):** Scaled the `ExecutorService` fixed thread pool to compensate for the thread-idling inherent to reading network streams.
* **Connection Lifecycle Management:** Explicitly implements `Connection: close` to aggressively terminate sockets after serving HTML. This frees up the assigned worker thread in milliseconds, maximizing concurrent user throughput.

## Performance Benchmarks
The server was benchmarked using **Apache JMeter (HTTP Request Sampler)** to map the exact performance cost of parsing HTTP headers and serving larger HTML payloads.

**The "Sweet Spot" Maximum Throughput Test:**
* **Concurrent Threads:** 120,000
* **Duration:** 60 seconds
* **Throughput:** **2,000 Requests Per Second (RPS)**
* **Average Latency:** ~2ms
* **Error Rate:** 0.01%

![JMeter HTTP Summary Report](<img width="1280" height="764" alt="Screenshot 2026-03-02 174126" src="https://github.com/user-attachments/assets/3d6eed06-ca20-4ee6-adbe-cd63687d5663" />
<img width="1280" height="764" alt="Screenshot 2026-03-02 174058" src="https://github.com/user-attachments/assets/524fa04d-d9bd-4995-be35-46e6739d06a0" />

)

## Engineering Insights & OS Bottlenecks

Pushing this server to its breaking point revealed that the highly optimized Java code vastly outpaced the operating system's local network stack.

1. **The Keep-Alive Trap & Thread Starvation:**
   Initial testing utilized `Connection: keep-alive` with a 5-second timeout. This immediately crashed the server's throughput. Because `java.io` utilizes a Thread-Per-Connection model, threads sat paralyzed waiting for idle clients to send more data. This proved firsthand why holding connections open at scale requires a transition to Non-Blocking I/O (`java.nio`) and an Event Loop architecture (e.g., Netty/Node.js).

2. **Ephemeral Port Exhaustion & `TIME_WAIT`:**
   When pushing the server to 10,000 RPS, the test yielded an 84% error rate. This was not a Java failure, but an OS limit. Windows only provides ~16,384 ephemeral (outgoing) ports. At 10,000 RPS, JMeter exhausted the entire port pool in 1.6 seconds. Because the sockets were rapidly cycling through the TCP `TIME_WAIT` state, the OS triggered **PAWS (Protection Against Wrapped Sequences)** and began actively dropping packets. 

3. **The Single-Machine Hardware Ceiling:**
   Testing revealed that increasing the thread pool from 100 to 1,000 yielded no throughput increase. At 2,000 RPS, requests were processed in an average of 2ms, meaning only ~4 physical threads were active at any given millisecond. The 2,000 RPS ceiling was strictly the limit of the OS loopback adapter (`127.0.0.1`) and JMeter's ability to allocate JVM threads. 

**Conclusion:** Scaling this architecture beyond 2,000 RPS requires abandoning localhost testing in favor of Distributed Load Testing (Master/Slave architecture) across multiple isolated network environments.

## Getting Started
1. Clone the repository.
2. Compile the server: `javac Server.java`
3. Run the server: `java Server`
4. Open a web browser and navigate to `http://localhost:8080`.

## Author
Sushanth Reddy Reguri
