import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

# The ASKALON MeteoAG workflow

def meteoag(cases, scale):
    def mkdist(dist):
        def wrapper():
            return scale*dist()
        return wrapper
    
    # Create runtime distributions
    # TODO Determine what the actual distributions should be
    dinit = mkdist(UniformDistribution(1,100))
    dcase_init = mkdist(UniformDistribution(1,100))
    drams_makevfile = mkdist(UniformDistribution(1,100))
    drams_init = mkdist(UniformDistribution(1,100))
    drevu_compare = mkdist(UniformDistribution(1,100))
    draver = mkdist(UniformDistribution(1,100))
    drams_hist = mkdist(UniformDistribution(1,100))
    drevu_dump = mkdist(UniformDistribution(1,100))
    dstageout = mkdist(UniformDistribution(1,100))
    
    w = Workflow(name="meteoag", description="""MeteoAG workflow from Nadeem and Fahringer""")
    
    init = Job(id="simulation_init", namespace="meteoag", name="simulation_init", runtime=dinit()*SECONDS)
    w.addJob(init)
    
    lastjobs = []
    for i in range(0,cases):
        
        case_init = Job(id="case_init_%d"%i, namespace="meteoag", name="case_init", runtime=dcase_init()*SECONDS, parents=[init])
        w.addJob(case_init)
        
        rams_makevfile = Job(id="rams_makevfile_%d"%i, namespace="meteoag", name="rams_makevfile", runtime=drams_makevfile()*SECONDS, parents=[case_init])
        w.addJob(rams_makevfile)
        
        rams_init = Job(id="rams_init_%d"%i, namespace="meteoag", name="rams_init", runtime=drams_init()*SECONDS, parents=[rams_makevfile])
        w.addJob(rams_init)
        
        revu_compare = Job(id="revu_compare_%d"%i, namespace="meteoag", name="revu_compare", runtime=drevu_compare()*SECONDS, parents=[rams_init])
        w.addJob(revu_compare)
        
        raver = Job(id="raver_%d"%i, namespace="meteoag", name="raver", runtime=draver()*SECONDS, parents=[revu_compare])
        w.addJob(raver)
        
        # CONTINUE? test goes here, but it isn't allowed in a DAG
        
        rams_hist = Job(id="rams_hist_%d"%i, namespace="meteoag", name="rams_hist", runtime=drams_hist()*SECONDS, parents=[raver])
        w.addJob(rams_hist)
        
        revu_dump = Job(id="revu_dump_%d"%i, namespace="meteoag", name="revu_dump", runtime=drevu_dump()*SECONDS, parents=[rams_hist])
        w.addJob(revu_dump)
        
        lastjobs.append(revu_dump)
    
    so = Job(id="stage_out", namespace="meteoag", name="StageOut", runtime=dstageout()*SECONDS, parents=lastjobs)
    w.addJob(so)
    
    return w

def main(*args):
    class Application(Main):
        def setoptions(self, parser):
            self.parser.add_option("-c", "--cases", dest="cases", metavar="n", type="int", default=2,
                help="Number of cases [default: %default]")
            self.parser.add_option("-s", "--scale", dest="scale", metavar="s", type="float", default=1.0,
                help="Runtime scale [default: %default]")
        
        def genworkflow(self, options):
            return meteoag(options.cases, options.scale)
    
    Application().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
