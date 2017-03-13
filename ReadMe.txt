PADprof - ReadMe

PADprof is designed to automatically detect Performance Anti-Patterns in the profiler data of YourKit. 
The supported Performance Anti-Patterns are so far Circuit Treasure Hunt, Extensive Processing and Wrong Cache Strategy.

For the analysis the following exportable YourKit files are needed:

Circuit Treasure Hunt:
- CPU-hot-spots.xml
- Call-tree--by-thread.xml
- Chart--CPU-time.csv

Extensive Processing:
- CPU-hot-spots.xml
- Call-tree-–-All-threads-together.xml
- Monitor-usage-statistics.xml

Wrong Cache Strategy:
- CPU-hot-spots.xml
- Call-tree-–-All-threads-together.xml
- Chart--CPU-time.csv
- Chart--Heap-Memory.csv
 
The files can be exported directly in the GUI of YourKit or by command line as shown here: https://www.yourkit.com/docs/java/help/export.jsp

The analysis is comparing a problem snapshot to other (older) snapshots in order to detect the anti-pattern. Therefore, at least exports from two snapshots are required.

Step-by-Step guide:

1. Export the required files with YourKit
2. First select the Hotspot file, then all the files from the problem snapshot, lastly all the comparison files. (multiple export from different snapshots can be selected in the comparison section)
3. Select which analysis should be run. Then set the thresholds for the analysis (default ones are provided that were chosen after different tests).
4. Hit "Start Analysis" and enjoy!

This Software is still work in progress, therefore some bugs can occur.

Currently only exports of the YourKit Version  2016.02 - b46 are supported.
