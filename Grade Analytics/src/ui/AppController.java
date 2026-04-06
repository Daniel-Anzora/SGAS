
package ui;

//imports classes from Backend packages(data, selection, and experiments)
import data.DataService;
import data.Dataset;
import data.DatasetType;
import selection.SelectionRequest;
import selection.SelectionResult;
import selection.SelectionService;
import experiments.ExperimentService;

public class AppController {
		//Service attributes
		private DataService data;
		private SelectionService selection;
		private ExperimentService experiments;
		
		//Contructor
		public AppController() {
			data = new DataService();
			selection = new SelectionService();
			experiments = new ExperimentService();
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
	    public Dataset generateDataset(DatasetType type, int size, long seed) {
	        return data.generate(type, size, seed);
	    }
	    //Runs performance experiments comparing algorithms
	    public ExperimentService getExperimentService() {
	        return experiments;
	    }
}
