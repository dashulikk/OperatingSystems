package os.lab2;
// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import os.lab2.util.Process;
import os.lab2.util.Results;
import os.lab2.util.State;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Optional;

public class SchedulingAlgorithm {

  public static Results run(int runtime, List<Process> processes, String processesFile) {
    int downtime = 0;

    try (PrintWriter out = new PrintWriter(new FileOutputStream(processesFile))) {
        out.print("""
                R  - process is ready
                E  - process is executing
                D  - process is done
                """);
        List<StringBuilder> lines = new ArrayList<>(processes.size());
        for (Process process : processes)
            lines.add(new StringBuilder("P").append("(")
                    .append(process.executionTime).append(',')
                    .append(process.deadline).append(',')
                    .append(process.period).append(") \t"));

        updateProcesses(processes, 0);
        for (int i = 0; i < runtime; i++) {
            Optional<Process> optionalProcess = processes.stream().filter(p -> p.available).findAny();
            if (optionalProcess.isEmpty()) {
                ++downtime;
                lines.forEach(sb -> sb.append(State.DONE));
                updateProcesses(processes, i + 1);
            }
            else {
                Process process = optionalProcess.get();
                int idx = 0;
                // choose process with the earliest deadline
                for (int j = 1; j < processes.size(); j++) {
                    Process p = processes.get(j);
                    if (p.available && process.deadline * (process.executed + 1) >= p.deadline * (p.executed + 1)) {
                        process = p;
                        idx = j;
                    }
                }
                process.available = false;
                boolean isBreak = false;
                for (int j = 0; j < process.executionTime; j++) {
                    for (int k = 0; k < lines.size(); k++) {
                        if (k == idx)
                            lines.get(k).append(State.EXECUTING);
                        else
                            lines.get(k).append(processes.get(k).available ? State.READY : State.DONE);
                    }
                    if (++i == runtime) {
                        isBreak = true;
                        break;
                    }
                    updateProcesses(processes, i);
                    ++process.CPUUseTime;
                }
                --i;
                process.executed++;
                if (isBreak) break;
            }
        }
        lines.forEach(out::println);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new Results("Pre-emptive", "Earliest deadline first", runtime, downtime);
  }

  private static void updateProcesses(List<Process> processes, int i) {
      for (int j = 0; j < processes.size(); j++) {
          Process process = processes.get(j);
          int b = i % process.period;
          if (b == 0) {
              // the next period of the process started
              if (process.available)
                  throw new RuntimeException("Deadline of process " + j + " is missed");
              // thus the process if available for execution
              process.available = true;
          } else if (process.available && b > process.deadline) {
              // if the deadline of process if missed
              throw new RuntimeException("Deadline of process " + j + " is missed");
          }
      }
  }
}
