# player-messaging

> Repository: [github.com/lakshmipriyav/player-messaging](https://github.com/lakshmipriyav/player-messaging.git)

A Java Use-case about two players talking to each other. One starts the conversation, the other replies, and they keep going for exactly 10 rounds before stopping gracefully.

---

## About the Project

The core idea is straightforward: two `Player` instances exchange messages back and forth. Every time a player receives a message, it replies with the received text plus its own send counter stuck at the end. After 10 full round-trips the initiator says goodbye and both players stop.

The Use-case is built in two modes 

### The Two Modes

**Mode A — Same Process**
Both players run inside the same JVM on separate threads. They talk through a `LinkedBlockingQueue` — fast, simple, no network involved.

**Mode B — Separate Processes**
Each player gets its own `java` process (its own PID). They talk over a TCP socket on `localhost:9090`. Same `Player` code, completely different transport underneath.

### A sample Conversation Looks Like

The message format confirmed with the team is `receivedMessage + "" + counter`:

```
Player1  →  "Hello"
Player2  →  "Hello1"
Player1  →  "Hello11"
Player2  →  "Hello112"
Player1  →  "Hello1121"
           ... 10 rounds total ...
```

The string grows with every touch — each player appends their own counter to whatever they received.

### A Note on Design

The key decision is the `MessageChannel` interface. `Player` never knows whether it's talking through a queue or a socket — it just calls `send()` and `receive()`. Swapping transport means swapping the implementation behind that interface. Nothing else changes.

`PlayerRole` (`INITIATOR` / `RESPONDER`) — it is enforced at runtime. Calling `run()` on an initiator or `initiateConversation()` on a responder throws immediately. Mistakes fail loudly, not silently.

---
## How to Run

### What You Need

- Java 21
- Maven 3.9+ — or just use `./mvnw` (the Maven wrapper is included, no install needed)

### Build Once

```bash
./mvnw clean package -DskipTests
```

This produces `target/player-messaging.jar`.

---
### Mode A — Both Players, One JVM

The quickest way to see everything working:

```bash
./scripts/run-in-process.sh
```

Or run the JAR directly:

```bash
java -jar target/player-messaging.jar
```

You should see something like this:

```
>>> Running IN-PROCESS mode (same JVM, two threads)

=== Transport : IN_PROCESS
    Threads   : 2  (main + responder-thread)
    Rounds    : 10
    PID       : 16927

[Player1][INITIATOR] ready.
[Player2][RESPONDER] ready.
[Player1] sent  (round  1): "Hello"

[Player2] received : "Hello"
[Player2] sent : "Hello1"
[Player1] received  (round  1): "Hello1"

[Player1] sent  (round  2): "Hello11"
...
[Player1] completed 10 round-trips.
[Player2] shutting down.
```
---

### Mode B — Each Player in Its Own Process

The script handles everything — it starts the responder in the background, waits a moment, then starts the initiator:

```bash
./scripts/run-multi-process.sh
```

If you want to run them manually (e.g. to watch both outputs at the same time), open two terminals:

```bash
# Terminal 1 — start this one first
java -cp target/player-messaging.jar \com.players.multiprocess.ResponderMain

# Terminal 2 — start after the responder is ready
java -cp target/player-messaging.jar \com.players.multiprocess.InitiatorMain

```

To use a different port (default is `9090`):

```bash
./scripts/run-multi-process.sh 9191
```
---

### Tests

```bash
# Everything
./mvnw clean test

```

---
