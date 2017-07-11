package asl.sensor.experiment;

import asl.sensor.input.DataStore;

public class RandDebugExperiment extends RandomizedExperiment {

  public final boolean SKIP_SOLVING = true;
  
  @Override
  protected void backend(DataStore ds) {
    super.backend(ds);
  }
  
  @Override
  public boolean getSolverState() {
    return SKIP_SOLVING;
  }

}
