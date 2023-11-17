package os.lab2;
// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import os.lab2.util.Process;
import os.lab2.util.Results;

import java.io.*;
import java.util.*;

public class Scheduling {

  private static int runtime = 100;
  private static List<Process> processes = new ArrayList<>();
  private static final String RESULTS_FILE = "out/Summary-Results.txt";
  private static final String PROCESSES_FILE = "out/Summary-Processes.txt";
  private static final String INIT_FILE = "scheduling.conf";

  private static void init() {
    File f = new File(Scheduling.INIT_FILE);
    String line;

    try {   
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
      while ((line = in.readLine()) != null) {
        if (line.startsWith("process")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processes.add(new Process(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
        }
        else if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          runtime = Integer.parseInt(st.nextToken());
        }
      }
      in.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    int i;

    // Hardcoded program arguments for simplicity

    System.out.println("Working...");
    init();

    Results results = SchedulingAlgorithm.run(runtime, processes, PROCESSES_FILE);
    try {
      //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
      PrintStream out = new PrintStream(new FileOutputStream(RESULTS_FILE));
      out.println("Scheduling Type: " + results.schedulingType);
      out.println("Scheduling Name: " + results.schedulingName);
      out.println("Simulation Run Time: " + results.computationTime);
      out.printf("Percentage of downtime: %.1f%s\n", Math.round(results.downtime * 1d / runtime * 1_000) / 10.0, "%");
      out.println("Process #\t  CPU use time\tExecuted");
      for (i = 1; i < processes.size() + 1; i++) {
        Process process = processes.get(i - 1);
        out.print(i);
        if (i < 100) { out.print("\t\t"); } else { out.print("\t"); }
        if (process.CPUUseTime < 100) { out.print("\t\t"); } else { out.print("\t"); }
        out.print(process.CPUUseTime);
        if (process.executed < 100) { out.print(" (t.u.)\t\t"); } else { out.print(" (t.u.)\t"); }
        out.println(process.executed);
      }
      out.flush();
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
  System.out.println("Completed.");
  }
}

