package cache_analysis;

import analysis.Analysis;
import cache_data.*;

public class CacheAnalysis extends Analysis<CacheVertexValue, CacheMsg> {

    @Override
    public void setAnalysisConf(){
      tool = new CacheTool(); 
      fact = new CacheState(); 
      msg = new CacheMsg();
    }
}