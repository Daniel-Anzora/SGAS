# SGAS
CSC-401 group project for educational purposes 

//How to run code?

Make sure to change directory to the correct folder and type java -cp bin engine.main.SGASMain in the terminal to run the program.

//how to reproduce experiment?

There is a load CSV button to load a batch of data containing student names and scores.
    The CSV is located in the Documentation folder with file name sgademo.csv

Input a K and then click run Selection, a value, sorttime and quicktime will be provided. 

In the next row below pick sizes, repeats, seed and a data type then click run batch.

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