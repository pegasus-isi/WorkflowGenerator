import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from generator import *
import math

# A simple fork-join workflow

def forkjoin(N, alpha, runtimeDist, sizeDist):
    # Width of workflow
    W = int(math.ceil(N/float(alpha)))
    
    # Number of levels
    L = int(math.ceil(N/float(W+1)))
    
    w = Workflow(name="forkjoin", description="""Fork-Join workflow (Figure 2b in Rahman et al)""")
    
    lastlevel = [File(name="infile.dat", size=sizeDist()*GB)]
    
    for i in range(0,L):
        fork = Job(id="task_%d"%i, namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=lastlevel)
        w.addJob(fork)
        
        lastlevel = []
        for j in range(0,W):
            tin = File(name="task_%d_%d_in.dat", size=sizeDist()*GB)
            tout = File(name="task_%d_%d_out.dat", size=sizeDist()*GB)
            
            fork.addOutput(tin)
            lastlevel.append(tout)
            
            j = Job(id="task_%d_%d"%(i,j), namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=[tin], outputs=[tout])
            w.addJob(j)
    
    outputs = [File(name="outfile.dat", size=sizeDist()*GB)]
    join = Job(id="join", namespace="parallel", name="Task", runtime=runtimeDist()*SECONDS, inputs=lastlevel, outputs=outputs)
    w.addJob(join)
    
    return w

def main(*args):
    class ForkJoin(Main):
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
            return forkjoin(options.N, options.alpha, 
                UniformDistribution(options.rtlow, options.rthigh), 
                UniformDistribution(options.slow, options.shigh))
    
    ForkJoin().main(*args)

if __name__ == '__main__':
    main(*sys.argv[1:])
