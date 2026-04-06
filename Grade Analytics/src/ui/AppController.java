
package ui;

//imports classes from Backend packages(data, selection, and experiments)
import engine.data.DataService;
import engine.data.Dataset;
import engine.data.DatasetType;
import engine.selection.SelectionRequest;
import engine.selection.SelectionResult;
import engine.selection.SelectionService;
import engine.experiments.BatchRequest;
import engine.experiments.BatchSummary;
import engine.experiments.ExperimentService;

public class AppController {
	//Service attributes
	private DataService data;
	private SelectionService selection;
	private ExperimentService experiments;
    
	
	//Contructor
	public AppController() {
		data = new DataService();
		selection = new SelectionService();
		experiments = new ExperimentService(data, selection);
	}
	//methods:
	//Runs SelectionResult
    public SelectionResult runSelection(SelectionRequest req, Dataset ds) {
        return selection.run(req, ds);
    }
    //Loads a dataset from a file
    public Dataset loadDataset(String path) {
        return data.loadCsv(path);
    }
	//Generates a dataset
    public Dataset generateDataset(engine.data.DatasetType type, int size, long seed) {
        return data.generate(type, size, seed);
    }
    /** Runs a batch experiment and exports averaged metrics to CSV. */
    public BatchSummary runBatch(BatchRequest req) {
        return experiments.run(req);
    }

    public ExperimentService getExperimentService() {
        return experiments;
    }
}
