# SGAS
CSC-401 group project for educational purposes 

//How to run code?

Open the SGAS.Jar file located in SGAS\Grade Analytics folder.

Alternatively in the terminal navigate to the SGAS\Grade Analytics folder and type java -cp out engine.main.SGASMain

//how to reproduce experiment?

There is a load CSV button to load a batch of data containing student names and scores.
    The CSV is located in the Documentation folder with file name sgademo.csv
Alternatively a manual selection can be inputed in the box below labelled manual entry.

Input a K and then click run Selection. A value, sorttime and quicktime will be provided. 

In the Batch experiment box, pick sizes, repeats, seed and a data type then click run batch.

The batch results will be outputed into a CSV file named results.CSV.
    The file is located in Grade Analytics folder.

//What each major file/function do?

DataService: loads a CSV file and generates data from it.

DataSet: packages the data and passes them between services.

DatasetType: restricts data into 4 choices (Random, sorted, reverse sorted, duplicates) and passes a datasetType into other files/functions.

BatchAggregatedRow: stores data for one results and stores average number for multiple experiment.

BatchRequest: is the input configuration object for running a batch experiment. 

BatchSummary: tells the UI or controller the location of the CSV.

CsvExporter: exports the data into a CSV file.

ExperimentService: validate inpu, generate data, average the results then export results.

SGASMain: Starts the GUI

MethodChoice: Specifies which algorithm path to run (Sort, QuickSelect, Both).

PivotStrategy: Tells quickselect how to choose its pivot index. 

QuickselectSelector: runs quickselect to find the k-th smallest value.

SelectionMode: Defines how the target index or value is chosen. 

SelectionRequest: input config (mode, method, pivot, k, percentile) for one experiment/run. 

SelectionResult: Output object from an experiment/run. 

SelectionService: Runs algorithm based on selection then returns results.

SortSelector: Fully sorts the array then finds the kth smallest element.

Stats: Tracks performance (excution time, element comparisons and number of swaps).


Limitations: Memory usage was not a focus of this project, so Quickselect recursive calls consume additional stack space, memory usage or cache behavior was not measured or compared. Quickselect struggles in duplicate heavy large dataset leading to O(n^2) efficiency.   

Runtime: In a dataset of 10k runtime is expected on average for heapsort to be 939340ns (.94ms) and for quickselect to be 98120ns (.1ms). 

Memory usage: Memory usage or comparisons between heapsort and quickselect were not conducted. 
