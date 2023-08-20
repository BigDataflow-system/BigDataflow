# README

[TOC]

## Getting started

The material fse23 includes the following four parts:

- `code`: The code of `BigDataflow` and` BigDataflow-classic`.
- `scripts`: All scripts of three tools in the experiments for preparing the control flow graphs, compiling, and running the client analyses.
- `input`: The control flow graphs for 2 client analyses.
- `output`:The results of 3 tools, including their logs and collected memory statistics of the clusters.


**input**

The [`input`](https://figshare.com/articles/dataset/material_fse23_zip/21971945/3) directory consists of 2 types of control flow graphs (CFG). The directory `PA`  and `PB` contain several CFGs of subjects for the alias analysis and cache analysis, respectively. We do not include all the CFGS as their total size is more than 200GB which exceeds both the FigShare's and GitHub's file size limit, 20GB and 100.00 MB, respectively. We will provide a way for obtaining these large-scale graphs in the future if they are needed.



**output**

The `output` directory is as the following two parts.

- `log`: The directories `P1_logs`  and `P2_logs` contain several logs of the `BigDataflow` and `BigDataflow-classic` tools when executing on the cloud, respectively. `P3_logs` contains the logs of `Chianina` on local machines.
- `statistics`: This directory contains all the memory usages of  `BigDataflow` and `BigDataflow-classic`. The time slice of a specific analysis on a CFG instance is got by checking the end of the log. Next, its memory usage can be extracted based on the time slice.

**code**

As the tool Chianina can be obtained from their public link, the `output` directory consists of the following two parts.

- `BigDataflow`: The code of BigDataflow in java under the branch **main**. All of its APIs are under the directory `Analysis/src/main/java/`.
- `BigDataflow-classic`: The code of  BigDataflow-classic in java under the branch **naive**. 

You can install them according to the `README.md` files under the directories, respectively. Note that, if you would like to run them on the cloud (e.g. EMR of Alibaba Cloud), you need to download the memory statistics from your EMR console first. Next, you can calculate the memory usage of a specific analysis on a CFG instance based on its time slice.

**scripts**

- `P1_scripts`: The scripts for running cache analysis of `BigDataflow-classic` on the cloud.
- `P2_scripts`: The scripts for running alias analysis of `BigDataflow-classic` on the cloud.

Notice that you can also write the scripts based on the README file under the `code` directories of 2 tools, respectively. We recommend this way as you can take up with the HDFS working environment step by step.
