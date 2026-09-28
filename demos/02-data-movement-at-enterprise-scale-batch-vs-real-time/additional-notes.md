In software architecture, **bounded datasets** have a known, finite end, while **unbounded datasets** continue to arrive indefinitely or have no predictable end.

### Bounded datasets

A bounded dataset is a fixed collection of data that can be fully read and processed.

Examples include:

- A CSV file
- A database table queried at a particular time
- Historical sales records
- A batch of uploaded images
- A log file that has already been closed

A typical bounded workflow looks like:

```text
Read all data → Process it → Produce a result → Stop
```

**Common uses**

- Batch processing
- Data warehouses and reporting
- ETL pipelines
- Machine-learning model training
- Financial reconciliation
- Imports and exports
- One-time data migrations

**Advantages**

- The system knows when processing is complete.
- It can calculate totals, averages, counts, and other global results easily.
- Processing can often be retried from the beginning.
- Results are usually deterministic and reproducible.
- It is simpler to test and debug.
- Resources can be planned more predictably.

**Disadvantages**

- Results may not be available until all data has been processed.
- Large datasets can require substantial storage and processing time.
- New data may not appear until the next batch run.
- A failure can waste work if the pipeline has poor checkpointing.
- Processing very large datasets can create spikes in CPU, memory, or network usage.

### Unbounded datasets

An unbounded dataset is an ongoing stream of data with no known final record.

Examples include:

- Web-server events
- Sensor readings
- Stock-price updates
- User activity events
- Application logs as they are generated
- IoT telemetry
- Messages from a queue or event broker

A typical unbounded workflow looks like:

```text
Receive event → Process it → Emit a result → Continue receiving events
```

**Common uses**

- Real-time monitoring
- Fraud detection
- Recommendation systems
- Alerting
- Live dashboards
- Event-driven applications
- Network and security monitoring
- Continuous analytics

**Advantages**

- Results can be produced with low latency.
- The system can react to events as they happen.
- It supports continuously changing data and live operational decisions.
- Work can be distributed across many processing nodes.
- It avoids waiting for a large batch to accumulate.
- It can support near-real-time user experiences.

**Disadvantages**

- There may be no natural point at which processing is “complete.”
- The system must handle late, duplicated, missing, or out-of-order events.
- State management becomes more complex.
- Failures and reprocessing require careful design.
- Results may change as additional events arrive.
- Capacity, backpressure, and traffic spikes must be managed.
- Retaining or replaying the stream can require significant infrastructure.

### Key architectural difference

The main distinction is not necessarily the storage format. It is whether the system can identify a **logical end** to the input.

| Characteristic | Bounded dataset | Unbounded dataset |
|---|---|---|
| Input size | Finite or known | Continually growing or unknown |
| Processing style | Batch | Streaming or continuous |
| Completion | Has a definite end | No natural end |
| Latency | Often seconds to hours | Usually milliseconds to seconds |
| Typical result | Complete historical answer | Current or incremental answer |
| Main concern | Throughput and total processing time | Latency, state, ordering, and reliability |
| Example | “Total sales for last month” | “Current sales total as orders arrive” |

### Windows in unbounded processing

Because an unbounded stream never ends, systems usually divide it into **windows**:

- **Tumbling window:** Fixed, non-overlapping periods, such as sales every five minutes.
- **Sliding window:** A continuously moving period, such as the last ten minutes, updated every minute.
- **Session window:** Groups events according to user activity, ending after a period of inactivity.

For example, “average temperature” is incomplete for an unbounded stream unless you specify a window:

```text
Average temperature over the last 10 minutes
```

Streaming systems also commonly use **event time**, meaning when an event actually occurred, rather than only processing time, meaning when the system received it. This helps handle delayed events.

### Choosing between them

Use a **bounded dataset and batch architecture** when:

- A small delay is acceptable.
- You need a complete and stable historical result.
- The data already exists as a finite collection.
- Simplicity and reproducibility are priorities.
- Processing can happen periodically.

Use an **unbounded dataset and streaming architecture** when:

- Decisions must be made quickly.
- Data arrives continuously.
- You need alerts or live updates.
- Waiting for a batch would create unacceptable delay.
- The system must react to events individually or incrementally.

Many real systems use both. For example, an application might process new orders through a streaming pipeline for real-time inventory updates, while also running bounded batch jobs overnight to recalculate historical reports and correct inconsistencies. This is often called a **lambda architecture**, or more generally a combination of batch and streaming processing.