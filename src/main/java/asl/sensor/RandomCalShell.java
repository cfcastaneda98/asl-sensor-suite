package asl.sensor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import asl.sensor.experiment.RandomizedExperiment;
import asl.sensor.input.DataBlock;
import asl.sensor.input.DataStore;
import asl.sensor.input.InstrumentResponse;
import asl.sensor.utils.TimeSeriesUtils;

public class RandomCalShell {

  /**
   * Shell interface for sensor suite, runnable from command line
   * @param args
   */
  public static void main(String[] args) {
    // TODO parse arguments and set up data structures to run operations
    
    // command line arg structure, by index:
    // 0. denotes whether or not calibration is high or low period
    // 1. location of file with cal input data
    // 2. location of file with sensor output
    // 3. location of RESP of sensor
    // 4. start time to trim data down to (assume ddd.hh:mm:ss.ms format)
    // 5. end time to trim data down to (same format)
    // 6. where to save output data
    
    RandomizedExperiment re = new RandomizedExperiment();
    if ( args[0].toUpperCase().startsWith("H") ) {
      re.setLowFreq(false);
    } else {
      re.setLowFreq(true);
    }
    
    try {
      DataStore ds = new DataStore();
      String calFilt = TimeSeriesUtils.getMplexNameList(args[1]).get(0);
      DataBlock calBlock = TimeSeriesUtils.getTimeSeries(args[1], calFilt);
      String outFilt = TimeSeriesUtils.getMplexNameList(args[2]).get(0);
      DataBlock outBlock = TimeSeriesUtils.getTimeSeries(args[2], outFilt);
      InstrumentResponse ir = new InstrumentResponse(args[3]);
      ds.setData(0, calBlock);
      ds.setData(1, outBlock);
      ds.setResponse(1, ir);
      
      SimpleDateFormat sdf = new SimpleDateFormat("D.H:m:s.S");
      sdf.setTimeZone( TimeZone.getTimeZone("UTC") );
      
      // TODO: may need to create calendar by using sdf to parse date from args
      long start = Long.parseLong(args[4]) * 1000;
      long end = Long.parseLong(args[5]) * 1000;
      
      ds.trimAll(start, end);
      
      re.runExperimentOnData(ds);
      
      // TODO: get specs on output format and deliver that
      
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // what sort of autocorrection
  }

}
