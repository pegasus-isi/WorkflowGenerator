import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *
import math

# A simple parallel chains workflow

def parallel(N, alpha, runtimeDist, sizeDist):
    # Width of workflow
    W = int(math.ceil(N/float(alpha)))
    
    # Number of levels
    L = int(math.ceil((N-2)/float(W)))
    
    w = Workflow(name="parallel", description="""Parallel workflow (Figure 2a in Rahman et al)""")
    
    sourcein = File(name="source_in.dat", size=sizeDist()*GB)
    sourceout = File(name="source_out.dat", size=sizeDist()*GB)
    source = Job(id="task_s", namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=[sourcein], outputs=[sourceout])
    w.addJob(source)
    
    lastlevel = []
    for i in range(0,W):
        tin = sourceout
        
        for j in range(0,L):
            tout = File(name="%d_%d.out"%(i,j), size=sizeDist()*GB)
            j = Job(id="task_%d_%d"%(i,j), namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=[tin], outputs=[tout])
            w.addJob(j)
            tin = tout
        
        lastlevel.append(tout)
    
    sinkout = File(name="sink_out.dat", size=sizeDist()*GB)
    sink = Job(id="task_t", namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=lastlevel, outputs=[sinkout])
    w.addJob(sink)
    
    return w

def main(*args):
    class Parallel(Main):
        def setoptions(self, parser):
            self.parser.add_option("-N", "--numjobs", dest="N", metavar="N", type="int", default=50,
                help="Number of jobs in workflow [default: %default]")
            self.parser.add_option("-a", "--alpha", dest="alpha", metavar="A", type="float", default=5,
                help="Ratio of number of jobs to width (e.g. W = ceil(N/alpha)) [default: %default]")
            
            self.parser.add_option("", "--rtlow", dest="rtlow", metavar="t", type="float", default=100,
                help="Lower bound on runtime in seconds [default: %default]")
            self.parser.add_option("", "--rthigh", dest="rthigh", metavar="t", type="float", default=500,
                help="Upper bound on runtime in seconds [default: %default]")
            
            self.parser.add_option("", "--slow", dest="slow", metavar="t", type="float", default=1,
                help="Lower bound on file size in GB [default: %default]")
            self.parser.add_option("", "--shigh", dest="shigh", metavar="t", type="float", default=5,
                help="Upper bound on file size in GB [default: %default]")
            
        
        def genworkflow(self, options):
            return parallel(options.N, options.alpha, 
                UniformDistribution(options.rtlow, options.rthigh), 
                UniformDistribution(options.slow, options.shigh))
    
    Parallel().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
