import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *

# The ASKALON Invmod workflow

def invmod(runs, params, iterations, scale):
    def mkdist(dist):
        def wrapper():
            return scale*dist()
        return wrapper
    
    # Create runtime distributions
    # TODO Determine what the actual distributions should be
    ddetermine_params = mkdist(UniformDistribution(1,100))
    druna = mkdist(UniformDistribution(1,100))
    drunb = mkdist(UniformDistribution(1,100))
    drunb2c = mkdist(UniformDistribution(1,100))
    drund = mkdist(UniformDistribution(1,100))
    dfindbest = mkdist(UniformDistribution(1,100))
    
    w = Workflow(name="invmod", description="""Invmod workflow from Nadeem and Fahringer""")
    
    determine_params = Job(id="determine_params", namespace="invmod", name="Determine Parameters", runtime=ddetermine_params()*SECONDS)
    w.addJob(determine_params)
    
    lastjobs = []
    for i in range(0,runs):
        
        runa = Job(id="runa_%d"%i, namespace="invmod", name="iWasimRunA", runtime=druna()*SECONDS, parents=[determine_params])
        w.addJob(runa)
        
        runb2c = runa
        for j in range(0, iterations):
            
            paramlist = []
            for k in range(0, params):
                runb = Job(id="runb_%d_%d_%d"%(i,j,k), namespace="invmod", name="iWasimRunB", runtime=drunb()*SECONDS, parents=[runb2c])
                w.addJob(runb)
                
                paramlist.append(runb)
            
            runb2c = Job(id="runb2c_%d_%d"%(i,j), namespace="invmod", name="iWasimRunB2C", runtime=drunb2c()*SECONDS, parents=paramlist)
            w.addJob(runb2c)
        
        # FABORABLE? test goes here, but not allowed in DAG
        rund = Job(id="rund_%d_%d"%(i,j), namespace="invmod", name="iWasimRunD", runtime=drund()*SECONDS, parents=[runb2c])
        w.addJob(rund)
        
        lastjobs.append(rund)
    
    findbest = Job(id="stage_out", namespace="invmod", name="iFindBest", runtime=dfindbest()*SECONDS, parents=lastjobs)
    w.addJob(findbest)
    
    return w

def main(*args):
    class Application(Main):
        def setoptions(self, parser):
            self.parser.add_option("-r", "--runs", dest="runs", metavar="n", type="int", default=5,
                help="Number of parallel runs [default: %default]")
            self.parser.add_option("-p", "--params", dest="params", metavar="n", type="int", default=5,
                help="Number of parameters [default: %default]")
            self.parser.add_option("-i", "--iterations", dest="iterations", metavar="n", type="int", default=1,
                help="Number of iterations of convergence loop [default: %default]")
            self.parser.add_option("-s", "--scale", dest="scale", metavar="s", type="float", default=1.0,
                help="Runtime scale [default: %default]")
        
        def genworkflow(self, options):
            return invmod(options.runs, options.params, options.iterations, options.scale)
    
    Application().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
